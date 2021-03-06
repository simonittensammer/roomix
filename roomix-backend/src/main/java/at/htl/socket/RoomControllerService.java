package at.htl.socket;

import at.htl.control.*;
import at.htl.dto.ChatMessageDTO;
import at.htl.dto.PlaySongMessageDTO;
import at.htl.dto.SkipVoteAmountDTO;
import at.htl.dto.SocketMessageDTO;
import at.htl.entity.*;
import at.htl.observers.MemberRepositoryObserver;
import at.htl.observers.PlaylistControllerObserver;
import at.htl.observers.PlaylistRepositoryObserver;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.websocket.Session;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Transactional
@ApplicationScoped
public class RoomControllerService implements PlaylistControllerObserver, PlaylistRepositoryObserver, MemberRepositoryObserver {

    private static final Logger LOGGER = Logger.getLogger("RoomControllerService");

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private PlaylistRepository playlistRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    public void init() {
        memberRepository.addObserver(this);
        playlistRepository.addObserver(this);
    }

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private Map<Long, Map<String, User>> members = new ConcurrentHashMap<>();
    private Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private Map<Long, PlaylistController> playlistControllers = new ConcurrentHashMap<>();
    private Map<Long, Integer> skipVotes = new ConcurrentHashMap<>();

    public void addSession(Session session) {
        try {
            String username = session.getUserProperties().get("username").toString();
            Long roomId = Long.valueOf(session.getUserProperties().get("roomId").toString());

            User user = executor.submit(() -> userRepository.findByName(username)).get();
            Room room = executor.submit(() -> roomRepository.findById(roomId)).get();

            if (!rooms.containsKey(roomId)) {
                rooms.put(roomId, room);
                members.put(roomId, new ConcurrentHashMap<>());
                skipVotes.put(roomId, 0);
                playlistControllers.put(roomId, new PlaylistController(roomId, room.getPlaylist()));
                playlistControllers.get(roomId).addObserver(this);
                System.out.println("here 4");
            }

            members.get(roomId).put(username, user);
            sessions.put((String) session.getUserProperties().get("username"), session);

            messageSession(session, new SocketMessageDTO("update-members", null));

            messageSession(session, new SocketMessageDTO(
                    "new-song",
                    new PlaySongMessageDTO(
                            playlistControllers.get(roomId).getCurrentSong(),
                            playlistControllers.get(roomId).getCurrentSongTime()
                    )
            ));

            broadcast(roomId, new SocketMessageDTO(
                    "skip-vote",
                    new SkipVoteAmountDTO(
                            skipVotes.get(roomId),
                            (int) Math.ceil((float) members.get(roomId).size() / 2)
                    )
            ));

            //printStatus();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removeSession(Session session) throws ExecutionException, InterruptedException {
        String username = session.getUserProperties().get("username").toString();
        Long roomId = Long.valueOf(session.getUserProperties().get("roomId").toString());

        members.get(roomId).remove(username);
        sessions.remove(username);

        if (members.get(roomId).size() == 0) {
            members.remove(roomId);
            rooms.remove(roomId);
            skipVotes.remove(roomId);
            playlistControllers.get(roomId).getSongTimer().cancel();
            playlistControllers.get(roomId).removeObserver(this);
            playlistControllers.remove(roomId);
        } else {
            checkForSkip(roomId, skipVotes.get(roomId));
        }
    }

    public void chatMessage(String message, Long roomId, String username) throws ExecutionException, InterruptedException {

        User user = members.get(roomId).get(username);
        Room room = rooms.get(roomId);
        Message chatMessage = new Message(username, room, message);

        managedExecutor.submit(() -> messageRepository.persist(chatMessage)).get();

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(username, chatMessage.getCreationDate(), message);
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO("chat-message", chatMessageDTO);

        broadcast(roomId, socketMessageDTO);

    }

    public void skipVote(Long roomId, boolean vote) throws ExecutionException, InterruptedException {

        int votes = skipVotes.get(roomId);

        if (vote) votes += 1;
        else votes -= 1;

        checkForSkip(roomId, votes);
    }

    private void checkForSkip(Long roomId, int votes) throws ExecutionException, InterruptedException {
        int amountNeeded = (int) Math.ceil((float) members.get(roomId).size() / 2);

        if (amountNeeded <= votes) {
            managedExecutor.submit(() -> playlistControllers.get(roomId).skipSong()).get();
            votes = 0;
        }
        skipVotes.put(roomId, votes);

        SocketMessageDTO socketMessageDTO = new SocketMessageDTO("skip-vote", new SkipVoteAmountDTO(votes, (amountNeeded)));
        broadcast(roomId, socketMessageDTO);
    }

    private void broadcast(Long roomId, SocketMessageDTO message) {
        Jsonb jsonb = JsonbBuilder.create();
        sessions.values().forEach(session -> {
            if (session.getUserProperties().get("roomId") == roomId) {
                messageSession(session, message);
            }
        });
    }

    private void messageSession(Session session, SocketMessageDTO message) {
        Jsonb jsonb = JsonbBuilder.create();
        LOGGER.info("messaging session(" + session.getUserProperties().get("username") + ", " + session.getUserProperties().get("roomId") + "): " + message);
        session.getAsyncRemote().sendObject(jsonb.toJson(message), result -> {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }

    private void printStatus() {
        List<String> sessionsStrings = new LinkedList<>();
        List<String> roomsStrings = new LinkedList<>();
        List<String> membersStrings = new LinkedList<>();
        List<String> playlistControllersStrings = new LinkedList<>();

        sessions.entrySet().stream().forEach(o -> {
            sessionsStrings.add("key=" + o.getKey() + ", value=session(" + o.getValue().getUserProperties().get("username") + ", " + o.getValue().getUserProperties().get("roomId") + ")");
        });

        rooms.entrySet().stream().forEach(o -> {
            roomsStrings.add("key=" + o.getKey() + ", value=" + o.getValue());
        });

        members.entrySet().stream().forEach(o -> {
            List<String> usersString = o.getValue().entrySet().stream().map(o2 -> {
                return "(key=" + o2.getKey() + ", value=" + o2.getValue().getUsername() + ")";
            }).collect(Collectors.toList());
            membersStrings.add("key=" + o.getKey() + ", value=" + String.join(", ", usersString));
        });

        playlistControllers.entrySet().stream().forEach(o -> {
            playlistControllersStrings.add("key=" + o.getKey() + ", value=playlistController(roomId=" + o.getValue().getRoomId() + ", playlistId=" + o.getValue().getPlaylist().getId() + ")");
        });

        LOGGER.info("\n######################" +
                "\nsessions:\n\t" + String.join("\n\t", sessionsStrings) +
                "\nrooms:\n\t" + String.join("\n\t", roomsStrings) +
                "\nmembers:\n\t" + String.join("\n\t", membersStrings) +
                "\nplaylistControllers:\n\t" + String.join("\n\t", playlistControllersStrings) +
                "\n######################\n");
    }

    @Override
    public void newSong(Long roomId) {

        broadcast(
                roomId,
                new SocketMessageDTO(
                        "new-song",
                        new PlaySongMessageDTO(
                                playlistControllers.get(roomId).getCurrentSong(),
                                playlistControllers.get(roomId).getCurrentSongTime()
                        )
                )
        );

        playlistRepository.changeCurrentSong(roomId, playlistControllers.get(roomId).getCurrentSong());
    }

    @Override
    public void addSong(Long roomId, Song song) {
        if (playlistControllers.get(roomId) != null) {

            broadcast(roomId, new SocketMessageDTO("add-song", song));

            playlistControllers.get(roomId).getPlaylist().getSongList().add(song);
            playlistControllers.get(roomId).updatePlaylist();
        }
    }

    @Override
    public void removeSong(Long roomId, Song song) {
        if (playlistControllers.get(roomId) != null) {

            broadcast(roomId, new SocketMessageDTO("remove-song", song));

            int index = -1;
            for (int i = 0; i < playlistControllers.get(roomId).getPlaylist().getSongList().size(); i++) {
                if (playlistControllers.get(roomId).getPlaylist().getSongList().get(i).getId() == song.getId()) {
                    index = i;
                }
            }
            playlistControllers.get(roomId).getPlaylist().getSongList().remove(index);
            LOGGER.info("length=" + playlistControllers.get(roomId).getPlaylist().getSongList().size());
            playlistControllers.get(roomId).updatePlaylist();

            if (playlistControllers.get(roomId).getPlaylist().getSongList().size() == 0) {
                broadcast(roomId, new SocketMessageDTO("stop", null));
            }
        }
    }

    @Override
    public void update(Long roomId) {
        System.out.println("update members");
        SocketMessageDTO socketMessageDTO = new SocketMessageDTO("update-members", null);
        broadcast(roomId, socketMessageDTO);
    }
}

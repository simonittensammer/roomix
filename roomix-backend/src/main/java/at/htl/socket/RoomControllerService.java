package at.htl.socket;

import at.htl.control.*;
import at.htl.dto.ChatMessageDTO;
import at.htl.dto.PlaySongMessageDTO;
import at.htl.dto.SocketMessageDTO;
import at.htl.entity.*;
import at.htl.observers.PlaylistControllerObserver;
import at.htl.observers.PlaylistRepositoryObserver;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
public class RoomControllerService implements PlaylistControllerObserver, PlaylistRepositoryObserver {

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
    ManagedExecutor managedExecutor;

    @Inject
    public void init() {
        playlistRepository.addObserver(this);
    }

    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private Map<Long, Map<String, User>> members = new ConcurrentHashMap<>();
    private Map<Long, Room> rooms = new ConcurrentHashMap<>();
    private Map<Long, PlaylistController> playlistControllers = new ConcurrentHashMap<>();

    public void addSession(Session session) {
        try {
            String username = session.getUserProperties().get("username").toString();
            Long roomId = Long.valueOf(session.getUserProperties().get("roomId").toString());

            User user = executor.submit(() -> userRepository.findByName(username)).get();
            Room room = executor.submit(() -> roomRepository.findById(roomId)).get();

            if (!rooms.containsKey(roomId)) {
                rooms.put(roomId, room);
                members.put(roomId, new ConcurrentHashMap<>());
                playlistControllers.put(roomId, new PlaylistController(roomId, room.getPlaylist()));
                playlistControllers.get(roomId).addObserver(this);
                System.out.println("here 4");
            }

            members.get(roomId).put(username, user);
            sessions.put((String) session.getUserProperties().get("username"), session);

            messageSesseion(session, playlistControllers.get(roomId).getCurrentSongMessage());

            printStatus();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removeSession(Session session) {
        String username = session.getUserProperties().get("username").toString();
        Long roomId = Long.valueOf(session.getUserProperties().get("roomId").toString());

        members.get(roomId).remove(username);
        sessions.remove(username);

        if (members.get(roomId).size() == 0) {
            members.remove(roomId);
            rooms.remove(roomId);
            playlistControllers.get(roomId).getSongTimer().cancel();
            playlistControllers.get(roomId).removeObserver(this);
            playlistControllers.remove(roomId);
        }
    }

    public void chatMessage(String message, Long roomId, String username) {


        try {
            User user = members.get(roomId).get(username);
            Room room = rooms.get(roomId);
            Message chatMessage = new Message(username, room, message);

            managedExecutor.submit(() -> messageRepository.persist(chatMessage)).get();

            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(username, chatMessage.getCreationDate(), message);
            SocketMessageDTO socketMessageDTO = new SocketMessageDTO("chat-message", chatMessageDTO);

            broadcast(roomId, socketMessageDTO);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    private void broadcast(Long roomId, SocketMessageDTO message) {
        Jsonb jsonb = JsonbBuilder.create();
        sessions.values().forEach(s -> {
            if (s.getUserProperties().get("roomId") == roomId) {
                LOGGER.info("messaging session(" + s.getUserProperties().get("username") + ", " + s.getUserProperties().get("roomId") + "): " + message);
                s.getAsyncRemote().sendObject(jsonb.toJson(message), result ->  {
                    if (result.getException() != null) {
                        System.out.println("Unable to send message: " + result.getException());
                    }
                });
            }
        });
    }

    private void messageSesseion(Session session, String message) {
        LOGGER.info("messaging session(" + session.getUserProperties().get("username") + ", " + session.getUserProperties().get("roomId") + "): " + message);
        session.getAsyncRemote().sendObject(message, result ->  {
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
            sessionsStrings.add("key=" + o.getKey() + ", value=session(" + o.getValue().getUserProperties().get("username") + ", " +o.getValue().getUserProperties().get("roomId") + ")");
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
            LOGGER.info("length="+playlistControllers.get(roomId).getPlaylist().getSongList().size());
            playlistControllers.get(roomId).updatePlaylist();

            if (playlistControllers.get(roomId).getPlaylist().getSongList().size() == 0) {
                broadcast(roomId, new SocketMessageDTO("stop", null));
            }
        }
    }
}

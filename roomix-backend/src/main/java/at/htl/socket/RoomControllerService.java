package at.htl.socket;

import at.htl.control.PlaylistRepository;
import at.htl.control.RoomRepository;
import at.htl.control.UserRepository;
import at.htl.entity.Playlist;
import at.htl.entity.Room;
import at.htl.entity.User;
import at.htl.observers.PlaylistControllerObserver;
import at.htl.observers.PlaylistRepositoryObserver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
    private PlaylistRepository playlistRepository;

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

            messageSesseion(session, playlistControllers.get(roomId).getCurrentSong());

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

    private void broadcast(Long roomId, String message) {
        sessions.values().forEach(s -> {
            if (s.getUserProperties().get("roomId") == roomId) {
                LOGGER.info("messaging session(" + s.getUserProperties().get("username") + ", " + s.getUserProperties().get("roomId") + "): " + message);
                s.getAsyncRemote().sendObject(message, result ->  {
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
        broadcast(roomId, playlistControllers.get(roomId).getCurrentSong());
    }

    @Override
    public void updatePlaylist(Long id, Playlist playlist) {
        playlistControllers.get(id).setPlaylist(playlist);
    }
}

package at.htl.socket;

import at.htl.control.RoomRepository;
import at.htl.control.UserRepository;
import at.htl.entity.Room;
import at.htl.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.websocket.Session;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.*;

@Transactional
@ApplicationScoped
public class RoomControllerService implements PlaylistObserver {

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoomRepository roomRepository;

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
                playlistControllers.get(roomId).addBidObserver(this);
            }

            members.get(roomId).put(username, user);
            sessions.put((String) session.getUserProperties().get("username"), session);

            messageSesseion(session, playlistControllers.get(roomId).getCurrentSong());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removeSession(Session session) {
        String username = session.getUserProperties().get("username").toString();
        Long roomId = Long.valueOf(session.getUserProperties().get("roomId").toString());

        members.get(roomId).remove(username);
        sessions.remove(username);
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    private void messageSesseion(Session session, String message) {
        session.getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }

    @Override
    public void newSong(Long roomId) {
        broadcast(playlistControllers.get(roomId).getCurrentSong());
    }
}

package at.htl.socket;

import at.htl.control.RoomRepository;
import at.htl.entity.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.*;

@ServerEndpoint("/room/{roomId}/{username}")
@Transactional
@ApplicationScoped
public class RoomSocket {

    @Inject
    RoomControllerService roomControllerService;

    private static ExecutorService roomExecuter = Executors.newSingleThreadExecutor();

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Long roomId, @PathParam("username") String username) {
        sessions.put(username, session);

        session.getUserProperties().put("roomId", roomId);
        session.getUserProperties().put("username", username);

        roomControllerService.addSession(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") Long roomId, @PathParam("username") String username) {
        sessions.remove(username);
        roomControllerService.removeSession(session);
    }

    @OnError
    public void onError(Session session, @PathParam("roomId") Long roomId, @PathParam("username") String username, Throwable throwable) {
        System.out.println(throwable.toString());

//        sessions.remove(username);
//        rooms.get(roomId).removeSession(session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("roomId") Long roomId, @PathParam("username") String username) {

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
}

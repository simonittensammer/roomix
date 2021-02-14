package at.htl.socket;

import at.htl.control.RoomRepository;
import at.htl.dto.SocketMessageDTO;
import at.htl.entity.Message;
import at.htl.entity.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

@ServerEndpoint("/room/{roomId}/{username}")
@Transactional
@ApplicationScoped
public class RoomSocket {

    private static final Logger LOGGER = Logger.getLogger("RoomSocket");

    @Inject
    RoomControllerService roomControllerService;

    private static ExecutorService roomExecuter = Executors.newSingleThreadExecutor();

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Long roomId, @PathParam("username") String username) {
        LOGGER.info("session openend with roomId=" + roomId + ", username=" + username);

        sessions.put(username, session);

        session.getUserProperties().put("roomId", roomId);
        session.getUserProperties().put("username", username);

        roomControllerService.addSession(session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("roomId") Long roomId, @PathParam("username") String username) throws ExecutionException, InterruptedException {
        LOGGER.info("session closed with roomId=" + roomId + ", username=" + username);

        sessions.remove(username);
        roomControllerService.removeSession(session);
    }

    @OnError
    public void onError(Session session, @PathParam("roomId") Long roomId, @PathParam("username") String username, Throwable throwable) {
        LOGGER.throwing(this.getClass().getSimpleName(), "onError", throwable);

//        sessions.remove(username);
//        rooms.get(roomId).removeSession(session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("roomId") Long roomId, @PathParam("username") String username) throws ExecutionException, InterruptedException {
        Jsonb jsonb = JsonbBuilder.create();
        SocketMessageDTO socketMessageDTO = jsonb.fromJson(message, SocketMessageDTO.class);

        switch (socketMessageDTO.getType()) {
            case "chat-message":
                roomControllerService.chatMessage(socketMessageDTO.getMessage().toString(), roomId, username);
                break;
            case "skip-song":
                roomControllerService.skipVote(roomId, (boolean) socketMessageDTO.getMessage());
                break;
            default:
                break;
        }
    }

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    LOGGER.info("Unable to send message: " + result.getException());
                }
            });
        });
    }
}

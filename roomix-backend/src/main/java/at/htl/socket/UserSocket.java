package at.htl.socket;

import at.htl.control.FriendRequestRepository;
import at.htl.control.RoomInviteRepository;
import at.htl.dto.SocketMessageDTO;
import at.htl.observers.FriendRequestRepositoryObserver;
import at.htl.observers.RoomInviteRepositoryObserver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ServerEndpoint("/user/{username}")
@Transactional
@ApplicationScoped
public class UserSocket implements FriendRequestRepositoryObserver, RoomInviteRepositoryObserver {

    @Inject
    FriendRequestRepository friendRequestRepository;

    @Inject
    RoomInviteRepository roomInviteRepository;

    @Inject
    public void init() {
        friendRequestRepository.addObserver(this);
        roomInviteRepository.addObserver(this);
    }

    private static final Logger LOGGER = Logger.getLogger("UserSocket");

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        LOGGER.info("user " + username + " connected");
    }

    @OnClose
    public void onClose(@PathParam("username") String username) {
        sessions.remove(username);
        LOGGER.info("user " + username + " disconnected");
    }

    @OnError
    public void onError(@PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        LOGGER.throwing(this.getClass().getSimpleName(), "onError", throwable);
    }

    @Override
    public void sendFriendRequest(String receiverKey) {
        SocketMessageDTO message = new SocketMessageDTO("receive-friend-request", null);
        Session receiver = sessions.get(receiverKey);

        LOGGER.info("friend-request sent to " + receiverKey);

        if (receiver != null) {
            sendMessage(receiver, message);
        }

    }

    @Override
    public void respondToFriendRequest(String senderKey, String receiverKey) {
        SocketMessageDTO message = new SocketMessageDTO("friend-request-response", null);
        Session sender = sessions.get(senderKey);
        Session receiver = sessions.get(receiverKey);

        LOGGER.info(senderKey + " and " + receiverKey + " are now friends");

        if (sender != null) {
            sendMessage(sender, message);
        }

        if (receiver != null) {
            sendMessage(receiver, message);
        }
    }

    @Override
    public void sendRoomInvite(String receiverKey) {
        SocketMessageDTO message = new SocketMessageDTO("receive-room-invite", null);
        Session receiver = sessions.get(receiverKey);

        LOGGER.info("room-invite sent to " + receiverKey);

        if (receiver != null) {
            sendMessage(receiver, message);
        }
    }

    @Override
    public void respontToRoomInvite(String senderKey, String receiverKey, Long roomId) {
        SocketMessageDTO message = new SocketMessageDTO("room-invite-response", null);
        Session sender = sessions.get(senderKey);
        Session receiver = sessions.get(receiverKey);

        LOGGER.info(receiverKey + " is now part of rooom " + roomId);

        if (sender != null) {
            sendMessage(sender, message);
        }

        if (receiver != null) {
            sendMessage(receiver, message);
        }
    }

    private void sendMessage(Session session, SocketMessageDTO socketMessageDTO) {
        Jsonb jsonb = JsonbBuilder.create();
        String message = jsonb.toJson(socketMessageDTO);;

        session.getAsyncRemote().sendObject(message, result ->  {
            if (result.getException() != null) {
                LOGGER.info("Unable to send message: " + result.getException());
            }
        });
    }
}

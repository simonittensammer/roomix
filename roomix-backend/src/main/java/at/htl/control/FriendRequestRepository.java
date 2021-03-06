package at.htl.control;

import at.htl.dto.FriendRequestDTO;
import at.htl.entity.FriendRequest;
import at.htl.entity.User;
import at.htl.observers.FriendRequestRepositoryObserver;
import at.htl.observers.RoomInviteRepositoryObserver;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FriendRequestRepository implements PanacheRepository<FriendRequest> {

    @Inject
    UserRepository userRepository;

    private List<FriendRequestRepositoryObserver> observerList = new ArrayList<>();

    public void addObserver(FriendRequestRepositoryObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(FriendRequestRepositoryObserver observer) {
        observerList.remove(observer);
    }

    public void notifyObserversSend(String receiver) {
        observerList.forEach(friendRequestRepositoryObserver -> friendRequestRepositoryObserver.sendFriendRequest(receiver));
    }

    public void notifyObserversRespond(String sender, String receiver) {
        observerList.forEach(friendRequestRepositoryObserver -> friendRequestRepositoryObserver.respondToFriendRequest(sender, receiver));
    }

    public void notifyObserversUnfriend(String sender, String receiver) {
        observerList.forEach(friendRequestRepositoryObserver -> friendRequestRepositoryObserver.unfriendUsers(sender, receiver));
    }

    public JsonArray getSerializedFriendRequestList(String username) {
        User user = userRepository.findByName(username);

        JsonArrayBuilder friendRequstsJson = Json.createArrayBuilder();

        if (user != null) {
            List<FriendRequest> friendRequestList = user.getFriendRequestList();

            Jsonb jsonb = JsonbBuilder.create();

            friendRequestList.forEach(friendRequest -> {
                JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(friendRequest.getSender())));
                JsonObject senderJson = jsonReader.readObject();
                jsonReader = Json.createReader(new StringReader(jsonb.toJson(friendRequest.getReceiver())));
                JsonObject receiverJson = jsonReader.readObject();

                friendRequstsJson.add(
                        Json.createObjectBuilder()
                        .add("id", friendRequest.getId())
                        .add("creationDate", friendRequest.getCreationDate().toString())
                        .add("sender", senderJson)
                        .add("receiver", receiverJson)
                );
            });
        }

        return friendRequstsJson.build();
    }

    public FriendRequest sendFriendRequest(FriendRequestDTO friendRequestDTO) {
        User sender = userRepository.findByName(friendRequestDTO.getSender());
        User receiver = userRepository.findByName(friendRequestDTO.getReceiver());

        if (sender == null || receiver == null) return null;

        if (sender.getFriendList().contains(receiver)) return null;

        if (streamAll().anyMatch(friendRequest -> friendRequest.getSender().equals(sender) && friendRequest.getReceiver().equals(receiver))) return null;

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        persist(friendRequest);
        receiver.getFriendRequestList().add(friendRequest);

        notifyObserversSend(receiver.getUsername());

        return friendRequest;
    }

    public boolean respondToFriendRequest(Long friendRequestId, boolean response) {
        FriendRequest friendRequest = findById(friendRequestId);

        if (friendRequest == null) return false;

        User receiver = friendRequest.getReceiver();
        User sender = friendRequest.getSender();

        if (receiver == null || sender == null) return false;

        if (response) {
            sender.getFriendList().add(receiver);
            receiver.getFriendList().add(sender);
        }

        delete(friendRequest);
        receiver.getFriendRequestList().remove(friendRequest);

        notifyObserversRespond(sender.getUsername(), receiver.getUsername());

        return true;
    }

    public boolean unfriendUsers(FriendRequestDTO friendRequestDTO) {
        User sender = userRepository.findByName(friendRequestDTO.getSender());
        User receiver = userRepository.findByName(friendRequestDTO.getReceiver());

        if (sender == null || receiver == null) return false;

        if (!sender.getFriendList().contains(receiver)) return false;

        sender.getFriendList().remove(receiver);
        receiver.getFriendList().remove(sender);

        notifyObserversUnfriend(sender.getUsername(), receiver.getUsername());

        return true;
    }
}

package at.htl.control;

import at.htl.entity.FriendRequest;
import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.util.List;

@ApplicationScoped
public class FriendRequestRepository implements PanacheRepository<FriendRequest> {

    @Inject
    UserRepository userRepository;

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
}

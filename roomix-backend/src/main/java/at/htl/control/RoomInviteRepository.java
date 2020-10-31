package at.htl.control;

import at.htl.entity.FriendRequest;
import at.htl.entity.RoomInvite;
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
public class RoomInviteRepository implements PanacheRepository<RoomInvite> {

    @Inject
    UserRepository userRepository;

    @Inject
    RoomRepository roomRepository;

    public JsonArray getSerializedRoomInviteList(String username) {
        User user = userRepository.findByName(username);

        JsonArrayBuilder roomInviteJson = Json.createArrayBuilder();

        if (user != null) {
            List<RoomInvite> roomInviteList = user.getRoomInviteList();

            Jsonb jsonb = JsonbBuilder.create();

            roomInviteList.forEach(roomInvite -> {
                JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(roomInvite.getSender())));
                JsonObject senderJson = jsonReader.readObject();
                jsonReader = Json.createReader(new StringReader(jsonb.toJson(roomInvite.getReceiver())));
                JsonObject receiverJson = jsonReader.readObject();
                jsonReader = Json.createReader(new StringReader(jsonb.toJson(roomInvite.getRoom())));
                JsonObject roomJson = jsonReader.readObject();

                roomInviteJson.add(
                        Json.createObjectBuilder()
                                .add("id", roomInvite.getId())
                                .add("creationDate", roomInvite.getCreationDate().toString())
                                .add("sender", senderJson)
                                .add("receiver", receiverJson)
                                .add("room", roomJson)
                );
            });
        }

        return roomInviteJson.build();
    }
}

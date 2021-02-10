package at.htl.control;

import at.htl.dto.FriendRequestDTO;
import at.htl.dto.RoomInviteDTO;
import at.htl.entity.*;
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

    @Inject
    MemberRepository memberRepository;

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

    public RoomInvite sendRoomInvite(RoomInviteDTO roomInviteDTO) {
        User sender = userRepository.findByName(roomInviteDTO.getSender());
        User receiver = userRepository.findByName(roomInviteDTO.getReceiver());
        Room room = roomRepository.findById(roomInviteDTO.getRoomId());

        if (sender == null || receiver == null || room == null) return null;

        RoomInvite roomInvite = new RoomInvite(sender, receiver, room);
        persist(roomInvite);
        receiver.getRoomInviteList().add(roomInvite);

        return roomInvite;
    }

    public boolean reponseToRoomInvite(Long roomInviteId, boolean response) {
        RoomInvite roomInvite = findById(roomInviteId);
        User receiver = roomInvite.getReceiver();
        User sender = roomInvite.getSender();
        Room room =roomInvite.getRoom();

        if (roomInvite == null || receiver == null || sender == null || room == null) return false;

        if (response) {
            Member member = new Member(receiver, room, "member");
            memberRepository.persist(member);
            room.getMemberList().add(member);
        }

        receiver.getRoomInviteList().remove(roomInvite);

        return true;
    }
}

package at.htl.boundary;

import at.htl.control.MemeberRepository;
import at.htl.control.RoomRepository;
import at.htl.control.UserRepository;
import at.htl.entity.Member;
import at.htl.entity.Room;
import at.htl.entity.User;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

@Path("room")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class RoomEndpoint {

    @Inject
    RoomRepository roomRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    MemeberRepository memberRepository;

    @GET
    public List<Room> getAll() {
        return roomRepository.streamAll().peek(o -> {
            Hibernate.initialize(o.getMessageList());
            Hibernate.initialize(o.getMemberList());
            Hibernate.initialize(o.getPlaylist());
        }).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Room getRoom(@PathParam("id") Long id) {
        return roomRepository.findById(id);
    }

    @GET
    @Path("/member/{id}")
    public Response getMember(@PathParam("id") Long id) {
        Member member = memberRepository.findById(id);

        if (member != null) {
            return Response.ok(serializeMember(member)).build();
        }

        return Response.status(406).entity("member does not exist").build();
    }

    @POST
    public Response createRoom(JsonObject jsonObject) {
        User creator = userRepository.findByName(jsonObject.getString("username"));

        if (creator != null) {
            Room room = new Room(jsonObject.getString("roomname"));
            roomRepository.persist(room);

            Member member = new Member(creator, room, "owner");
            memberRepository.persist(member);

            return Response.status(201).entity(creator).build();
        }

        return Response.status(406).entity("user does not exist").build();
    }


    @POST
    @Path("/member")
    public Response addMember(JsonObject jsonObject) {
        User user = userRepository.findByName(jsonObject.getString("username"));

        if (user != null) {
            Room room = roomRepository.findById(jsonObject.getJsonNumber("roomId").longValue());

            Member member = new Member(user, room, "member");
            memberRepository.persist(member);

            return Response.status(201).entity(serializeMember(member)).build();
        }

        return Response.status(406).entity("user does not exist").build();
    }

    private JsonObject serializeMember(Member member) {
        JsonbConfig config = new JsonbConfig().withStrictIJSON(true);
        Jsonb jsonb = JsonbBuilder.create(config);

        JsonReader jsonReaderRoom = Json.createReader(new StringReader(jsonb.toJson(member.getRoom())));
        JsonReader jsonReaderUser = Json.createReader(new StringReader(jsonb.toJson(member.getRoom())));
        JsonObject roomJson = jsonReaderRoom.readObject();
        JsonObject userJson = jsonReaderUser.readObject();
        jsonReaderRoom.close();
        jsonReaderUser.close();

        return Json.createObjectBuilder()
                .add("id", member.getId())
                .add("role", member.getRole())
                .add("user", userJson)
                .add("room", roomJson)
                .build();
    }

}

package at.htl.boundary;

import at.htl.control.*;
import at.htl.entity.*;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
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
    MemberRepository memberRepository;

    @Inject
    SongRepository songRepository;

    @Inject
    PlaylistRepository playlistRepository;

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

    @GET
    @Path("/{id}/song")
    public Response getCurrentSong(@PathParam("id") Long id) {
        Room room = roomRepository.findById(id);

        if (room != null) {
            Song song = room.getPlaylist().getCurrentSong();

            if (song != null) {
                return Response.ok(song).build();
            }

            return Response.noContent().build();
        }

        return Response.status(406).entity("room does not exist").build();
    }

    @POST
    public Response createRoom(JsonObject jsonObject) {
        User creator = userRepository.findByName(jsonObject.getString("username"));

        if (creator != null) {
            Room room = new Room(jsonObject.getString("roomname"));
            playlistRepository.persist(room.getPlaylist());
            roomRepository.persist(room);

            Member member = new Member(creator, room, "owner");
            memberRepository.persist(member);

            creator.getMemberList().add(member);

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

    @POST
    @Path("/song")
    public Response addSongToPlaylist(JsonObject jsonObject) {
        Room room = roomRepository.findById(jsonObject.getJsonNumber("roomId").longValue());
        Song song = songRepository.findById(jsonObject.getJsonNumber("songId").longValue());

        if(room != null && song != null) {
            Playlist playlist = room.getPlaylist();
            playlist.getSongList().add(song);

            if(playlist.getSongList().size() == 1) {
                playlist.setCurrentSong(song);
            }

            return Response.ok("song added").build();
        }

        return Response.status(406).entity("room or song does not exist").build();
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

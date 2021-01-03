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
            Hibernate.initialize(o.getPlaylist().getSongList());
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
            return Response.ok(memberRepository.getSerializedMember(member.getId())).build();
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

            return Response.status(201).entity(memberRepository.getSerializedMember(member.getId())).build();
        }

        return Response.status(406).entity("user does not exist").build();
    }

    @POST
    @Path("/song")
    public Response addSongToPlaylist(JsonObject jsonObject) {
        Room room = roomRepository.findById(jsonObject.getJsonNumber("roomId").longValue());
        Song song;

        if (jsonObject.containsKey("songId")) {
            song = songRepository.findById(jsonObject.getJsonNumber("songId").longValue());
        } else {
            song = songRepository.findByUrl(jsonObject.getJsonObject("song").getString("url"));
            if (song == null) {
                song = new Song(jsonObject.getJsonObject("song"));
                songRepository.persist(song);
            }
        }

        if(room != null && song != null) {
            Playlist playlist = room.getPlaylist();
            playlistRepository.addSong(playlist.getId(), song);

            if(playlist.getSongList().size() == 1) {
                playlist.setCurrentSong(song);
            }

            return Response.ok(songRepository.findByUrl(song.getUrl())).build();
        }

        return Response.status(406).entity("room or song does not exist").build();
    }

    @DELETE
    @Path("{roomId}/song/delete/{songId}")
    public Response deleteSongFromRoomPlaylist(@PathParam("roomId") Long roomId, @PathParam("songId") Long songId) {
        Room room = roomRepository.findById(roomId);
        Song song = songRepository.findById(songId);
        System.out.println(room);
        Playlist playlist = room.getPlaylist();

        if (room != null && song != null) {
            if (room.getPlaylist().getSongList().contains(song)) {
                playlistRepository.removeSong(playlist.getId(), song);

                if (room.getPlaylist().getCurrentSong().equals(song)) {
                    if (room.getPlaylist().getSongList().size() > 0) {
                        room.getPlaylist().setCurrentSong(room.getPlaylist().getSongList().get(0));
                    } else {
                        room.getPlaylist().setCurrentSong(null);
                    }
                }

                if (roomRepository.findAllRoomsWithSong(song).size() == 0) {
                    songRepository.delete(song);
                }

                return Response.ok(song).build();
            }

            return Response.status(406).entity("song is not in the playlist").build();
        }

        return Response.status(406).entity("room or song does not exist").build();
    }
}

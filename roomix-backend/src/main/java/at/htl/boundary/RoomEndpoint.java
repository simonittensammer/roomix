package at.htl.boundary;

import at.htl.control.*;
import at.htl.dto.ChatMessageDTO;
import at.htl.dto.MemberDTO;
import at.htl.dto.RoomDTO;
import at.htl.dto.RoomUpdateDTO;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.util.List;
import java.util.Locale;
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

    @Inject
    MessageRepository messageRepository;

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
    @Path("/{id}/members")
    public Response getAllMembers(@PathParam("id") Long id) {
        Room room = roomRepository.findById(id);

        if(room != null) {
            List<JsonObject> serializedMembers = room.getMemberList().stream().map(member -> {
                return memberRepository.getSerializedMember(member.getId());
            }).collect(Collectors.toList());

            return Response.ok(serializedMembers).build();
        }

        return Response.status(406).entity("room does not exist").build();
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
    @Path("/popular")
    public List<Room> getPopularPublicRooms(@QueryParam("limit") int limit) {
        return roomRepository.findPopularPublicRooms(limit);
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
    public Response createRoom(RoomDTO roomDTO) throws IOException {
        User creator = userRepository.findByName(roomDTO.getUsername());

        if (creator != null) {
            Room room = new Room(roomDTO.getRoomname());
            room.setPrivate(roomDTO.isPrivate());
            room.setPicUrl(roomDTO.getPicUrl());

            if (room.getPicUrl().equals("")) {
                try (InputStream inputStream = getClass().getResourceAsStream("/images/default-room-pic.txt");
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    room.setPicUrl(reader.lines().collect(Collectors.joining(System.lineSeparator())));
                }
            }

            playlistRepository.persist(room.getPlaylist());
            roomRepository.persist(room);

            Member member = new Member(creator, room, "owner");
            memberRepository.persist(member);

            creator.getMemberList().add(member);

            return Response.status(201).entity(creator).build();
        }

        return Response.status(406).entity("user does not exist").build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") Long roomId) {
        Room room = roomRepository.findById(roomId);

        if (room == null) return Response.status(Response.Status.BAD_REQUEST).build();

        roomRepository.delete(room);

        return Response.noContent().build();
    }


    @POST
    @Path("/member")
    public Response addMember(MemberDTO memberDTO, @Context UriInfo uriInfo) {
        User user = userRepository.findByName(memberDTO.getUsername());
        Room room = roomRepository.findById(memberDTO.getRoomId());

        if (user == null || room == null) return Response.status(Response.Status.BAD_REQUEST).build();

        Member member = memberRepository.addMember(user, room);

        return Response.created(uriInfo.getAbsolutePath()).entity(member).build();
    }

    @DELETE
    @Path("/member")
    public Response removeMember(@QueryParam("username") String username, @QueryParam("roomId") Long roomId) {
        User user = userRepository.findByName(username);
        Room room = roomRepository.findById(roomId);

        if (user == null || room == null) return Response.status(Response.Status.BAD_REQUEST).build();

        Member member = memberRepository.getMemberOfRoom(user, room);

        if (member == null) return Response.status(Response.Status.BAD_REQUEST).build();

        room.getMemberList().remove(member);
        user.getMemberList().remove(member);
        memberRepository.removeMember(member);

        if (room.getMemberList().size() == 0) roomRepository.delete(room);

        return Response.noContent().build();
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

    @PUT
    public Response updateRoom(RoomUpdateDTO roomUpdateDTO) {
        return Response.ok(roomRepository.update(roomUpdateDTO)).build();
    }

    @DELETE
    @Path("{roomId}/song/delete/{songUrl}")
    public Response deleteSongFromRoomPlaylist(@PathParam("roomId") Long roomId, @PathParam("songUrl") String songUrl) {
        Room room = roomRepository.findById(roomId);
        Song song = songRepository.findByUrl(songUrl);
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

    @GET
    @Path("messages/{roomId}")
    public List<ChatMessageDTO> getMessagesInRoom(@PathParam("roomId") Long roomId) {
        return roomRepository.getAllMessagesInRoom(roomId);
    }

    @POST
    @Path("messages/{roomId}")
    public Response addChatMessage(ChatMessageDTO chatMessageDTO, @PathParam("roomId") Long roomId) {
        User user = userRepository.findByName(chatMessageDTO.getSender());
        Room room = roomRepository.findById(roomId);
        Message message = new Message(user.getUsername(), room, chatMessageDTO.getContent());

        messageRepository.persist(message);
        return Response.ok(message).build();
    }

    @GET
    @Path("search")
    public Response searchPublicRoomsByName(@QueryParam("searchTerm") String searchTerm) {
        List<Room> result;

        if (searchTerm == null) result = roomRepository.findPopularPublicRooms(5);

        else result = roomRepository.streamAll()
                .map(room -> roomRepository.initRoom(room))
                .filter(room -> room.getName().toLowerCase().contains(searchTerm.toLowerCase()) && !room.isPrivate())
                .limit(5)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }
}

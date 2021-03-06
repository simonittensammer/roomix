package at.htl.boundary;

import at.htl.control.*;
import at.htl.dto.*;
import at.htl.entity.*;
import org.hibernate.Hibernate;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Path("room")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed("user")
//@PermitAll
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

    @Inject
    TagRepository tagRepository;

    @GET
    public List<Room> getAll() {
        return roomRepository.streamAll().map(room -> roomRepository.initRoom(room)).collect(Collectors.toList());
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
    public List<Room> getPopularPublicRooms(@QueryParam("searchTerm") String searchTerm, @QueryParam("limit") int limit, @QueryParam("tags") String tags) {
        return roomRepository.streamAll()
                .map(room -> roomRepository.initRoom(room))
                 .filter(room -> {
                    if (searchTerm == null || searchTerm.equals("")) return true;
                    return room.getName().toLowerCase().contains(searchTerm.toLowerCase());
                })
                .filter(room -> !room.isPrivate())
                .filter(room -> {
                    if (tags == null || tags.equals("")) return true;
                    List<String> tagList = Arrays.asList(tags.split(",").clone());
                    return room.getTagList().stream().map(Tag::getName).anyMatch(tagList::contains);
                })
                .sorted((o1, o2) -> o2.getMemberList().size() - o1.getMemberList().size())
                .limit(limit)
                .collect(Collectors.toList());
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
            room.setTagList(roomDTO.getTagList());

            if (room.getPicUrl().equals("")) {
                try (InputStream inputStream = getClass().getResourceAsStream("/images/default-room-pic.txt");
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    room.setPicUrl(reader.lines().collect(Collectors.joining(System.lineSeparator())));
                }
            }

            playlistRepository.persist(room.getPlaylist());
            room.getTagList().forEach(tag -> tagRepository.getEntityManager().merge(tag));

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
    public Response updateRoom(RoomUpdateDTO roomUpdateDTO) throws IOException {
        Room room = roomRepository.findById(roomUpdateDTO.getRoomId());

        if (room == null) return Response.status(Response.Status.BAD_REQUEST).build();

        if (roomUpdateDTO.isResetPic()) {
            try (InputStream inputStream = getClass().getResourceAsStream("/images/default-room-pic.txt");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                roomUpdateDTO.setPicUrl(reader.lines().collect(Collectors.joining(System.lineSeparator())));
            }
        }

        roomUpdateDTO.getTagList().forEach(tagRepository.getEntityManager()::merge);

        room.update(roomUpdateDTO);

        return Response.ok(room).build();
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
    @Path("tags")
    public List<Tag> getAllTags() {
        return tagRepository.listAll();
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

    @PUT
    @Path("{roomId}/member/{username}")
    public Response updateMemberRole(@PathParam("roomId") Long roomId, @PathParam("username") String username, @QueryParam("role") String role) {
        Room room = roomRepository.findById(roomId);
        User user = userRepository.findByName(username);

        if (room == null || user == null) return Response.status(Response.Status.BAD_REQUEST).build();

        Member member = memberRepository.getMemberOfRoom(user, room);

        if (member == null) return Response.status(Response.Status.BAD_REQUEST).build();

        if (!Arrays.asList("member", "dj", "admin", "owner").contains(role)) return Response.status(Response.Status.BAD_REQUEST).build();

        member.setRole(role);

        return Response.ok(member).build();
    }
}

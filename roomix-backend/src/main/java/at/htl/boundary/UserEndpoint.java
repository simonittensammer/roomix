package at.htl.boundary;

import at.htl.control.*;
import at.htl.dto.FriendRequestDTO;
import at.htl.dto.RoomInviteDTO;
import at.htl.dto.UserUpdateDTO;
import at.htl.entity.*;
import at.htl.control.UserRepository;
import org.hibernate.Hibernate;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Path("user")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@RolesAllowed("user")
//@PermitAll
public class UserEndpoint {

    @Inject
    UserRepository userRepository;

    @Inject
    RoomRepository roomRepository;

    @Inject
    FriendRequestRepository friendRequestRepository;

    @Inject
    RoomInviteRepository roomInviteRepository;

    @Inject
    MemberRepository memberRepository;


    @GET
    public List<User> getAll() {
        return userRepository.streamAll().peek(o -> {
            Hibernate.initialize(o.getFriendRequestList());
            Hibernate.initialize(o.getMemberList());
            Hibernate.initialize(o.getRoomInviteList());
            Hibernate.initialize(o.getFriendList());
            Hibernate.initialize(o.getAddUser());
        }).collect(Collectors.toList());
    }

    @GET
    @Path("/{userName}")
    public User getUser(@PathParam("userName") String userName) {
        User user = userRepository.findByName(userName);
//        System.out.println(user.toString());
        return user;
    }

    @GET
    @Path("/{username}/rooms")
    public List<Room> getUserRooms(@PathParam("username") String username) {
        return userRepository.findAllRoomsOfUser(username);
    }

    @GET
    @Path("/{username}/members")
    public List<JsonObject> getUserMembers(@PathParam("username") String username) {
        return userRepository.findAllSerializedMembers(username);
    }

    @GET
    @Path("/{username}/friends")
    public List<User> findFriends(@PathParam("username") String username) {
        return userRepository.findAllFriends(username);
    }

    @GET
    @Path("/{username}/friendRequests")
    public JsonArray getFriendRequests(@PathParam("username") String username) {
        return friendRequestRepository.getSerializedFriendRequestList(username);
    }

    @GET
    @Path("/{username}/roomInvites")
    public JsonArray getRoomInvites(@PathParam("username") String username) {
        return roomInviteRepository.getSerializedRoomInviteList(username);
    }

    @GET
    @Path("friendRequests/{id}/{response}")
    public Response respondToFriendRequest(@PathParam("id") Long friendRequestId, @PathParam("response") boolean response) {
        if (friendRequestRepository.respondToFriendRequest(friendRequestId, response))
            return Response.noContent().build();

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("roomInvites/{id}/{response}")
    public Response respondToRoomInvite(@PathParam("id") Long roomInviteId, @PathParam("response") boolean response) {
        if (roomInviteRepository.respondToRoomInvite(roomInviteId, response)) return Response.noContent().build();

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

//    @POST
//    @Path("/login")
//    public Response loginUser(JsonObject jsonObject) {
//        String username = jsonObject.getString("username");
//        String password = jsonObject.getString("password");
//
//        User user = userRepository.findByName(username);
//
//        if (user != null) {
//            if (user.getPassword().equals(password)) {
//                return Response.ok(user).build();
//            } else {
//                return Response.status(406).entity("password wrong").build();
//            }
//        }
//
//        return Response.status(406).entity("user does not exist").build();
//    }

    @PUT
    public Response updateUser(UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByName(userUpdateDTO.getUsername());

        if (user == null) return Response.status(Response.Status.BAD_REQUEST).build();

        if(userUpdateDTO.isResetProfilePic()) {
            try (InputStream inputStream = getClass().getResourceAsStream("/images/default-user-pic.txt");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                userUpdateDTO.setPicUrl(reader.lines().collect(Collectors.joining(System.lineSeparator())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        user.update(userUpdateDTO);
        return Response.ok(user).build();
    }

    @POST
    @Path("/friend")
    public Response friendUsers(JsonObject jsonObject) {
        User user1 = userRepository.findByName(jsonObject.getString("username1"));
        User user2 = userRepository.findByName(jsonObject.getString("username2"));

        if (user1 != null && user2 != null) {
            userRepository.friendUsers(user1, user2);
            return Response.ok().build();
        }

        return Response.status(406).entity("user(s) do(es) not exist").build();
    }

    @POST
    @Path("/friendRequest")
    public Response sendFriendRequest(FriendRequestDTO friendRequestDTO, @Context UriInfo uriInfo) {

        FriendRequest friendRequest = friendRequestRepository.sendFriendRequest(friendRequestDTO);

        if (friendRequest == null) return Response.status(Response.Status.BAD_REQUEST).build();

        return Response.created(uriInfo.getAbsolutePath()).entity(friendRequest).build();
    }

    @PUT
    @Path("/unfriend")
    public Response unfriendUsers(FriendRequestDTO friendRequestDTO) {

        if (!friendRequestRepository.unfriendUsers(friendRequestDTO)) return Response.status(Response.Status.BAD_REQUEST).build();

        return Response.noContent().build();
    }

    @POST
    @Path("/roomInvite")
    public Response sendRoomInvite(RoomInviteDTO roomInviteDTO, @Context UriInfo uriInfo) {
        RoomInvite roomInvite = roomInviteRepository.sendRoomInvite(roomInviteDTO);

        if (roomInvite == null) return Response.status(Response.Status.BAD_REQUEST).build();

        return Response.created(uriInfo.getAbsolutePath()).entity(roomInvite).build();
    }

    @DELETE
    @Path("/delete/{username}")
    public Response deleteUser(@PathParam("username") String username) {
        User user = userRepository.findByName(username);

        if (user != null) {
            userRepository.delete(user);
            return Response.ok(user).build();
        }

        return Response.status(406).entity("user does not exist").build();
    }

    @GET
    @Path("{username}/search")
    public Response searchUsersWithMatchingName(@PathParam("username") String username, @QueryParam("searchTerm") String searchTerm) {
        User searchUser = userRepository.findByName(username);

        if (searchUser == null) return Response.status(Response.Status.BAD_REQUEST).build();

        List<User> result = userRepository.streamAll()
                .map(user -> userRepository.initUser(user))
                .filter(user -> user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) && !user.getUsername().equals(username) && !searchUser.getFriendList().contains(user))
                .limit(5)
                .collect(Collectors.toList());

        return Response.ok(result).build();
    }

    @GET
    @Path("{username}/{roomId}/friends/search")
    public Response searchFriendsWithMatchingName(@PathParam("username") String username, @PathParam("roomId") Long roomId, @QueryParam("searchTerm") String searchTerm) {
        User searchUser = userRepository.findByName(username);
        Room room = roomRepository.findById(roomId);

        if (searchUser == null || room == null) return Response.status(Response.Status.BAD_REQUEST).build();

        List<User> result;

        if (searchTerm == null) {

            result = searchUser.getFriendList().stream().map(user -> userRepository.initUser(user)).limit(5).collect(Collectors.toList());

        } else {

            result = searchUser.getFriendList().stream()
                    .map(user -> userRepository.initUser(user))
                    .filter(user -> user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) && memberRepository.getMemberOfRoom(user, room) == null)
                    .limit(5)
                    .collect(Collectors.toList());
        }

        return Response.ok(result).build();
    }

}

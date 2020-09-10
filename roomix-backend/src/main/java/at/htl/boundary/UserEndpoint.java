package at.htl.boundary;

import at.htl.control.UserRepository;
import at.htl.entity.Member;
import at.htl.entity.Room;
import at.htl.entity.User;
import at.htl.control.UserRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("user")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class UserEndpoint {

    @Inject
    UserRepository userRepository;

    @GET
    public List<User> getAll() {
        return userRepository.streamAll().peek(o -> {
            Hibernate.initialize(o.getFriendRequestList());
            Hibernate.initialize(o.getMemberList());
            Hibernate.initialize(o.getRoomInviteList());
        }).collect(Collectors.toList());
    }

    @GET
    @Path("/{userName}")
    public User getUser(@PathParam("userName") String userName) {
        User user = userRepository.findByName(userName);
        System.out.println(user.toString());
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

    @POST
    public Response addUser(User user) {
        try {
            userRepository.persist(user);
            return Response.status(201).entity(user).build();
        }catch (PersistenceException e) {
            return Response.status(406).entity("username is already taken").build();
        }
    }

    @POST
    @Path("/login")
    public Response loginUser(JsonObject jsonObject) {
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");

        User user = userRepository.findByName(username);

        if (user != null) {
            if(user.getPassword().equals(password)) {
                return Response.ok(user).build();
            } else {
                return Response.status(406).entity("password wrong").build();
            }
        }

        return Response.status(406).entity("user does not exist").build();
    }

    @POST
    @Path("/update")
    public Response updateUser(JsonObject jsonObject) {
        String username = jsonObject.getString("username");
        JsonObject changes = jsonObject.getJsonObject("changes");

        User user = userRepository.findByName(username);

        if (user != null) {
            try {
                userRepository.updateUser(user, changes);
                return Response.ok(user).build();
            }catch (PersistenceException e) {
                return Response.status(406).entity("username is already taken").build();
            }
        }

        return Response.status(406).entity("user does not exist").build();
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
}

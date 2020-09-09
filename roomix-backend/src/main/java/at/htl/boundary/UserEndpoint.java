package at.htl.boundary;

import at.htl.control.UserRepository;
import at.htl.entity.User;
import at.htl.control.UserRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
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

    @POST
    public Response addUser(User user) {
        try {
            userRepository.persist(user);
            return Response.status(201).entity(user).build();
        }catch (PersistenceException e) {
            System.out.println(e.getMessage());
            return Response.status(406).entity("username is already taken").build();
        }
    }

    @POST
    @Path("/login")
    public Response loginUser(JsonObject json) {
        String username = json.getString("username");
        String password = json.getString("password");

        User user = userRepository.findByName(username);

        if (user != null) {
            if(user.getPassword().equals(password)) {
                return Response.ok(user).build();
            } else {
                return Response.status(406).entity("password wrong").build();
            }
        }

        return Response.status(406).entity("this user does not exist").build();
    }
}

package at.htl.boundary;

import at.htl.control.UserRepository;
import at.htl.dto.JwtTokenDTO;
import at.htl.entity.User;
import at.htl.util.TokenUtils;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class AuthResource {

    @Inject
    @ConfigProperty(name = "smallrye.jwt.new-token.lifespan")
    long lifespan;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Inject
    UserRepository userRepository;

    @POST
    public Response addUser(User user) throws IOException {
        try {
            if (user.getPicUrl().equals("")) {
                try (InputStream inputStream = getClass().getResourceAsStream("/images/default-user-pic.txt");
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    user.setPicUrl(reader.lines().collect(Collectors.joining(System.lineSeparator())));
                }
            }
            userRepository.persist(user);
            return Response.status(201).entity(user).build();
        } catch (PersistenceException e) {
            return Response.status(406).entity("username is already taken").build();
        }
    }

    @POST
    @Path("/login")
    public Response login(JsonObject object) throws Exception {
        String username = object.getString("username");
        String password = object.getString("password");

        User user = userRepository.findByName(username);

        if (user == null) return Response.status(Response.Status.BAD_REQUEST).build();

        if (!user.getPassword().equals(password)) return Response.status(Response.Status.UNAUTHORIZED).build();

        long exp = Instant.now().getEpochSecond() + lifespan;

//        String token = Jwt
//                .claims(claims)
//                .groups("user")
//                .sign();

        String token = generateToken(user.getUsername());

        JwtTokenDTO jwtTokenDTO = new JwtTokenDTO(token, exp, user);

        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(jwtTokenDTO);

        return Response.ok().entity(result).build();
    }

    private String generateToken(String username) throws Exception {
        Map<String, Long> timeClaims = new HashMap<>();
        timeClaims.put(Claims.exp.name(), TokenUtils.currentTimeInSecs() + 120l);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.iss.name(), issuer);
        claims.put(Claims.upn.name(), username);
        claims.put(Claims.groups.name(), Set.of(new String[]{"user"}));

        return TokenUtils.generateTokenString(claims, timeClaims);
    }
}

package at.htl.control;

import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.persistence.PersistenceException;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findByName(String userName) {
        User user = find("USR_NAME", userName).firstResult();

        if (user != null) {
            Hibernate.initialize(user);
            Hibernate.initialize(user.getFriendRequestList());
            Hibernate.initialize(user.getMemberList());
            Hibernate.initialize(user.getRoomInviteList());
        }
        return user;
    }

    public void updateUser(User user, JsonObject jsonObject) {

        if (jsonObject.containsKey("username")) {
            if (findByName(jsonObject.getString("username")) != null) {
                throw new PersistenceException();
            }
        }

        jsonObject.forEach((s, jsonValue) -> {
            String value = jsonValue.toString().replace("\"", "");
            switch (s) {
                case "displayname":
                    user.setDisplayname(value);
                    break;
                case "username":
                    user.setUsername(value);
                    break;
                case "password":
                    user.setPassword(value);
                    break;
                case "picUrl":
                    user.setPicUrl(value);
                    break;
                default:
                    break;
            }
        });
    }
}

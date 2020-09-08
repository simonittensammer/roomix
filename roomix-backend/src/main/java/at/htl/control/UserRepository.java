package at.htl.control;

import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findByName(String userName) {
        User user = find("USR_NAME", userName).firstResult();

        if(user != null) {
            Hibernate.initialize(user);
            Hibernate.initialize(user.getFriendRequestList());
            Hibernate.initialize(user.getMemberList());
            Hibernate.initialize(user.getRoomInviteList());
        }
        return user;
    }
}

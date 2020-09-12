package at.htl.control;

import at.htl.entity.FriendRequest;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FriendRequestRepository implements PanacheRepository<FriendRequest> {
}

package at.htl.control;

import at.htl.entity.RoomInvite;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomInviteRepository implements PanacheRepository<RoomInvite> {
}

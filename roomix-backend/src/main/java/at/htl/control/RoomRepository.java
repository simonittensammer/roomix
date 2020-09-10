package at.htl.control;

import at.htl.entity.Room;
import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomRepository implements PanacheRepository<Room> {

    @Override
    public Room findById(Long id) {
        Room room = find("RM_ID", id).firstResult();

        if (room != null) {
            Hibernate.initialize(room);
            Hibernate.initialize(room.getMessageList());
            Hibernate.initialize(room.getMemberList());
            Hibernate.initialize(room.getPlaylist());
        }
        return room;
    }
}

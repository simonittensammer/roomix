package at.htl.control;

import at.htl.entity.Member;
import at.htl.entity.Room;
import at.htl.entity.Song;
import at.htl.entity.User;
import at.htl.observers.MemberRepositoryObserver;
import at.htl.observers.PlaylistRepositoryObserver;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class MemberRepository implements PanacheRepository<Member> {

    private final List<MemberRepositoryObserver> observerList = new ArrayList<>();

    public void addObserver(MemberRepositoryObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(MemberRepositoryObserver observer) {
        observerList.remove(observer);
    }

    private void notifyObserver(Long roomId) {
        observerList.forEach(memberRepositoryObserver -> memberRepositoryObserver.update(roomId));
    }

    @Override
    public Member findById(Long id) {
        Member member = find("MBR_ID", id).firstResult();

        if (member != null) {
            Hibernate.initialize(member);
            Hibernate.initialize(member.getUser());
            Hibernate.initialize(member.getRoom());
        }
        return member;
    }

    public JsonObject getSerializedMember(Long id) {
        Member member = findById(id);

        Jsonb jsonb = JsonbBuilder.create();

        JsonReader jsonReaderRoom = Json.createReader(new StringReader(jsonb.toJson(member.getRoom())));
        JsonObject roomJson = jsonReaderRoom.readObject();
        jsonReaderRoom.close();

        JsonReader jsonReaderUser = Json.createReader(new StringReader(jsonb.toJson(member.getUser())));
        JsonObject userJson = jsonReaderUser.readObject();
        jsonReaderUser.close();

        return Json.createObjectBuilder()
                .add("id", member.getId())
                .add("role", member.getRole())
                .add("user", userJson)
                .add("room", roomJson)
                .build();
    }

    public Member addMember(User user, Room room) {
        Member member = new Member(user, room, "member");
        persist(member);
        notifyObserver(member.getRoom().getId());
        return member;
    }

    public void removeMember(Member member) {
        delete(member);
        notifyObserver(member.getRoom().getId());
    }

    public Member getMemberOfRoom(User user, Room room) {
        return user.getMemberList()
                .stream()
                .filter(member -> member.getRoom().getId().equals(room.getId()))
                .findFirst()
                .orElse(null);
    }
}

package at.htl.control;

import at.htl.entity.Member;
import at.htl.entity.Room;
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

@ApplicationScoped
@Transactional
public class MemberRepository implements PanacheRepository<Member> {

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
        JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(member.getRoom())));
        JsonObject roomJson = jsonReader.readObject();
        jsonReader = Json.createReader(new StringReader(jsonb.toJson(member.getUser())));
        JsonObject userJson = jsonReader.readObject();
        jsonReader.close();

        return Json.createObjectBuilder()
                .add("id", member.getId())
                .add("role", member.getRole())
                .add("user", userJson)
                .add("room", roomJson)
                .build();
    }
}

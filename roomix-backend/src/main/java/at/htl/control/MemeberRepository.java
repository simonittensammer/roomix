package at.htl.control;

import at.htl.entity.Member;
import at.htl.entity.Room;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MemeberRepository implements PanacheRepository<Member> {

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
}

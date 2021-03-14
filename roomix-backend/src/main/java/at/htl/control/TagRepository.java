package at.htl.control;

import at.htl.entity.Tag;
import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {

    public Tag findByName(String name) {
        return find("TAG_NAME", name).firstResult();
    }
}

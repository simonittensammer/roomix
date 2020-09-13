package at.htl.control;

import at.htl.entity.Song;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SongRepository implements PanacheRepository<Song> {

    public Song findByUrl(String url) {
        return find("SNG_URL", url).firstResult();
    }
}

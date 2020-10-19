package at.htl.control;

import at.htl.entity.Playlist;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlaylistRepository implements PanacheRepository<Playlist> {

}

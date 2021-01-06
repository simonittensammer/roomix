package at.htl.control;

import at.htl.entity.Playlist;
import at.htl.entity.Song;
import at.htl.observers.PlaylistRepositoryObserver;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PlaylistRepository implements PanacheRepository<Playlist> {

    private List<PlaylistRepositoryObserver> observerList = new ArrayList<>();

    public void addObserver(PlaylistRepositoryObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(PlaylistRepositoryObserver observer) {
        observerList.remove(observer);
    }

    private void notifyObserversAdd(Long id, Song song) {
        observerList.forEach(observer -> observer.addSong(id, song));
    }

    private void notifyObserversRemove(Long id, Song song) {
        observerList.forEach(observer -> observer.removeSong(id, song));
    }

    public void addSong(Long id,Song song) {
        Playlist playlist = findById(id);
        playlist.getSongList().add(song);

        notifyObserversAdd(id, song);
    }

    public void removeSong(Long id,Song song) {
        Playlist playlist = findById(id);
        playlist.getSongList().remove(song);

        notifyObserversRemove(id, song);
    }

    public void changeCurrentSong(Long id,Song song) {
        Playlist playlist = findById(id);
        playlist.setCurrentSong(song);
    }
}

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

    private void notifyObservers(Long id, Playlist playlist) {
        observerList.forEach(observer -> observer.updatePlaylist(id, playlist));
    }

    public void addSong(Long id,Song song) {
        Playlist playlist = findById(id);
        playlist.getSongList().add(song);

        notifyObservers(id, playlist);
    }

    public void removeSong(Long id,Song song) {
        Playlist playlist = findById(id);
        playlist.getSongList().remove(song);

        notifyObservers(id, playlist);
    }

    public void changeCurrentSong(Long id,Song song) {
        Playlist playlist = findById(id);
        playlist.setCurrentSong(song);
    }
}

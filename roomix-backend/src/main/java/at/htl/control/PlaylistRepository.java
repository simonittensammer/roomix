package at.htl.control;

import at.htl.entity.Playlist;
import at.htl.entity.Song;
import at.htl.socket.PlaylistObserver;
import at.htl.socket.PlaylistSongObserver;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PlaylistRepository implements PanacheRepository<Playlist> {

    private List<PlaylistSongObserver> observerList = new ArrayList<>();

    public void addObserver(PlaylistSongObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(PlaylistSongObserver observer) {
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
}

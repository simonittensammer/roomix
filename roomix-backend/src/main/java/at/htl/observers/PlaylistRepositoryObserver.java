package at.htl.observers;

import at.htl.entity.Playlist;
import at.htl.entity.Song;

public interface PlaylistRepositoryObserver {

    public void addSong(Long roomId, Song song);

    public void removeSong(Long roomId, Song song);
}

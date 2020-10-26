package at.htl.observers;

import at.htl.entity.Playlist;
import at.htl.entity.Song;

public interface PlaylistRepositoryObserver {

    public void updatePlaylist(Long id, Playlist playlist);
}

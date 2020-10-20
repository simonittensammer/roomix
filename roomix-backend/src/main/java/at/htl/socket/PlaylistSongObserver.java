package at.htl.socket;

import at.htl.entity.Playlist;
import at.htl.entity.Song;

public interface PlaylistSongObserver {

    public void updatePlaylist(Long id, Playlist playlist);
}

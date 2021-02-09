package at.htl.dto;

import at.htl.entity.Song;

import java.io.Serializable;

public class PlaySongMessageDTO implements Serializable {

    private Song song;
    private long time;

    public PlaySongMessageDTO() {
    }

    public PlaySongMessageDTO(Song song, long time) {
        this.song = song;
        this.time = time;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

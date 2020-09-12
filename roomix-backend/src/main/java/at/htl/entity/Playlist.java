package at.htl.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Entity
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLL_ID")
    Long id;

    @OneToMany
    List<Song> songList;

    @OneToOne
    Song currentSong;

    public Playlist() {
        this.songList = new LinkedList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }
}

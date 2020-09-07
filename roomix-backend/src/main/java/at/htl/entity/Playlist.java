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

    @OneToMany
    List<Song> songQueue;

    public Playlist() {
        this.songList = new LinkedList<>();
        this.songQueue = new LinkedList<>();
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

    public List<Song> getSongQueue() {
        return songQueue;
    }

    public void setSongQueue(List<Song> songQueue) {
        this.songQueue = songQueue;
    }
}

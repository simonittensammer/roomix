package at.htl.entity;

import javax.persistence.*;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SNG_ID")
    Long id;

    @Column(name = "SNG_TITLE")
    String title;

    @Column(name = "SNG_ARTIST")
    String artist;

    @Column(name = "SNG_URL")
    String url;

    @Column(name = "SNG_PIC")
    String picUrl;

    @Column(name = "SNG_LENGTH")
    int length;

    public Song() { }

    public Song(String title, String artist, String url, String picUrl, int length) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.picUrl = picUrl;
        this.length = length;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}

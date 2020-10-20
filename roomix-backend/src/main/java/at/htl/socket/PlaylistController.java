package at.htl.socket;

import at.htl.control.PlaylistRepository;
import at.htl.control.SongRepository;
import at.htl.entity.Playlist;
import at.htl.entity.Song;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistController {

    private Long roomId;

    private Playlist playlist;
    private Song previousSong;

    private Timer songTimer;
    private LocalDateTime songStartTime;

    private List<PlaylistObserver> observerList;

    private Random rn;

    public PlaylistController(Long roomId, Playlist playlist) {
        this.roomId = roomId;
        this.playlist = playlist;
        songTimer = new Timer("Song-Timer");
        observerList = new ArrayList<>();
        rn = new Random();

        startSong();
    }

    private void startSong() {
        songStartTime = LocalDateTime.now();
        songTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                nextSong();
            }
        }, playlist.getCurrentSong().getLength() * 1000);

        notifyObservers();
    }

    private void nextSong() {
        previousSong = playlist.getCurrentSong();

        Song nextSong;
        do {
            nextSong = playlist.getSongList().get(rn.nextInt(playlist.getSongList().size()));
        } while (nextSong == previousSong);

        playlist.setCurrentSong(nextSong);

        startSong();
    }

    public void skipSong() {
        nextSong();
    }

    public String getCurrentSong() {

        Jsonb jsonb = JsonbBuilder.create();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(playlist.getCurrentSong())));
        JsonObject songJson = jsonReader.readObject();
        jsonReader.close();

        return Json.createObjectBuilder()
                .add("song", songJson)
                .add("time", songStartTime.toString())
                .build().toString();
    }

    public void addObserver(PlaylistObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(PlaylistObserver observer) {
        observerList.remove(observer);
    }

    private void notifyObservers() {
        observerList.forEach(observer -> observer.newSong(roomId));
    }

    public Timer getSongTimer() {
        return songTimer;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        System.out.println("playlist updated " + playlist.getSongList().size());
    }
}

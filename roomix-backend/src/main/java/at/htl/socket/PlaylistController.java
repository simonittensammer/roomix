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
    private Queue<Song> songQueue;

    private Timer songTimer;
    private LocalDateTime songStartTime;

    private List<PlaylistObserver> observerList;

    public PlaylistController(Long roomId, Playlist playlist) {
        this.roomId = roomId;
        this.playlist = playlist;
        songQueue = new LinkedList<>();
        songTimer = new Timer("Song-Timer");
        observerList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            songQueue.add(playlist.getSongList().get((int) Math.random() * playlist.getSongList().size()));
        }

        startSong();
    }

    private void startSong() {
        System.out.println("now playing " + playlist.getCurrentSong().getTitle());

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
        playlist.setCurrentSong(songQueue.poll());
        songQueue.add(playlist.getSongList().get((int) Math.random() * playlist.getSongList().size()));
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

    public void addBidObserver(PlaylistObserver observer) {
        observerList.add(observer);
    }

    public void removeBidObserver(PlaylistObserver observer) {
        observerList.remove(observer);
    }

    private void notifyObservers() {
        observerList.forEach(observer -> observer.newSong(roomId));
    }
}

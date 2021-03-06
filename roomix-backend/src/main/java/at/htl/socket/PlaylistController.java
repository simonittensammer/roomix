package at.htl.socket;

import at.htl.entity.Playlist;
import at.htl.entity.Song;
import at.htl.observers.PlaylistControllerObserver;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class PlaylistController {

    private static final Logger LOGGER = Logger.getLogger("PlaylistController");

    private Long roomId;

    private Playlist playlist;
    private Song previousSong;

    private Timer songTimer;
    private boolean timerIsRunning;
    private LocalDateTime songStartTime;

    private List<PlaylistControllerObserver> observerList;

    private Random rn;

    public PlaylistController(Long roomId, Playlist playlist) {
        this.roomId = roomId;
        this.playlist = playlist;
        songTimer = new Timer("Song-Timer");
        timerIsRunning = false;
        observerList = new ArrayList<>();
        rn = new Random();

        if (playlist.getSongList().size() > 0) {
            startSong();
        }
    }

    private void startSong() {
        songStartTime = LocalDateTime.now();
        songTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                nextSong();
            }
        }, playlist.getCurrentSong().getLength() * 1000);
        timerIsRunning = true;

        if (timerIsRunning) {
            notifyObservers();
        }
    }

    private void nextSong() {
        previousSong = playlist.getCurrentSong();

        Song nextSong;
        do {
            nextSong = playlist.getSongList().get(rn.nextInt(playlist.getSongList().size()));
        } while (nextSong == previousSong && playlist.getSongList().size() > 1);

        playlist.setCurrentSong(nextSong);

        startSong();
    }

    public void skipSong() {
        songTimer.cancel();
        songTimer = new Timer("Song-Timer");
        nextSong();
    }

    public String getCurrentSongMessage() {

        Jsonb jsonb = JsonbBuilder.create();
        JsonReader jsonReader = Json.createReader(new StringReader(jsonb.toJson(playlist.getCurrentSong())));
        JsonObject songJson = jsonReader.readObject();
        jsonReader.close();

        String timeStamp = String.valueOf(Duration.between(songStartTime, LocalDateTime.now()).toSeconds());

        return Json.createObjectBuilder()
                .add("type", "new-song")
                .add("message", Json.createObjectBuilder()
                        .add("song", songJson)
                        .add("time", timeStamp)
                        .build()
                ).build().toString();
    }

    public long getCurrentSongTime() {
        return Duration.between(songStartTime, LocalDateTime.now()).toSeconds();
    }

    public Song getCurrentSong() {
        return playlist.getCurrentSong();
    }

    public void addObserver(PlaylistControllerObserver observer) {
        observerList.add(observer);
    }

    public void removeObserver(PlaylistControllerObserver observer) {
        observerList.remove(observer);
    }

    private void notifyObservers() {
        observerList.forEach(observer -> observer.newSong(roomId));
    }

    public Long getRoomId() {
        return roomId;
    }

    public Timer getSongTimer() {
        return songTimer;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void updatePlaylist() {
        System.out.println("playlist updated " + playlist.getSongList().size());

        if (timerIsRunning && this.playlist.getSongList().size() == 0) {
            songTimer.cancel();
            timerIsRunning = false;
            LOGGER.info("\nSTOPPPIUG\n");
        }
        else if (!this.playlist.getSongList().contains(this.playlist.getCurrentSong())) {
            songTimer.cancel();
            songTimer = new Timer("Song-Timer");
            nextSong();
        }
        else if (!timerIsRunning && this.playlist.getSongList().size() > 0) {
            songTimer = new Timer("Song-Timer");

            if (playlist.getCurrentSong() == null) {
                playlist.setCurrentSong(playlist.getSongList().get(0));
            }

            startSong();
        }
    }
}

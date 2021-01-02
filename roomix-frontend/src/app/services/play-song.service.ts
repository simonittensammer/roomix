import {Injectable, OnInit} from '@angular/core';
import {Song} from '../models/song';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {webSocket} from 'rxjs/webSocket';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class PlaySongService {

  songSocket: WebSocketSubject<Song>;
  currentSong: Song;
  currentSongUrl: string;
  currentSongTimer: number;
  completeUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) { }

  connect(username, roomid) {
    this.songSocket = webSocket(
        'ws://localhost:8080/room/'
        + roomid + '/' + username);

    this.songSocket.asObservable().subscribe(
        data => {
            // @ts-ignore
            this.currentSong = data.song;
            this.currentSongUrl = this.currentSong.url;
            // @ts-ignore
            this.currentSongTimer = data.time;
            console.log(this.currentSong);
            console.log(data);
            this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + this.currentSongUrl + '?start=' + this.currentSongTimer + '&controls=1&amp&autoplay=1');
        }, error => {
          console.log(error);
        }
    );
  }

  disconnect() {
    this.songSocket.complete();
  }
}

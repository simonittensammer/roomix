import {Injectable, OnInit} from '@angular/core';
import {Song} from '../models/song';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {webSocket} from 'rxjs/webSocket';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {RoomService} from './room.service';
import {Room} from '../models/room';

@Injectable({
    providedIn: 'root'
})
export class PlaySongService {

    songSocket: WebSocketSubject<Song>;
    currentSong: Song;
    currentSongUrl: string;
    currentSongTimer: number;
    completeUrl: SafeResourceUrl;
    room: Room;

    constructor(
        private sanitizer: DomSanitizer,
        private roomService: RoomService
    ) {
    }

    connect(username, roomid) {
        this.roomService.roomValue.subscribe(
            value => {
                this.room = value;
            }
        );

        this.songSocket = webSocket(
            'ws://localhost:8080/room/'
            + roomid + '/' + username);

        this.songSocket.asObservable().subscribe(
            data => {
                console.log(data);
                // @ts-ignore
                // console.log(data);
                // @ts-ignore
                if (data.type === 'new-song') {
                    // @ts-ignore
                    this.currentSong = data.message.song;
                    this.currentSongUrl = this.currentSong.url;
                    // @ts-ignore
                    this.currentSongTimer = data.message.time;
                    this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/'
                        + this.currentSongUrl + '?start=' + this.currentSongTimer + '&controls=1&amp&autoplay=1');
                }
                // @ts-ignore
                else if (data.type === 'add-song') {
                    // @ts-ignore
                    const song = JSON.parse(data.message);
                    this.room.playlist.songList.push(song);
                }
                // @ts-ignore
                else if (data.type === 'remove-song') {
                    // @ts-ignore
                    const song = JSON.parse(data.message);
                    this.room.playlist.songList.splice(this.room.playlist.songList.findIndex(x => x.url === song.url), 1);
                }
                // @ts-ignore
                else if (data.type === 'stop') {
                    // @ts-ignore
                    console.log('stop playing song');
                    this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(null);

                }
            }, error => {
                console.log(error);
            }
        );
    }

    disconnect() {
        this.songSocket.complete();
    }
}

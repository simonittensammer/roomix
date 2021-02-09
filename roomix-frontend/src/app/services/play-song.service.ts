import {Injectable, OnInit} from '@angular/core';
import {Song} from '../models/song';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {webSocket} from 'rxjs/webSocket';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {RoomService} from './room.service';
import {Room} from '../models/room';
import {SocketMessageDTO} from '../models/dto/SocketMessageDTO';
import {PlaySongMessageDTO} from '../models/dto/PlaySongMessageDTO';

@Injectable({
    providedIn: 'root'
})
export class PlaySongService {

    songSocket: WebSocketSubject<SocketMessageDTO>;
    currentSong: Song;
    currentSongUrl: string;
    currentSongTimer: number;
    completeUrl: SafeResourceUrl;
    room: Room;
    connected = false;

    constructor(
        private sanitizer: DomSanitizer,
        private roomService: RoomService
    ) {
    }

    connect(username, roomid) {
        this.roomService.getRoom(roomid).subscribe(value => {
            return this.room = value;
        });

        this.songSocket = webSocket(
            'ws://localhost:8080/room/'
            + roomid + '/' + username);

        this.connected = true;

        this.songSocket.asObservable().subscribe(
            data  => {
                console.log(data);

                if (data.type === 'new-song') {
                    const message: PlaySongMessageDTO = data.message as PlaySongMessageDTO;
                    this.currentSong = message.song;
                    this.currentSongUrl = this.currentSong.url;
                    this.currentSongTimer = message.time;
                    this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/'
                        + this.currentSongUrl + '?start=' + this.currentSongTimer + '&controls=1&amp&autoplay=1');
                }

                else if (data.type === 'add-song') {
                    const song: Song = data.message as Song;
                    this.room.playlist.songList.push(song);
                }

                else if (data.type === 'remove-song') {
                    console.log('removeing');
                    const song: Song = data.message as Song;
                    this.room.playlist.songList.splice(this.room.playlist.songList.findIndex(x => x.url === song.url), 1);
                }

                else if (data.type === 'stop') {
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
        this.connected = false;
    }
}

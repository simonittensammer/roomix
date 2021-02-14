import {Injectable, OnInit} from '@angular/core';
import {Song} from '../models/song';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {webSocket} from 'rxjs/webSocket';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {RoomService} from './room.service';
import {Room} from '../models/room';
import {SocketMessageDTO} from '../models/dto/SocketMessageDTO';
import {PlaySongMessageDTO} from '../models/dto/PlaySongMessageDTO';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {ChatMessageDTO} from "../models/dto/chatMessageDTO";

@Injectable({
    providedIn: 'root'
})
export class PlaySongService {

    songSocket: WebSocketSubject<SocketMessageDTO>;
    currentSong: Song;
    currentSongUrl = '';
    currentSongTimer: number;
    completeUrl: SafeResourceUrl;
    room: Room;
    private listeningRoom: BehaviorSubject<Room>;
    connected = false;

    public YT: any;
    public video: any;
    public player: any;
    public reframed: boolean = false;

    private resetSkipVote = new Subject<void>();
    public resetSkipVoteEvent = this.resetSkipVote.asObservable();

    isRestricted = /iPhone|iPad|iPod|Android/i.test(navigator.userAgent);

    constructor(
        private sanitizer: DomSanitizer,
        private roomService: RoomService
    ) {
        this.listeningRoom = new BehaviorSubject<Room>(JSON.parse(localStorage.getItem('listeningRoom')));
        this.room = new Room('');
        this.init();
    }

    public get roomValue() {
        return this.listeningRoom.asObservable();
    }

    public updateRoomValue(room: Room) {
        localStorage.setItem('listeningRoom', JSON.stringify(room));
        this.listeningRoom.next(room);
    }

    connect(username, roomid) {
        this.roomService.getRoom(roomid).subscribe(room => {

            this.roomService.getMessages(roomid).subscribe(messages => {
                this.room = room;
                this.room.messageList = messages;
                this.updateRoomValue(room);
                this.roomService.updateRoomValue(this.room);
            });
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
                    this.player.loadVideoById(this.currentSongUrl, this.currentSongTimer);
                    this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/'
                        + this.currentSongUrl + '?start=' + this.currentSongTimer + '&controls=1&amp&autoplay=1');
                    this.resetSkipVote.next();
                }

                else if (data.type === 'add-song') {
                    const song: Song = data.message as Song;
                    this.room.playlist.songList.push(song);
                    this.roomService.updateRoomValue(this.room);
                }

                else if (data.type === 'remove-song') {
                    console.log('removeing');
                    const song: Song = data.message as Song;
                    this.room.playlist.songList.splice(this.room.playlist.songList.findIndex(x => x.url === song.url), 1);
                    this.roomService.updateRoomValue(this.room);
                }

               else if (data.type === 'stop') {
                    console.log('stop playing song');
                    this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(null);

                }

               else if (data.type === 'chat-message') {
                   const chatMessage: ChatMessageDTO = data.message as ChatMessageDTO;
                   console.log(chatMessage.sender + ': ' + chatMessage.content);
                   this.room.messageList.push(chatMessage);
                   this.roomService.updateRoomValue(this.room);
                }
            }, error => {
                console.log(error);
            }
        );
    }

    disconnect() {
        this.songSocket.complete();
        this.currentSong = new Song('', '', '', '', 0);
        this.currentSongUrl = '';
        this.player.loadVideoById(this.currentSongUrl);
        this.currentSongTimer = 0;
        this.completeUrl = this.sanitizer.bypassSecurityTrustResourceUrl('');
        this.connected = false;
    }

    // tslint:disable-next-line:ban-types
    sendChatMessage(message: String) {
        this.songSocket.next(new SocketMessageDTO('chat-message', message));
    }

    skipSong(skip: boolean) {
        // @ts-ignore
        this.songSocket.next(new SocketMessageDTO('skip-song', skip));
    }

    init() {
        // Return if Player is already created
        if (window['YT']) {
            this.startVideo();
            return;
        }

        const tag = document.createElement('script');
        tag.src = 'http://www.youtube.com/iframe_api';
        const firstScriptTag = document.getElementsByTagName('script')[0];
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

        /* 3. startVideo() will create an <iframe> (and YouTube player) after the API code downloads. */
        window['onYouTubeIframeAPIReady'] = () => this.startVideo();
    }

    startVideo() {
        this.reframed = false;
        this.player = new window['YT'].Player('player', {
            videoId: this.currentSongUrl,
            startSeconds: this.currentSongTimer,
            playerVars: {
                autoplay: 1,
                modestbranding: 1,
                controls: 1,
                disablekb: 1,
                rel: 0,
                showinfo: 0,
                fs: 0,
                playsinline: 1
            },
            events: {
                'onStateChange': this.onPlayerStateChange.bind(this),
                'onError': this.onPlayerError.bind(this),
                'onReady': this.onPlayerReady.bind(this),
            }
        });
    }

    onPlayerReady(event) {
        if (this.isRestricted) {
            event.target.mute();
            event.target.playVideo();
        } else {
            event.target.playVideo();
        }
    }

    onPlayerStateChange(event) {
        console.log(event);
        switch (event.data) {
            case window['YT'].PlayerState.PLAYING:
                if (this.cleanTime() === 0) {
                    console.log('started ' + this.cleanTime());
                } else {
                    console.log('playing ' + this.cleanTime())
                }
                break;
            case window['YT'].PlayerState.PAUSED:
                if (this.player.getDuration() - this.player.getCurrentTime() !== 0) {
                    console.log('paused' + ' @ ' + this.cleanTime());
                }
                break;
            case window['YT'].PlayerState.ENDED:
                console.log('ended ');
                break;
        }
    }

    cleanTime() {
        return Math.round(this.player.getCurrentTime());
    }

    onPlayerError(event) {
        switch (event.data) {
            case 2:
                console.log('' + this.video);
                break;
            case 100:
                break;
            case 101 || 150:
                break;
        }
    }

    changeVolume(volume: number) {
        this.player.setVolume(volume);
    }
}

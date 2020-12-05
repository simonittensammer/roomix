import { Component, OnInit } from '@angular/core';
import {Room} from '../../models/room';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {RoomService} from '../room.service';
import {first} from 'rxjs/operators';
import {PlaylistService} from '../playlist/playlist.service';
import {Song} from '../../models/song';
import {AccountService} from '../../account/account.service';
import {PlaySongService} from './play-song.service';
import {DomSanitizer} from '@angular/platform-browser';
import {User} from '../../models/user';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss'],
})
export class RoomComponent implements OnInit {

  user: User;
  room: Room;
  friendUserName: string;

  constructor(
      private playlistService: PlaylistService,
      private route: ActivatedRoute,
      private roomService: RoomService,
      private router: Router,
      private accountService: AccountService,
      private playSongService: PlaySongService,
      private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.route.params.subscribe(
        (params: Params) => {
          this.roomService.getRoom(params.id)
              .pipe(first())
              .subscribe(data => {
                this.room = data;
                console.log(this.room);
                this.playSongService.connect(this.accountService.userValue.username, this.room.id);
                console.log(this.playSongService.currentSong);
                this.user = this.accountService.userValue;
                this.roomService.updateRoomValue(this.room);
              });
        }
    );
    console.log(this.playSongService.currentSong);
  }

  sanitize(songUrl: string, songStart: number) {
      console.log('https://www.youtube.com/embed/' + songUrl + '?start=' + songStart + '&controls=1&amp&autoplay=1');
      return this.sanitizer.bypassSecurityTrustResourceUrl('https://www.youtube.com/embed/' + songUrl + '?start=' + songStart + '&controls=1&amp&autoplay=1');
  }

  showPlaylist() {
      this.router.navigate(['playlist', this.room.id], {relativeTo: this.route});
  }

  sendRoomInvite() {
      this.accountService.sendRoomInvite(this.room.id, this.user.username, this.friendUserName)
          .pipe(first())
          .subscribe(data => {
              console.log(data);
          });
  }
}

import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {Room} from '../../../../models/room';
import {RoomService} from '../../../../services/room.service';
import {Song} from '../../../../models/song';
import {PlaylistService} from '../../../../services/playlist.service';
import {PlaySongService} from '../../../../services/play-song.service';
import {GlobalConstants} from '../../../../helpers/globalConstants';
import {User} from '../../../../models/user';
import {UserService} from '../../../../services/user.service';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.scss'],
})
export class PlaylistComponent implements OnInit {

  collapsed: boolean;
  roles: Array<string>;
  user: User;
  room: Room;
  listeningRoom: Room;

  constructor(
      private playlistService: PlaylistService,
      private playSongService: PlaySongService,
      private userService: UserService,
      private route: ActivatedRoute,
      private roomService: RoomService
  ) { }

  ngOnInit() {
      this.roomService.roomValue.subscribe(
          value => {
              this.room = value;
              this.playSongService.roomValue.subscribe( value2 => {
                  this.listeningRoom = value2;
              });
          }
      );
      this.userService.userValue.subscribe(value => {
          this.user = value;
          console.log(this.user.activeMember.role);
      });
      this.roles = GlobalConstants.ROLES;
  }

    deleteFromPlaylist(song: Song) {
        this.playlistService.deleteSongFromPlaylist(this.room.id, song.url)
            .pipe(first())
            .subscribe(data => {
                console.log(data);
                // this.room.playlist.songList.splice(this.room.playlist.songList.findIndex(x => x.url === song.url), 1);
            });
    }

    collapseList() {
        this.collapsed = !this.collapsed;
    }
}

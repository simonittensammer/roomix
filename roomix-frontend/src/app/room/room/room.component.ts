import { Component, OnInit } from '@angular/core';
import {Room} from '../../models/room';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {RoomService} from '../../services/room.service';
import {first} from 'rxjs/operators';
import {PlaylistService} from '../../services/playlist.service';
import {Song} from '../../models/song';
import {AccountService} from '../../services/account.service';
import {PlaySongService} from '../../services/play-song.service';
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
  collapsed: boolean;

  constructor(
      private playlistService: PlaylistService,
      private route: ActivatedRoute,
      private roomService: RoomService,
      private router: Router,
      private accountService: AccountService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(
        (params: Params) => {
          this.roomService.getRoom(params.id)
              .pipe(first())
              .subscribe(data => {
                this.room = data;
                console.log(this.room);
                this.accountService.userValue.subscribe(
                      value => {
                          this.user = value;
                      }
                );

                if (!this.roomService.oldRoom || this.room.id !== this.roomService.oldRoom.id) {
                   this.roomService.oldRoom = this.room;
                   this.roomService.updateRoomValue(this.room);
                }
              });
        }
    );
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

    collapseList() {
        this.collapsed = !this.collapsed;
    }
}

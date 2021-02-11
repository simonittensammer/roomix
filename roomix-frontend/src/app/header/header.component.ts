import {Component, OnChanges, OnInit} from '@angular/core';
import {AccountService} from '../services/account.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {User} from '../models/user';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
import {PlaySongService} from '../services/play-song.service';
import {UserSocketService} from '../services/user-socket.service';
import {UserService} from '../services/user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {

  user: User;
  room: Room;

  constructor(
      private accountService: AccountService,
      private roomService: RoomService,
      private userService: UserService,
      private userSocketService: UserSocketService,
      private playSongService: PlaySongService,
      private router: Router,
      private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.playSongService.roomValue.subscribe(
        value => {
          this.room = value;
        }
    );
    this.userService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
    if (this.user.username !== null) {
        this.userSocketService.connect(this.user.username);
    }
  }

  logout() {
    if (this.playSongService.connected) {
      this.playSongService.disconnect();
    }
    this.user = null;
    this.accountService.logout();
  }
}

import {Component, OnChanges, OnInit} from '@angular/core';
import {AccountService} from '../services/account.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {User} from '../models/user';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
import {PlaySongService} from '../services/play-song.service';

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
    this.accountService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
  }

  logout() {
    this.playSongService.disconnect();
    this.user = null;
    this.accountService.logout();
  }
}

import {Component, OnChanges, OnInit} from '@angular/core';
import {AccountService} from '../services/account.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {User} from '../models/user';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
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
      private router: Router,
      private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.roomService.roomValue.subscribe(
        value => {
          this.room = value;
          this.userService.userValue.subscribe(
              value2 => {
                this.user = value2;
              }
          );
        }
    );
    if (this.user.username !== null) {
        this.userSocketService.connect(this.user.username);
    }
  }

  logout() {
    this.user = null;
    this.accountService.logout();
  }
}

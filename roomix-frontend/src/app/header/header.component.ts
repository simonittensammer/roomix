import {Component, OnChanges, OnInit} from '@angular/core';
import {AccountService} from '../services/account.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {User} from '../models/user';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {

  isLoggedIn$: Observable<boolean>;
  user: User;
  room: Room;

  constructor(
      private accountService: AccountService,
      private roomService: RoomService,
      private router: Router,
      private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.isLoggedIn$ = this.accountService.isLoggedIn;
    this.roomService.roomValue.subscribe(
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
    this.user = null;
    this.accountService.logout();
  }

}

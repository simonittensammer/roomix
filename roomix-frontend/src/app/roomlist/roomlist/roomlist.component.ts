import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Room} from '../../models/room';
import {first} from 'rxjs/operators';
import {RoomlistService} from '../../services/roomlist.service';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-roomlist',
  templateUrl: './roomlist.component.html',
  styleUrls: ['./roomlist.component.scss'],
})
export class RoomlistComponent implements OnInit {

  user: User;

  constructor(
      private accountService: AccountService,
      private router: Router,
      private userService: UserService,
      private roomListService: RoomlistService
  ) { }

  ngOnInit() {
    this.userService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
  }

  logout() {
    this.accountService.logout();
  }

  showRoom(room: Room) {
    this.router.navigate(['room', room.id]);
  }

}

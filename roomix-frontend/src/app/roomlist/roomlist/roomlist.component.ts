import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Room} from '../../models/room';
import {first} from 'rxjs/operators';

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
  ) { }

  ngOnInit() {
    this.accountService.userValue.subscribe(
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

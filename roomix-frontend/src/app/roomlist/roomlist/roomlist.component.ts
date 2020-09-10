import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../account/account.service';
import {User} from '../../models/user';
import {ActivatedRoute, Router} from '@angular/router';
import {Room} from '../../models/room';

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
      private route: ActivatedRoute,
  ) {
    this.user = this.accountService.userValue;
  }

  ngOnInit() {
    console.log(this.accountService.userValue);
  }

  logout() {
    this.accountService.logout();
  }

  showRoom(room: Room) {
    this.router.navigate(['room', room.id]);
  }

}

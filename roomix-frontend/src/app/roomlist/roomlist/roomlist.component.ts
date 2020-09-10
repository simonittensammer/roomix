import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../account/account.service';
import {User} from '../../models/user';

@Component({
  selector: 'app-roomlist',
  templateUrl: './roomlist.component.html',
  styleUrls: ['./roomlist.component.scss'],
})
export class RoomlistComponent implements OnInit {

  user: User;

  constructor(private accountService: AccountService) {
    this.user = this.accountService.userValue;
  }

  ngOnInit() {
    console.log(this.accountService.userValue);
  }

  logout() {
    this.accountService.logout();
  }

}

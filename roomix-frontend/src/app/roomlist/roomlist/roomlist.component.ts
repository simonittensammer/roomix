import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../account/account.service';

@Component({
  selector: 'app-roomlist',
  templateUrl: './roomlist.component.html',
  styleUrls: ['./roomlist.component.scss'],
})
export class RoomlistComponent implements OnInit {

  constructor(private accountService: AccountService) { }

  ngOnInit() {}

  logout() {
    this.accountService.logout();
  }

}

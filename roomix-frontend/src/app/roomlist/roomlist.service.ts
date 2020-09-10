import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AccountService} from '../account/account.service';
import {Member} from '../models/member';
import {Room} from '../models/room';

@Injectable({
  providedIn: 'root'
})
export class RoomlistService {

  constructor(
      private router: Router,
      private http: HttpClient,
      private accountService: AccountService
  ) { }

  createNewRoom(roomname) {
    //this.accountService.userValue.memberList.push(new Member(this.accountService.userValue, new Room(roomname), 'owner'));
    console.log(this.accountService.userValue);
    //this.accountService.addRoom(this.accountService.userValue);
  }
}

import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AccountService} from './account.service';
import {Member} from '../models/member';
import {Room} from '../models/room';
import {User} from '../models/user';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RoomlistService {

  user: User;
  createRoomVisible: boolean;

  constructor(
      private router: Router,
      private http: HttpClient,
      private accountService: AccountService
  ) { }

  createNewRoom(username, roomInput) {
    return this.http.post<User>(GlobalConstants.apiUrl + '/room', { username: username, roomname: roomInput.name })
        .pipe(map(user => {
          return user;
        }));
  }

  showCreateRoom() {
      this.createRoomVisible = !this.createRoomVisible;
  }
}

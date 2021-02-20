import { Injectable } from '@angular/core';
import {User} from '../models/user';
import {BehaviorSubject} from 'rxjs';
import {Member} from '../models/member';
import {GlobalConstants} from '../helpers/globalConstants';
import {FriendRequest} from '../models/friend-request';
import {RoomInvite} from '../models/room-invite';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userSubject: BehaviorSubject<User>;

  constructor(
      private http: HttpClient
  ) {
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
  }

  public get userValue() {
    return this.userSubject.asObservable();
  }

  public updateUserValue(user: User) {
    localStorage.setItem('user', JSON.stringify(user));
    this.userSubject.next(user);
  }

  getProperMemberList(username) {
    return this.http.get<Member[]>(GlobalConstants.apiUrl + '/user/' + username + '/members');
  }

  getProperFriendList(username) {
    return this.http.get<User[]>(GlobalConstants.apiUrl + '/user/' + username + '/friends');
  }

  getProperFriendRequestList(username) {
    return this.http.get<FriendRequest[]>(GlobalConstants.apiUrl + '/user/' + username + '/friendRequests');
  }

  getProperRoomInviteList(username) {
    return this.http.get<RoomInvite[]>(GlobalConstants.apiUrl + '/user/' + username + '/roomInvites');
  }

  updateUser(username: string, image: string, displayname: string, password: string) {
    return this.http.post<User>(GlobalConstants.apiUrl + '/user/update', {username, image, displayname, password});
  }
}

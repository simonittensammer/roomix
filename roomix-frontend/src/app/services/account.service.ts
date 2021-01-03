import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {User} from '../models/user';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Member} from '../models/member';
import {FriendRequest} from '../models/friend-request';
import {RoomInvite} from '../models/room-invite';
import {RoomService} from './room.service';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private userSubject: BehaviorSubject<User>;

  constructor(
      private router: Router,
      private http: HttpClient,
      private roomService: RoomService
  ) {
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
  }

  public updateIsLoggedIn(loggedIn: boolean) {
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

  login(username, password) {
    return this.http.post<User>(GlobalConstants.apiUrl + '/user/login', { username, password })
        .pipe(map(user => {
          return user;
        }));
  }

  logout() {
    // remove user from local storage and set current user to null
    // localStorage.removeItem('user');
    // localStorage.removeItem('room');
    this.roomService.updateRoomValue(null);
    // this.userSubject.next(null);
    this.updateUserValue(null);
    this.router.navigate(['/login']);
  }

  register(user: User) {
    return this.http.post(GlobalConstants.apiUrl + '/user', user);
  }

  sendFriendRequest(sender: string, receiver: string) {
    return this.http.post(GlobalConstants.apiUrl + '/user/friendRequest', {sender, receiver});
  }

  friendRequestResponse(friendRequest: FriendRequest, accept: boolean) {
    return this.http.get(GlobalConstants.apiUrl + '/user/' + friendRequest.receiver.username + '/friendRequests/' + friendRequest.id + '/' + accept);
  }

  sendRoomInvite(roomId: number, sender: string, receiver: string) {
    return this.http.post(GlobalConstants.apiUrl + '/user/roomInvite', {roomId, sender, receiver});
  }

  roomInviteResponse(roomInvite: RoomInvite, accept: boolean) {
    return this.http.get(GlobalConstants.apiUrl + '/user/' + roomInvite.receiver.username + '/roomInvites/' + roomInvite.id + '/' + accept);
  }

}

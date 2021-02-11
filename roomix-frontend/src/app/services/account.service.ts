import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from '../models/user';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Member} from '../models/member';
import {FriendRequest} from '../models/friend-request';
import {RoomInvite} from '../models/room-invite';
import {RoomService} from './room.service';
import {FriendRequestDTO} from '../models/dto/friendRequestDTO';
import {RoomInviteDTO} from '../models/dto/roomInviteDTO';
import {UserSocketService} from './user-socket.service';
import {UserService} from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(
      private router: Router,
      private http: HttpClient,
      private roomService: RoomService,
      private userSocketService: UserSocketService,
      private userService: UserService
) {}

  login(username, password) {
    return this.http.post<User>(GlobalConstants.apiUrl + '/user/login', { username, password })
        .pipe(map(user => {
          this.userSocketService.connect(user.username);
          return user;
        }));
  }

  logout() {
    // remove user from local storage and set current user to null
    // localStorage.removeItem('user');
    // localStorage.removeItem('room');
    this.roomService.updateRoomValue(null);
    // this.userSubject.next(null);
    this.userService.updateUserValue(null);
    this.router.navigate(['/login']);
  }

  register(user: User) {
    return this.http.post(GlobalConstants.apiUrl + '/user', user);
  }

  sendFriendRequest(sender: string, receiver: string): Observable<FriendRequest> {
    return this.http.post<FriendRequest>(GlobalConstants.apiUrl + '/user/friendRequest', new FriendRequestDTO(sender, receiver));
  }

  friendRequestResponse(friendRequest: FriendRequest, accept: boolean) {
    return this.http.get(GlobalConstants.apiUrl + '/user/friendRequests/' + friendRequest.id + '/' + accept);
  }

  sendRoomInvite(roomId: number, sender: string, receiver: string): Observable<RoomInvite> {
    return this.http.post<RoomInvite>(GlobalConstants.apiUrl + '/user/roomInvite', new RoomInviteDTO(sender, receiver, roomId));
  }

  roomInviteResponse(roomInvite: RoomInvite, accept: boolean) {
    return this.http.get(GlobalConstants.apiUrl + '/user/roomInvites/' + roomInvite.id + '/' + accept);
  }

}

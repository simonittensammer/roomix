import {Injectable} from '@angular/core';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {SocketMessageDTO} from '../models/dto/SocketMessageDTO';
import {webSocket} from 'rxjs/webSocket';
import {AccountService} from './account.service';
import {UserService} from './user.service';
import {first} from 'rxjs/operators';
import {User} from '../models/user';
import {Room} from '../models/room';
import {RoomService} from './room.service';

@Injectable({
    providedIn: 'root'
})
export class UserSocketService {

    userSocket: WebSocketSubject<SocketMessageDTO>;
    user: User;
    room: Room;

    constructor(
        private userService: UserService,
        private roomService: RoomService
    ) {
    }

    connect(username) {
        this.userService.userValue.subscribe(
            value => {
                this.user = value;
            });
        this.roomService.roomValue.subscribe(
            value => {
                this.room = value;
            });

        this.userSocket = webSocket('ws://localhost:8080/user/' + username);
        console.log(username);
        this.userSocket.asObservable().subscribe(data => {

            if (data.type === 'receive-friend-request') {
                console.log('receive-friend-request');
                this.userService.getProperFriendRequestList(username)
                    .pipe(first())
                    .subscribe( value => {
                        this.user.friendRequestList = value;
                        this.userService.updateUserValue(this.user);
                    });
            }

            if (data.type === 'friend-request-response') {
                console.log('friend-request-response');
                this.userService.getProperFriendList(username)
                    .pipe(first())
                    .subscribe( value => {
                        this.user.friendList = value;
                        this.userService.updateUserValue(this.user);
                    });
            }

            if (data.type === 'receive-room-invite') {
                console.log('receive-room-invite');
                this.userService.getProperRoomInviteList(username)
                    .pipe(first())
                    .subscribe( value => {
                        this.user.roomInviteList = value;
                        this.userService.updateUserValue(this.user);
                    });
            }

            if (data.type === 'room-invite-response') {
                console.log('room-invite-response');
                console.log(data.message);
                this.roomService.getMembers(data.message)
                    .pipe(first())
                    .subscribe( value => {
                        this.room.memberList = value;
                        this.roomService.updateRoomValue(this.room);
                    });
                this.userService.getProperMemberList(username)
                    .pipe(first())
                    .subscribe( value => {
                        this.user.memberList = value;
                        this.userService.updateUserValue(this.user);
                    });
            }
        });
    }
}

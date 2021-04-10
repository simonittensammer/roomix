import {Injectable} from '@angular/core';
import {User} from '../models/user';
import {BehaviorSubject} from 'rxjs';
import {Member} from '../models/member';
import {GlobalConstants} from '../helpers/globalConstants';
import {FriendRequest} from '../models/friend-request';
import {RoomInvite} from '../models/room-invite';
import {HttpClient} from '@angular/common/http';
import {UserUpdateDTO} from '../models/dto/userUpdateDTO';
import {FriendRequestDTO} from '../models/dto/friendRequestDTO';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private userSubject: BehaviorSubject<User>;

    constructor(
        private http: HttpClient
    ) {
        // this.getUser(localStorage.getItem('username')).subscribe(user => {
        this.userSubject = new BehaviorSubject<User>(null);
        // });
        // this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
    }

    public get userValue() {
        return this.userSubject.asObservable();
    }

    public updateUserValue(user: User) {
        if (user) {
            localStorage.setItem('username', user.username);
        } else {
            localStorage.setItem('username', null);
        }
        this.userSubject.next(user);
    }

    getUser(username) {
        return this.http.get<User>(GlobalConstants.APIURL + '/user/' + username);
    }

    getProperMemberList(username) {
        return this.http.get<Member[]>(GlobalConstants.APIURL + '/user/' + username + '/members');
    }

    getProperFriendList(username) {
        return this.http.get<User[]>(GlobalConstants.APIURL + '/user/' + username + '/friends');
    }

    getProperFriendRequestList(username) {
        return this.http.get<FriendRequest[]>(GlobalConstants.APIURL + '/user/' + username + '/friendRequests');
    }

    getProperRoomInviteList(username) {
        return this.http.get<RoomInvite[]>(GlobalConstants.APIURL + '/user/' + username + '/roomInvites');
    }

    updateUser(username: string, image: string, displayname: string, password: string, resetProfilePic: boolean) {
        return this.http.put<User>(GlobalConstants.APIURL + '/user', new UserUpdateDTO(username, displayname, password, image, resetProfilePic));
    }

    unfriend(friendRequestDTO: FriendRequestDTO) {
        return this.http.put(GlobalConstants.APIURL + '/user/unfriend', friendRequestDTO);
    }
}

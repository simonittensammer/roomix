import {Injectable} from '@angular/core';
import {User} from '../models/user';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {FriendRequest} from '../models/friend-request';
import {RoomInvite} from '../models/room-invite';
import {RoomService} from './room.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {UserSocketService} from './user-socket.service';
import {UserService} from './user.service';
import {FriendRequestDTO} from '../models/dto/friendRequestDTO';
import {RoomInviteDTO} from '../models/dto/roomInviteDTO';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    addFriendVisible: boolean;
    editProfileVisible: boolean;

    constructor(
        private router: Router,
        private http: HttpClient,
        private roomService: RoomService,
        private sanitizer: DomSanitizer,
        private userSocketService: UserSocketService,
        private userService: UserService
    ) {
    }


    login(username, password) {
        return this.http.post<User>(GlobalConstants.apiUrl + '/user/login', {username, password})
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

    register(user: User, base64textString: string) {
        user.picUrl = base64textString;
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

    searchUserWithMatchingName(username: string, searchTerm: string): Observable<User[]> {
        return this.http.get<Array<User>>(GlobalConstants.apiUrl + '/user/' + username + '/search?searchTerm=' + searchTerm);
    }

    searchFriendsWithMatchingName(username: string, roomId: number, searchTerm: string) {
        return this.http.get<Array<User>>(GlobalConstants.apiUrl + '/user/' + username + '/' + roomId + '/friends/search?searchTerm=' + searchTerm);
    }

    showAddFriend() {
        this.addFriendVisible = !this.addFriendVisible;
    }

    public sanitizeBase64(picUrl: string): SafeUrl {
        return this.sanitizer.bypassSecurityTrustUrl('data:image/jpg;base64, ' + picUrl);
    }

    showEditProfile() {
        this.editProfileVisible = !this.editProfileVisible;
    }
}

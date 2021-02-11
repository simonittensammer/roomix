import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Room} from '../models/room';
import {BehaviorSubject} from 'rxjs';
import {Member} from '../models/member';
import {User} from '../models/user';
import {MemberDTO} from '../models/dto/memberDTO';

@Injectable({
    providedIn: 'root'
})
export class RoomService {
    private roomSubject: BehaviorSubject<Room>;
    oldRoom: Room = null;
    inviteFriendVisible: boolean;

    constructor(
        private http: HttpClient,
    ) {
        this.roomSubject = new BehaviorSubject<Room>(JSON.parse(localStorage.getItem('room')));
    }

    getRoom(id) {
        return this.http.get<Room>(GlobalConstants.apiUrl + '/room/' + id)
            .pipe(map(room => {
                return room;
            }));
    }

    public get roomValue() {
        return this.roomSubject.asObservable();
    }

    public updateRoomValue(room: Room) {
        localStorage.setItem('room', JSON.stringify(room));
        this.roomSubject.next(room);
    }

    public getMembers(id) {
        return this.http.get<Array<Member>>(GlobalConstants.apiUrl + '/room/' + id + '/members')
            .pipe(map(members => {
                return members;
            }));
    }

    showInviteFriend() {
        this.inviteFriendVisible = !this.inviteFriendVisible;
    }

    getPopularPublicRooms(limit: number) {
        return this.http.get<Array<Room>>(GlobalConstants.apiUrl + '/room/popular/?limit=' + limit)
            .pipe(map(rooms => {
                return rooms;
            }));
    }

    addMember(username: string, roomId: number) {
        return this.http.post(GlobalConstants.apiUrl + '/room/member', new MemberDTO(username, roomId));
    }
}

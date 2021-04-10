import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Room} from '../models/room';
import {BehaviorSubject, Observable} from 'rxjs';
import {Member} from '../models/member';
import {User} from '../models/user';
import {MemberDTO} from '../models/dto/memberDTO';
import {ChatMessageDTO} from '../models/dto/chatMessageDTO';
import {RoomUpdateDTO} from '../models/dto/roomUpdateDTO';
import {Tag} from '../models/tag';

@Injectable({
    providedIn: 'root'
})
export class RoomService {
    private roomSubject: BehaviorSubject<Room>;
    inviteFriendVisible: boolean;
    editRoomVisible: boolean;

    constructor(
        private http: HttpClient,
    ) {
        this.roomSubject = new BehaviorSubject<Room>(JSON.parse(localStorage.getItem('room')));
    }

    getRoom(id) {
        return this.http.get<Room>(GlobalConstants.APIURL + '/room/' + id)
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
        return this.http.get<Array<Member>>(GlobalConstants.APIURL + '/room/' + id + '/members')
            .pipe(map(members => {
                return members;
            }));
    }

    showInviteFriend() {
        this.inviteFriendVisible = !this.inviteFriendVisible;
    }

    getPopularPublicRooms(limit: number, searchTerm: string, tagList: Tag[]) {
        const tags = tagList.map(tag => tag.name).join(',');
        return this.http.get<Array<Room>>(GlobalConstants.APIURL + '/room/popular?searchTerm=' + searchTerm + '&limit=' + limit + '&tags=' + tags)
            .pipe(map(rooms => {
                return rooms;
            }));
    }

    addMember(username: string, roomId: number) {
        return this.http.post(GlobalConstants.APIURL + '/room/member', new MemberDTO(username, roomId));
    }

    removeMember(username: string, roomId: number) {
        return this.http.delete(GlobalConstants.APIURL + '/room/member?username=' + username + '&roomId=' + roomId);
    }

    getMessages(roomId: number): Observable<ChatMessageDTO[]> {
        return this.http.get<ChatMessageDTO[]>(GlobalConstants.APIURL + '/room/messages/' + roomId);
    }

    showEditRoom() {
        this.editRoomVisible = !this.editRoomVisible;
    }

    deleteRoom(roomId: number) {
        return this.http.delete(GlobalConstants.APIURL + '/room/' + roomId);
    }

    updateRoom(roomId: number, image: string, name: string, isPrivate: boolean, tagList: Tag[]) {
        return this.http.put<Room>(GlobalConstants.APIURL + '/room', new RoomUpdateDTO(roomId, name, isPrivate, image, tagList));
    }

    updateRole(roomId: number, member: Member, newRole: string) {
        return this.http.put<Member>(GlobalConstants.APIURL + '/room/' + roomId + '/member/' + member.user.username + '?role=' + newRole, null);
    }
}

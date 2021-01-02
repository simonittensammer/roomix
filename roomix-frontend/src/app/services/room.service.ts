import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/user';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Room} from '../models/room';
import {BehaviorSubject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class RoomService {
    private roomSubject: BehaviorSubject<Room>;

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
}

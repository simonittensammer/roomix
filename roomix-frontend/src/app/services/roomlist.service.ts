import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {AccountService} from './account.service';
import {Member} from '../models/member';
import {Room} from '../models/room';
import {User} from '../models/user';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {RoomDTO} from '../models/dto/roomDTO';
import {Tag} from '../models/tag';

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
    ) {
    }

    createNewRoom(username, roomname, isPrivate, base64textString: string, tagList: Tag[]) {
        return this.http.post<User>(GlobalConstants.APIURL + '/room', new RoomDTO(username, roomname, isPrivate, base64textString, tagList))
            .pipe(map(user => {
                return user;
            }));
    }

    showCreateRoom() {
        this.createRoomVisible = !this.createRoomVisible;
    }
}

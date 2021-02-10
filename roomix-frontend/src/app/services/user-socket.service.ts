import {Injectable} from '@angular/core';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {SocketMessageDTO} from '../models/dto/SocketMessageDTO';
import {webSocket} from 'rxjs/webSocket';
import {AccountService} from './account.service';

@Injectable({
    providedIn: 'root'
})
export class UserSocketService {

    userSocket: WebSocketSubject<SocketMessageDTO>;

    constructor() {
    }


    connect(username) {
        this.userSocket = webSocket('ws://localhost:8080/user/' + username);

        this.userSocket.asObservable().subscribe(data => {

            if (data.type === 'receive-friend-request') {
                console.log('receive-friend-request');
            }

            if (data.type === 'friend-request-response') {
                console.log('friend-request-response');
            }

            if (data.type === 'receive-room-invite') {
                console.log('receive-room-invite');
            }

            if (data.type === 'room-invite-response') {
                console.log('room-invite-response');
            }
        });
    }
}

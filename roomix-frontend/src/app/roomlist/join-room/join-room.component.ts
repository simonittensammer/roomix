import { Component, OnInit } from '@angular/core';
import {User} from '../../models/user';
import {AccountService} from '../../services/account.service';
import {FriendRequest} from '../../models/friend-request';
import {first} from 'rxjs/operators';
import {RoomInvite} from '../../models/room-invite';

@Component({
  selector: 'app-join-room',
  templateUrl: './join-room.component.html',
  styleUrls: ['./join-room.component.scss'],
})
export class JoinRoomComponent implements OnInit {

  user: User;

  constructor(private accountService: AccountService) { }

  ngOnInit() {
    this.user = this.accountService.userValue;
  }

  response(roomInvite: RoomInvite, accept: boolean) {
    this.accountService.roomInviteResponse(roomInvite, accept)
        .pipe(first())
        .subscribe(data => {
          console.log(data);
        });
  }

}

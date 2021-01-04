import { Component, OnInit } from '@angular/core';
import {RoomService} from '../../services/room.service';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {first} from 'rxjs/operators';
import {Room} from '../../models/room';

@Component({
  selector: 'app-invite-friend',
  templateUrl: './invite-friend.component.html',
  styleUrls: ['./invite-friend.component.scss'],
})
export class InviteFriendComponent implements OnInit {

  user: User;
  room: Room;

  constructor(
      public roomService: RoomService,
      private accountService: AccountService
  ) { }

  ngOnInit() {
    this.roomService.roomValue.subscribe(
        value => {
          this.room = value;
        }
    );
    this.accountService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
  }

  sendRoomInvite(userName) {
    this.accountService.sendRoomInvite(this.room.id, this.user.username, userName)
        .pipe(first())
        .subscribe(data => {
          console.log(data);
        });
  }
}

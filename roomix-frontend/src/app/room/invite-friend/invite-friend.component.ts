import { Component, OnInit } from '@angular/core';
import {RoomService} from '../../services/room.service';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {first} from 'rxjs/operators';
import {Room} from '../../models/room';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-invite-friend',
  templateUrl: './invite-friend.component.html',
  styleUrls: ['./invite-friend.component.scss'],
})
export class InviteFriendComponent implements OnInit {

  user: User;
  room: Room;
  searchQuery = '';

  constructor(
      public roomService: RoomService,
      private accountService: AccountService,
      private userService: UserService
  ) { }

  ngOnInit() {
    this.roomService.roomValue.subscribe(
        value => {
          this.room = value;
        }
    );
    this.userService.userValue.subscribe(
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

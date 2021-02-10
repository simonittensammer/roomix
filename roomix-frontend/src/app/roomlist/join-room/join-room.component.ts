import { Component, OnInit } from '@angular/core';
import {User} from '../../models/user';
import {AccountService} from '../../services/account.service';
import {FriendRequest} from '../../models/friend-request';
import {first} from 'rxjs/operators';
import {RoomInvite} from '../../models/room-invite';
import {RoomService} from '../../services/room.service';
import {Room} from '../../models/room';
import {compareNumbers} from '@angular/compiler-cli/src/diagnostics/typescript_version';
import {Router} from '@angular/router';

@Component({
  selector: 'app-join-room',
  templateUrl: './join-room.component.html',
  styleUrls: ['./join-room.component.scss'],
})
export class JoinRoomComponent implements OnInit {

  user: User;
  publicRooms: Array<Room>;
  limit = 10;

  constructor(private accountService: AccountService,
              private router: Router,
              private roomService: RoomService) { }

  ngOnInit() {
    this.accountService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
    this.roomService.getPopularPublicRooms(this.limit).subscribe(
        publicRooms => {
            this.publicRooms = publicRooms;
        }
    );
  }

  response(roomInvite: RoomInvite, accept: boolean) {
    this.accountService.roomInviteResponse(roomInvite, accept)
        .pipe(first())
        .subscribe(data => {
          console.log(data);
        });
  }

  updatePublicRoomLimit() {
      if (this.limit >= 1) {
          this.roomService.getPopularPublicRooms(this.limit).subscribe(
              publicRooms => {
                  this.publicRooms = publicRooms;
              }
          );
      }
  }

    showRoom(room: Room) {
        this.router.navigate(['room', room.id]);
    }
}

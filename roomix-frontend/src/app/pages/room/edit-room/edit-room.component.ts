import { Component, OnInit } from '@angular/core';
import {RoomService} from '../../../services/room.service';
import {Room} from '../../../models/room';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {User} from '../../../models/user';
import {PlaySongService} from '../../../services/play-song.service';

@Component({
  selector: 'app-edit-room',
  templateUrl: './edit-room.component.html',
  styleUrls: ['./edit-room.component.scss'],
})
export class EditRoomComponent implements OnInit {

  room: Room;
  user: User;

  constructor(
      private roomService: RoomService,
      private userService: UserService,
      private playSongService: PlaySongService,
      private router: Router
  ) { }

  ngOnInit() {
    this.roomService.roomValue.subscribe(value => {
      this.room = value;
    });
    this.userService.userValue.subscribe(value => {
      this.user = value;
    });
  }

    onSubmit() {

    }

  deleteRoom() {
    this.roomService.deleteRoom(this.room.id).subscribe(value => {
      this.userService.getProperMemberList(this.user.username).subscribe(value2 => {
        this.user.memberList = value2;
        this.userService.updateUserValue(this.user);
        this.playSongService.disconnect();
        this.roomService.showEditRoom();
        this.router.navigate(['roomlist']);
      });
    });
  }
}

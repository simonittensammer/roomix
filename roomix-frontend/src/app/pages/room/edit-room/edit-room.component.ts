import { Component, OnInit } from '@angular/core';
import {RoomService} from '../../../services/room.service';
import {Room} from '../../../models/room';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {User} from '../../../models/user';
import {PlaySongService} from '../../../services/play-song.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {GlobalConstants} from '../../../helpers/globalConstants';
import {Tag} from '../../../models/tag';

@Component({
  selector: 'app-edit-room',
  templateUrl: './edit-room.component.html',
  styleUrls: ['./edit-room.component.scss'],
})
export class EditRoomComponent implements OnInit {

  room: Room;
  user: User;
  editRoomForm: FormGroup;
  base64textString = '';
  roles: Array<string>;

  constructor(
      private roomService: RoomService,
      private userService: UserService,
      private playSongService: PlaySongService,
      private router: Router
  ) {
    this.roles = GlobalConstants.ROLES;
  }

  ngOnInit() {
    this.roomService.roomValue.subscribe(value => {
      this.room = value;
    });
    this.userService.userValue.subscribe(value => {
      this.user = value;
      console.log(this.user.activeMember.role);
    });
    this.editRoomForm = new FormGroup({
      picUrl: new FormControl(null),
      name: new FormControl(this.room.name, Validators.required),
      isPrivate: new FormControl(this.room.private)
    });
    this.editRoomForm.value.picUrl = this.room.picUrl;
  }

  onSubmit() {
    if (this.editRoomForm.valid) {
      this.roomService.updateRoom(this.room.id, this.base64textString,
          this.editRoomForm.value.name, this.editRoomForm.value.isPrivate, this.room.tagList).subscribe(value => {
            this.room.name = value.name;
            this.room.private = value.private;
            this.room.picUrl = value.picUrl;
            this.roomService.updateRoomValue(this.room);
      });
    }
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

  handleFileSelect(evt) {
    const files = evt.target.files;
    const file = files[0];

    if (files && file) {
      const reader = new FileReader();

      reader.onload = this._handleReaderLoaded.bind(this);

      reader.readAsBinaryString(file);
    }
  }

  _handleReaderLoaded(readerEvt) {
    const binaryString = readerEvt.target.result;
    this.base64textString = btoa(binaryString);
  }

  childToParent(tagList: Tag[]){
    this.room.tagList = tagList;
  }
}

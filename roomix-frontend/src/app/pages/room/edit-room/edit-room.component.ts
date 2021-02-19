import { Component, OnInit } from '@angular/core';
import {RoomService} from '../../../services/room.service';

@Component({
  selector: 'app-edit-room',
  templateUrl: './edit-room.component.html',
  styleUrls: ['./edit-room.component.scss'],
})
export class EditRoomComponent implements OnInit {

  constructor(
      public roomService: RoomService
  ) { }

  ngOnInit() {}

    onSubmit() {

    }
}

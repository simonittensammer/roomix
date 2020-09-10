import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {RoomlistService} from '../roomlist.service';

@Component({
  selector: 'app-create-room',
  templateUrl: './create-room.component.html',
  styleUrls: ['./create-room.component.scss'],
})
export class CreateRoomComponent implements OnInit {

  newRoomForm: FormGroup;

  constructor(private roomlistService: RoomlistService) { }

  ngOnInit() {
    this.newRoomForm = new FormGroup({
      name: new FormControl('', Validators.required),
    });
  }

  onSubmit() {
    if (this.newRoomForm.valid) {
      console.log(this.newRoomForm.value);
      this.roomlistService.createNewRoom(this.newRoomForm.value);
    }
  }

}

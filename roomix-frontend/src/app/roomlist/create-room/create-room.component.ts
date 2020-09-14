import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {RoomlistService} from '../roomlist.service';
import {first} from 'rxjs/operators';
import {LoginComponent} from '../../account/login/login.component';
import {AccountService} from '../../account/account.service';

@Component({
  selector: 'app-create-room',
  templateUrl: './create-room.component.html',
  styleUrls: ['./create-room.component.scss'],
})
export class CreateRoomComponent implements OnInit {

  newRoomForm: FormGroup;

  constructor(
      private roomlistService: RoomlistService,
      private accountService: AccountService
  ) { }

  ngOnInit() {
    this.newRoomForm = new FormGroup({
      name: new FormControl('', Validators.required),
    });
  }

  onSubmit() {
    if (this.newRoomForm.valid) {
      console.log(this.newRoomForm.value);
      this.roomlistService.createNewRoom(this.newRoomForm.value)
          .pipe(first())
          .subscribe(data => {
            this.accountService.getProperMemberList(data.username)
                .pipe(first())
                .subscribe(data2 => {
                  data.memberList = data2;
                  this.accountService.updateUserValue(data);
                });
            console.log('done!');
          });
    }
  }

}
import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {RoomlistService} from '../../services/roomlist.service';
import {first} from 'rxjs/operators';
import {LoginComponent} from '../../account/login/login.component';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {Router} from '@angular/router';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-create-room',
  templateUrl: './create-room.component.html',
  styleUrls: ['./create-room.component.scss'],
})
export class CreateRoomComponent implements OnInit {

  user: User;
  newRoomForm: FormGroup;

  constructor(
      private roomlistService: RoomlistService,
      private accountService: AccountService,
      private userService: UserService,
      private router: Router
  ) { }

  ngOnInit() {
    this.newRoomForm = new FormGroup({
      // roomPic: new FormControl(null),
      name: new FormControl('', Validators.required),
      isPrivate: new FormControl(false)
    });
    this.userService.userValue.subscribe(
          value => {
              this.user = value;
          }
    );
  }

  onSubmit() {
    if (this.newRoomForm.valid) {
      console.log(this.newRoomForm.value);
      this.roomlistService.createNewRoom(this.user.username, this.newRoomForm.value.name, this.newRoomForm.value.isPrivate)
          .pipe(first())
          .subscribe(data => {
            this.userService.getProperMemberList(data.username)
                .pipe(first())
                .subscribe(data2 => {
                  data.memberList = data2;
                  this.userService.updateUserValue(data);
                });
            console.log('done!');
          });
    }
    this.roomlistService.showCreateRoom();
  }

}

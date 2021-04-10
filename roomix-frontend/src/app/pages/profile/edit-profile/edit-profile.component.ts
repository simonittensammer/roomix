import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../../services/account.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {User} from '../../../models/user';
import {UserService} from '../../../services/user.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss'],
})
export class EditProfileComponent implements OnInit {

  editProfileForm: FormGroup;
  base64textString = '';
  user: User;

  constructor(
      public accountService: AccountService,
      public userService: UserService
  ) { }

  ngOnInit() {
    this.userService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
    this.editProfileForm = new FormGroup({
      profilePic: new FormControl(null),
      username: new FormControl(this.user.username, Validators.required,),
      displayname: new FormControl(this.user.displayname, Validators.required),
      resetProfilePic: new FormControl(false),
      password: new FormControl(this.user.password, Validators.required)
    });
    this.editProfileForm.value.profilePic = this.user.picUrl;
    this.base64textString = this.user.picUrl;
  }

  onSubmit() {
    if (this.editProfileForm.valid) {
      this.userService.updateUser(this.user.username, this.base64textString, this.editProfileForm.value.displayname,
          this.editProfileForm.value.password, this.editProfileForm.value.resetProfilePic).subscribe(value => {
            this.user.displayname = value.displayname;
            this.user.password = value.password;
            this.user.picUrl = value.picUrl;
            this.userService.updateUserValue(this.user);
      });
    }
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
}

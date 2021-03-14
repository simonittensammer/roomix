import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {RoomlistService} from '../../../services/roomlist.service';
import {first} from 'rxjs/operators';
import {LoginComponent} from '../../../account/login/login.component';
import {AccountService} from '../../../services/account.service';
import {User} from '../../../models/user';
import {Router} from '@angular/router';
import {UserService} from '../../../services/user.service';
import {Tag} from '../../../models/tag';

@Component({
    selector: 'app-create-room',
    templateUrl: './create-room.component.html',
    styleUrls: ['./create-room.component.scss'],
})
export class CreateRoomComponent implements OnInit {

    user: User;
    newRoomForm: FormGroup;
    tagList: Tag[] = [];
    base64textString = '';

  constructor(
      private roomlistService: RoomlistService,
      private accountService: AccountService,
      private userService: UserService,
      private router: Router
  ) { }

    ngOnInit() {
        this.newRoomForm = new FormGroup({
            picUrl: new FormControl(null),
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
            this.roomlistService.createNewRoom(this.user.username, this.newRoomForm.value.name,
                this.newRoomForm.value.isPrivate, this.base64textString, this.tagList)
                .pipe(first())
                .subscribe(data => {
                    console.log(this.user);
                    console.log(data);
                    this.userService.getProperMemberList(data.username)
                        .pipe(first())
                        .subscribe(data2 => {
                            this.user.memberList = data2;
                            this.userService.updateUserValue(this.user);
                        });
                    console.log('done!');
                });
        }
        this.roomlistService.showCreateRoom();
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
        this.tagList = tagList;
    }
}

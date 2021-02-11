import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {first} from 'rxjs/operators';
import {AccountService} from '../../services/account.service';
import {LoginComponent} from '../login/login.component';
import {Router} from '@angular/router';
import {DomSanitizer, SafeResourceUrl, SafeUrl} from '@angular/platform-browser';
import {UserService} from '../../services/user.service';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {

    regForm: FormGroup;
    base64textString = '';
    test: SafeUrl;

  constructor(
      private accountService: AccountService,
      private userService: UserService,
      private sanitizer: DomSanitizer,
      private router: Router,
  ) { }

    ngOnInit(): void {
        this.regForm = new FormGroup({
            profilePic: new FormControl(null),
            username: new FormControl('', Validators.required),
            displayname: new FormControl('', Validators.required),
            password: new FormControl('', Validators.required)
        });
    }

    onSubmit() {
        if (this.regForm.valid) {
            this.accountService.register(this.regForm.value, this.base64textString)
                .pipe(first())
                .subscribe(registerData => {
                    const username = this.regForm.value.username;
                    const password = this.regForm.value.password;
                    this.accountService.login(username, password)
                        .pipe(first())
                        .subscribe(data => {
                            this.userService.getProperMemberList(data.username)
                                .pipe(first())
                                .subscribe(data2 => {
                                    data.memberList = data2;
                                    this.userService.updateUserValue(data);
                                    this.router.navigate(['roomlist']);
                                });
                        });
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

import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AccountService} from '../../../services/account.service';
import {first} from 'rxjs/operators';
import {Router} from '@angular/router';
import {User} from '../../../models/user';
import {UserService} from '../../../services/user.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {

    user: User;
    loginForm: FormGroup;

    constructor(
        private accountService: AccountService,
        private userService: UserService,
        private router: Router
    ) {
    }

    ngOnInit(): void {
        this.loginForm = new FormGroup({
            username: new FormControl('', Validators.required),
            password: new FormControl('', Validators.required)
        });

        this.userService.userValue.subscribe(
            value => {
                this.user = value;
            }
        );

        if (localStorage.getItem('username') && localStorage.getItem('id_token')) {
            this.userService.getUser(localStorage.getItem('username')).subscribe(value2 => {
                this.userService.updateUserValue(value2);
                this.login(value2.username, value2.password);
            });
        }
    }

    onSubmit() {
        if (this.loginForm.valid) {
            this.login(this.loginForm.value.username, this.loginForm.value.password);
        }
    }

    login(username: string, password: string) {
        this.accountService.login(username, password)
            .pipe(first())
            .subscribe(data => {
                this.userService.getProperMemberList(data.username)
                    .pipe(first())
                    .subscribe(data2 => {
                        data.memberList = data2;
                        this.userService.getProperFriendRequestList(data.username)
                            .pipe(first())
                            .subscribe(data3 => {
                                data.friendRequestList = data3;
                                this.userService.getProperFriendList(data.username)
                                    .pipe(first())
                                    .subscribe(data4 => {
                                        data.friendList = data4;
                                        this.userService.getProperRoomInviteList(data.username)
                                            .pipe(first())
                                            .subscribe(data5 => {
                                                data.roomInviteList = data5;
                                                this.userService.updateUserValue(data);
                                                this.router.navigate(['roomlist']);
                                            });
                                    });
                            });
                    });
            });
    }

}

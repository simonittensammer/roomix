import {Component, OnInit} from '@angular/core';
import {first} from 'rxjs/operators';
import {AccountService} from '../../../../services/account.service';
import {User} from '../../../../models/user';
import {UserService} from '../../../../services/user.service';

@Component({
    selector: 'app-add-friend',
    templateUrl: './add-friend.component.html',
    styleUrls: ['./add-friend.component.scss'],
})
export class AddFriendComponent implements OnInit {

    user: User;
    friendUserName: string;

    constructor(
        private accountService: AccountService,
        private userService: UserService
    ) {}

    ngOnInit() {
        this.userService.userValue.subscribe(
            value => {
                this.user = value;
            }
        );
    }

    sendFriendRequest() {
        this.accountService.sendFriendRequest(this.user.username, this.friendUserName)
            .pipe(first())
            .subscribe(data => {
                console.log(data);
            });
    }
}

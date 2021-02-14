import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {debounceTime, distinctUntilChanged, filter, first, map, pluck} from 'rxjs/operators';
import {AccountService} from '../../../../services/account.service';
import {User} from '../../../../models/user';
import {UserService} from '../../../../services/user.service';
import {fromEvent} from 'rxjs';

@Component({
    selector: 'app-add-friend',
    templateUrl: './add-friend.component.html',
    styleUrls: ['./add-friend.component.scss'],
})
export class AddFriendComponent implements OnInit, AfterViewInit {

    @ViewChild('input') inputElement: ElementRef;
    user: User;
    possibleFriends: Array<User>;

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

    sendFriendRequest(username: string) {
        this.accountService.sendFriendRequest(this.user.username, username)
            .pipe(first())
            .subscribe(data => {
                console.log(data);
            });
    }

    ngAfterViewInit() {
        fromEvent(this.inputElement.nativeElement, 'keyup')
            .pipe(
                debounceTime(500),
                pluck('target', 'value'),
                distinctUntilChanged(),
                filter((value: string) => value.length > 3),
                map((value) => value.split(' ').join('+'))
            ).subscribe(value => {
                console.log(value);
                this.accountService.searchUserWithMatchingName(value).subscribe(users => {
                    this.possibleFriends = users;
                });
            });
    }
}

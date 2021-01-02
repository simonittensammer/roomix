import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {first} from 'rxjs/operators';
import {FriendRequest} from '../../models/friend-request';

@Component({
  selector: 'app-friend-requests',
  templateUrl: './friend-requests.component.html',
  styleUrls: ['./friend-requests.component.scss'],
})
export class FriendRequestsComponent implements OnInit {

  user: User;
  collapsed: boolean;

  constructor(
      private accountService: AccountService,
  ) { }

  ngOnInit() {
    this.accountService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
  }

  response(friendRequest: FriendRequest, accept: boolean) {
    this.accountService.friendRequestResponse(friendRequest, accept)
        .pipe(first())
        .subscribe(data => {
          console.log(data);
        });
  }

    collapseList() {
        this.collapsed = !this.collapsed;
    }
}

import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {ActivatedRoute, Router} from '@angular/router';
import {RoomService} from '../../services/room.service';
import {first} from 'rxjs/operators';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-friendlist',
  templateUrl: './friendlist.component.html',
  styleUrls: ['./friendlist.component.scss'],
})
export class FriendlistComponent implements OnInit {

  user: User;
  friendUserName: string;
  collapsed: boolean;

  constructor(
      private accountService: AccountService,
      private route: ActivatedRoute,
      private userService: UserService,
      private router: Router
  ) { }

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

  showRequests() {
    this.router.navigate(['requests'], {relativeTo: this.route});
  }

  collapseList() {
    this.collapsed = !this.collapsed;
  }
}

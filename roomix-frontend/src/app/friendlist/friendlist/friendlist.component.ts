import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../account/account.service';
import {User} from '../../models/user';
import {ActivatedRoute, Router} from '@angular/router';
import {RoomService} from '../../room/room.service';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-friendlist',
  templateUrl: './friendlist.component.html',
  styleUrls: ['./friendlist.component.scss'],
})
export class FriendlistComponent implements OnInit {

  user: User;
  friendUserName: string;

  constructor(
      private accountService: AccountService,
      private route: ActivatedRoute,
      private router: Router
  ) { }

  ngOnInit() {
    this.user = this.accountService.userValue;
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

}

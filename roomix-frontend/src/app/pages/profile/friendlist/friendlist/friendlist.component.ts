import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../../../services/account.service';
import {User} from '../../../../models/user';
import {ActivatedRoute, Router} from '@angular/router';
import {RoomService} from '../../../../services/room.service';
import {first} from 'rxjs/operators';
import {UserService} from '../../../../services/user.service';
import {FriendRequestDTO} from '../../../../models/dto/friendRequestDTO';

@Component({
  selector: 'app-friendlist',
  templateUrl: './friendlist.component.html',
  styleUrls: ['./friendlist.component.scss'],
})
export class FriendlistComponent implements OnInit {

  user: User;
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

  showRequests() {
    this.router.navigate(['requests'], {relativeTo: this.route});
  }

  collapseList() {
    this.collapsed = !this.collapsed;
  }

  unfriend(friend: string) {
    this.userService.unfriend(new FriendRequestDTO(this.user.username, friend)).subscribe(value => {
      console.log(value);
    });
  }
}

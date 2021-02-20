import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RoomService} from '../../../services/room.service';
import {AccountService} from '../../../services/account.service';
import {User} from '../../../models/user';
import {debounceTime, distinctUntilChanged, filter, first, map, pluck} from 'rxjs/operators';
import {Room} from '../../../models/room';
import {UserService} from '../../../services/user.service';
import {fromEvent} from 'rxjs';

@Component({
  selector: 'app-invite-friend',
  templateUrl: './invite-friend.component.html',
  styleUrls: ['./invite-friend.component.scss'],
})
export class InviteFriendComponent implements OnInit, AfterViewInit {

  user: User;
  room: Room;
  searchQuery = '';
  possibleInvites: Array<User>;
  @ViewChild('input') inputElement: ElementRef;

  constructor(
      public roomService: RoomService,
      private accountService: AccountService,
      private userService: UserService
  ) { }

  ngOnInit() {
    this.roomService.roomValue.subscribe(
        value => {
          this.room = value;
          this.userService.userValue.subscribe(
              value2 => {
                  this.user = value2;
                  this.accountService.searchFriendsWithMatchingName(this.user.username, this.room.id, '').subscribe(users => {
                      this.possibleInvites = users;
                  });
                }
            );
        }
    );
  }

  sendRoomInvite(userName) {
    this.accountService.sendRoomInvite(this.room.id, this.user.username, userName)
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
                filter((value: string) => value.length > -1),
                map((value) => value.split(' ').join('+'))
            ).subscribe(value => {
            console.log(value);
            this.accountService.searchFriendsWithMatchingName(this.user.username, this.room.id, value).subscribe(users => {
                this.possibleInvites = users;
            });
        });
    }
}

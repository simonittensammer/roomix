import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {User} from '../../models/user';
import {AccountService} from '../../services/account.service';
import {FriendRequest} from '../../models/friend-request';
import {debounceTime, distinctUntilChanged, filter, first, map, pluck} from 'rxjs/operators';
import {RoomInvite} from '../../models/room-invite';
import {RoomService} from '../../services/room.service';
import {Room} from '../../models/room';
import {compareNumbers} from '@angular/compiler-cli/src/diagnostics/typescript_version';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {fromEvent} from 'rxjs';

@Component({
    selector: 'app-join-room',
    templateUrl: './join-room.component.html',
    styleUrls: ['./join-room.component.scss'],
})
export class JoinRoomComponent implements OnInit, AfterViewInit {

    @ViewChild('input') inputElement: ElementRef;
    user: User;
    publicRooms: Array<Room>;
    limit = 10;

    constructor(private accountService: AccountService,
                private userService: UserService,
                private router: Router,
                private roomService: RoomService) {
    }

    ngOnInit() {
        this.userService.userValue.subscribe(
            value => {
                this.user = value;
            }
        );
        this.roomService.getPopularPublicRooms(this.limit, '').subscribe(
            publicRooms => {
                this.publicRooms = publicRooms;
            }
        );
    }

    response(roomInvite: RoomInvite, accept: boolean) {
        this.accountService.roomInviteResponse(roomInvite, accept)
            .pipe(first())
            .subscribe(data => {
                console.log(data);
            });

        this.user.roomInviteList.splice(this.user.roomInviteList.indexOf(roomInvite), 1);
    }

    updatePublicRoomLimit() {
        if (this.limit >= 1) {
            this.roomService.getPopularPublicRooms(this.limit, '').subscribe(
                publicRooms => {
                    this.publicRooms = publicRooms;
                }
            );
        }
    }

    showRoom(room: Room) {
        this.roomService.getRoom(room.id).subscribe(value => {
            if (value != null) {
                this.router.navigate(['room', room.id]);
            }
        });
    }

    ngAfterViewInit() {
        fromEvent(this.inputElement.nativeElement, 'keyup')
            .pipe(
                debounceTime(500),
                pluck('target', 'value'),
                distinctUntilChanged(),
                filter((value: string) => value.length > 0),
                map((value) => value.split(' ').join('+'))
            ).subscribe(value => {
            console.log(value);
            this.roomService.getPopularPublicRooms(this.limit, value).subscribe(users => {
                this.publicRooms = users;
            });
        });
    }
}

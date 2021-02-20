import {Component, OnInit} from '@angular/core';
import {Room} from '../../../models/room';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {RoomService} from '../../../services/room.service';
import {first} from 'rxjs/operators';
import {PlaylistService} from '../../../services/playlist.service';
import {AccountService} from '../../../services/account.service';
import {User} from '../../../models/user';
import {PlaySongService} from '../../../services/play-song.service';
import {UserService} from '../../../services/user.service';
import {Member} from '../../../models/member';

@Component({
    selector: 'app-room',
    templateUrl: './room.component.html',
    styleUrls: ['./room.component.scss'],
})
export class RoomComponent implements OnInit {

    user: User;
    room: Room;
    listeningRoom: Room;
    collapsed: boolean;
    joined: boolean;
    left: boolean;

    constructor(
        private playlistService: PlaylistService,
        private route: ActivatedRoute,
        public roomService: RoomService,
        private router: Router,
        private accountService: AccountService,
        private userService: UserService,
        private playSongService: PlaySongService
    ) {
    }

    ngOnInit() {
        this.route.params.subscribe(
            (params: Params) => {
                this.roomService.getRoom(params.id)
                    .pipe(first())
                    .subscribe(data => {
                        this.roomService.getMembers(params.id)
                            .pipe(first())
                            .subscribe(data2 => {
                                data.memberList = data2;
                                console.log(this.room);
                                this.playSongService.roomValue.subscribe(lRoom => {
                                    this.listeningRoom = lRoom;
                                    this.userService.userValue.subscribe(
                                        value => {
                                            this.user = value;
                                            this.room = data;
                                            if (!this.roomService.oldRoom || this.room.id !== this.roomService.oldRoom.id) {
                                                this.roomService.oldRoom = this.room;
                                                this.roomService.updateRoomValue(this.room);
                                            }
                                        });
                                });
                        });
                });
            });

        this.playSongService.updateMemberListEvent.subscribe(() => {
           this.roomService.getMembers(this.room.id).subscribe(members => {
               this.room.memberList = members;
               if (!this.userIsMember()) {
                   this.userService.getProperMemberList(this.user.username).subscribe(memberList => {
                       this.user.memberList = memberList;
                       this.joined = false;
                       this.left = true;
                       this.userService.updateUserValue(this.user);
                       this.playSongService.disconnect();
                       this.router.navigate(['roomlist']);
                   });
               }
           });
        });
    }

    showPlaylist() {
        this.router.navigate(['playlist', this.room.id], {relativeTo: this.route});
    }

    collapseList() {
        this.collapsed = !this.collapsed;
    }

    userIsMember() {
        for (const member of this.room.memberList) {
            if (member.user.id === this.user.id) {
                return true;
            }
        }
        return false;
    }

    joinRoomFunc() {
        console.log('Joined Room');
        this.roomService.addMember(this.user.username, this.room.id).subscribe(
            value => {
                console.log(value);
                this.userService.getProperMemberList(this.user.username).subscribe(value2 => {
                    this.user.memberList = value2;
                    this.userService.updateUserValue(this.user);
                    this.roomService.getMembers(this.room.id)
                        .pipe(first())
                        .subscribe( value3 => {
                            this.joined = true;
                            this.left = false;
                            this.room.memberList = value3;
                            this.roomService.updateRoomValue(this.room);
                        });
                });
            });
    }

    leaveRoomFunc() {
        // ADD HTTP REQUEST ONCE ENDPOINT IS CREATED
        console.log('Left Room');
        this.roomService.removeMember(this.user.username, this.room.id).subscribe(
            value => {
                console.log(value);
                this.roomService.updateRoomValue(null);
                this.userService.getProperMemberList(this.user.username).subscribe(memberList => {
                            this.user.memberList = memberList;
                            this.joined = false;
                            this.left = true;
                            this.userService.updateUserValue(this.user);
                            this.playSongService.disconnect();
                            this.router.navigate(['roomlist']);
                    });
            }
        );
    }

    removeUserFromRoom(member: Member) {
        this.roomService.removeMember(member.user.username, this.room.id).subscribe(value => {
            this.roomService.getMembers(this.room.id).subscribe(value2 => {
                this.room.memberList = value2;
                this.roomService.updateRoomValue(this.room);
            });
        });
    }
}

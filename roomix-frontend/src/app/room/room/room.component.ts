import {Component, OnInit} from '@angular/core';
import {Room} from '../../models/room';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {RoomService} from '../../services/room.service';
import {first} from 'rxjs/operators';
import {PlaylistService} from '../../services/playlist.service';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {PlaySongService} from '../../services/play-song.service';

@Component({
    selector: 'app-room',
    templateUrl: './room.component.html',
    styleUrls: ['./room.component.scss'],
})
export class RoomComponent implements OnInit {

    user: User;
    room: Room;
    collapsed: boolean;

    constructor(
        private playlistService: PlaylistService,
        private route: ActivatedRoute,
        public roomService: RoomService,
        private router: Router,
        private accountService: AccountService,
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
                                this.accountService.userValue.subscribe(
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
    }

    showPlaylist() {
        this.router.navigate(['playlist', this.room.id], {relativeTo: this.route});
    }

    collapseList() {
        this.collapsed = !this.collapsed;
    }
}

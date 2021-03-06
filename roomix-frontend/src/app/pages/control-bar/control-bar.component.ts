import {AfterViewInit, Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';
import {RoomService} from '../../services/room.service';
import {Room} from '../../models/room';
import {AccountService} from '../../services/account.service';
import {User} from '../../models/user';
import {PlaySongService} from '../../services/play-song.service';
import { SkipVoteAmountDTO } from '../../models/dto/skipVoteAmountDTO';
import {UserService} from '../../services/user.service';

@Component({
    selector: 'app-control-bar',
    templateUrl: './control-bar.component.html',
    styleUrls: ['./control-bar.component.scss']
})
export class ControlBarComponent implements OnInit {

    user: User;
    room: Room;
    skip: boolean;
    songProgress = 0;
    animationTime = '30s';

    // @ViewChild('songProgress') songProgressBar: ElementRef;
    skips: SkipVoteAmountDTO;

    constructor(
        private roomService: RoomService,
        private accountService: AccountService,
        public playSongService: PlaySongService,
        private userService: UserService,
        private renderer: Renderer2
    ) {
    }

    ngOnInit() {
        this.playSongService.roomValue.subscribe(
            value => {
                this.room = value;
                this.playSongService.changeVolume(-1);
            }
        );

        this.userService.userValue.subscribe(value => {
            this.user = value;
        });

        this.playSongService.resetSkipVoteEvent.subscribe(() => {
            this.skip = false;
        });

        this.playSongService.updateSkipAmountEvent.subscribe(skipVoteAmountDTO => {
            this.skips = skipVoteAmountDTO;
        });
    }

    adjustVolume(value) {
        this.playSongService.changeVolume(value);
    }

    skipSong() {
        this.skip = !this.skip;
        this.playSongService.skipSong(this.skip);
    }

    mutePlayer() {
        this.playSongService.mutePlayer();
    }

    removeActiveMember() {
        this.user.activeMember = null;
        this.userService.updateUserValue(this.user);
    }

    updateMemberList() {
        this.roomService.getMembers(this.room.id).subscribe(members => {
            this.room.memberList = members;
        });
    }
}

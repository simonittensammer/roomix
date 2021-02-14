import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
import {Observable} from 'rxjs';
import {AccountService} from '../services/account.service';
import {User} from '../models/user';
import {PlaySongService} from '../services/play-song.service';
import {DomSanitizer} from '@angular/platform-browser';
import {UserService} from '../services/user.service';
import {SkipVoteAmountDTO} from '../models/dto/skipVoteAmountDTO';

@Component({
    selector: 'app-control-bar',
    templateUrl: './control-bar.component.html',
    styleUrls: ['./control-bar.component.scss'],
})
export class ControlBarComponent implements OnInit {

    user: User;
    room: Room;
    skip: boolean;
    skips: SkipVoteAmountDTO;

    constructor(
        private roomService: RoomService,
        private accountService: AccountService,
        private userService: UserService,
        public playSongService: PlaySongService,
        private sanitizer: DomSanitizer
    ) {}

    ngOnInit() {
        this.playSongService.roomValue.subscribe(
            value => {
                this.room = value;
            }
        );
        this.playSongService.changeVolume(-1);

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
}

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
    volumePercentage = 100;
    volumeStage: string;
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
        this.volumeStage = 'h';

        this.playSongService.resetSkipVoteEvent.subscribe(() => {
            this.skip = false;
        });

        this.playSongService.updateSkipAmountEvent.subscribe(skipVoteAmountDTO => {
            this.skips = skipVoteAmountDTO;
        });
    }

    adjustVolume(value) {
        this.volumePercentage = value;
        // console.log(this.volumePercentage);

        // tslint:disable-next-line:triple-equals
        if (this.volumePercentage == 0) {
            this.volumeStage = 'x';
        } else if (this.volumePercentage <= 33) {
            this.volumeStage = 'l';
        } else if (this.volumePercentage <= 66) {
            this.volumeStage = 'm';
        } else {
            this.volumeStage = 'h';
        }
    }

    skipSong() {
        this.skip = !this.skip;
        this.playSongService.skipSong(this.skip);
    }
}

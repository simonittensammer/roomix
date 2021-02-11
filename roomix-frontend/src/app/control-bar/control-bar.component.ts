import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
import {Observable} from 'rxjs';
import {AccountService} from '../services/account.service';
import {User} from '../models/user';
import {PlaySongService} from '../services/play-song.service';
import {DomSanitizer} from '@angular/platform-browser';
import {UserService} from '../services/user.service';

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

    constructor(
        private roomService: RoomService,
        private accountService: AccountService,
        private userService: UserService,
        public playSongService: PlaySongService,
        private sanitizer: DomSanitizer
    ) {
    }

    ngOnInit() {
        this.roomService.roomValue.subscribe(
            value => {
                this.room = value;
                this.userService.userValue.subscribe(
                    value2 => {
                        this.user = value2;
                        console.log(this.room);

                        // this.playSongService.connect();
                        console.log(this.playSongService.currentSong);
                    }
                );
            }
        );

        this.volumeStage = 'h';
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
}

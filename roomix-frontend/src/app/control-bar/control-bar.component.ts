import {AfterViewInit, Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
import {AccountService} from '../services/account.service';
import {User} from '../models/user';
import {PlaySongService} from '../services/play-song.service';
<<<<<<< HEAD
=======
import {DomSanitizer} from '@angular/platform-browser';
import {UserService} from '../services/user.service';
import {SkipVoteAmountDTO} from '../models/dto/skipVoteAmountDTO';
>>>>>>> ad1872efc0144666d8c6778ac7d15f6b3c099358

@Component({
    selector: 'app-control-bar',
    templateUrl: './control-bar.component.html',
    styleUrls: ['./control-bar.component.scss']
})
export class ControlBarComponent implements OnInit {

    user: User;
    room: Room;
    skip: boolean;
<<<<<<< HEAD
    songProgress = 0;
    animationTime = '30s';

    // @ViewChild('songProgress') songProgressBar: ElementRef;
=======
    skips: SkipVoteAmountDTO;
>>>>>>> ad1872efc0144666d8c6778ac7d15f6b3c099358

    constructor(
        private roomService: RoomService,
        private accountService: AccountService,
        public playSongService: PlaySongService,
        private renderer: Renderer2
    ) {
    }

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

<<<<<<< HEAD
    // startAnimation() {
    //     console.log('start');
    //
    //     this.renderer.setStyle(this.songProgressBar.nativeElement, 'transition-duration', '30s');
    //     this.renderer.setStyle(this.songProgressBar.nativeElement, 'width', this.songProgress + '%');
    // }
=======
    mutePlayer() {
        this.playSongService.mutePlayer();
    }
>>>>>>> ad1872efc0144666d8c6778ac7d15f6b3c099358
}

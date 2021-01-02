import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RoomService} from '../services/room.service';
import {Room} from '../models/room';
import {Observable} from 'rxjs';
import {AccountService} from '../services/account.service';
import {User} from '../models/user';

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
        private accountService: AccountService
    ) {
    }

    ngOnInit() {
        this.roomService.roomValue.subscribe(
            value => {
                this.room = value;
            }
        );
        this.accountService.userValue.subscribe(
            value => {
                this.user = value;
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

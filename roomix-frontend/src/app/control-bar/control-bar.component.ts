import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RoomService} from "../services/room.service";
import {Room} from "../models/room";

@Component({
    selector: 'app-control-bar',
    templateUrl: './control-bar.component.html',
    styleUrls: ['./control-bar.component.scss'],
})
export class ControlBarComponent implements OnInit {

    room: Room;
    volumePercentage = 100;
    volumeStage: string;

    constructor(
        private roomService: RoomService
    ) {
    }

    ngOnInit() {
        this.room = this.roomService.roomValue;
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

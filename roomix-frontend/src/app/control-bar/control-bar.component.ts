import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';

@Component({
    selector: 'app-control-bar',
    templateUrl: './control-bar.component.html',
    styleUrls: ['./control-bar.component.scss'],
})
export class ControlBarComponent implements OnInit {

    volumePercentage = 100;

    constructor() {
    }

    ngOnInit() {
    }

    adjustVolume(value) {
        this.volumePercentage = value;
        // console.log(this.volumePercentage);

        // tslint:disable-next-line:triple-equals
        if (this.volumePercentage == 0) {
            console.log('mute');
        } else if (this.volumePercentage <= 33) {
            console.log('low');
        } else if (this.volumePercentage <= 66) {
            console.log('medium');
        } else {
            console.log('high');
        }
    }
}

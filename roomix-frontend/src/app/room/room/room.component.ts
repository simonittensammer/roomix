import { Component, OnInit } from '@angular/core';
import {Room} from '../../models/room';
import {ActivatedRoute, Params} from '@angular/router';
import {RoomService} from '../room.service';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss'],
})
export class RoomComponent implements OnInit {

  room: Room;

  constructor(
      private route: ActivatedRoute,
      private roomService: RoomService
  ) {
      this.room.name = '';
  }

  ngOnInit() {
    this.route.params.subscribe(
        (params: Params) => {
          this.roomService.getRoom(params.id)
              .pipe(first())
              .subscribe(data => {
                this.room = data;
              });
        }
    );
  }

}

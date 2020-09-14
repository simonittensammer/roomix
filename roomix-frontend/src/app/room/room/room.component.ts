import { Component, OnInit } from '@angular/core';
import {Room} from '../../models/room';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {RoomService} from '../room.service';
import {first} from 'rxjs/operators';
import {PlaylistService} from '../playlist/playlist.service';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.scss'],
})
export class RoomComponent implements OnInit {

  room: Room;

  constructor(
      private playlistService: PlaylistService,
      private route: ActivatedRoute,
      private roomService: RoomService,
      private router: Router,
  ) {
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

  showPlaylist() {
      this.router.navigate(['playlist', this.room.id], {relativeTo: this.route});
  }
}

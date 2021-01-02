import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {Room} from '../../../models/room';
import {RoomService} from '../../../services/room.service';
import {Song} from '../../../models/song';
import {PlaylistService} from '../../../services/playlist.service';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.scss'],
})
export class PlaylistComponent implements OnInit {

  room: Room;
  collapsed: boolean;

  constructor(
      private playlistService: PlaylistService,
      private route: ActivatedRoute,
      private roomService: RoomService,
      private router: Router,
  ) { }

  ngOnInit() {
      this.roomService.roomValue.subscribe(
          value => {
              this.room = value;
          }
      );
  }

  showAddSong() {
    this.router.navigate(['add-song', this.room.id], {relativeTo: this.route});
  }

    deleteFromPlaylist(song: Song) {
        this.playlistService.deleteSongFromPlaylist(this.room.id, song.id)
            .pipe(first())
            .subscribe(data => {
                console.log(data);
                // this.room.playlist.songList.splice(this.room.playlist.songList.indexOf(data));
                location.reload();
            });
    }

    collapseList() {
        this.collapsed = !this.collapsed;
    }
}

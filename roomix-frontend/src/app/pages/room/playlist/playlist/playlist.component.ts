import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {Room} from '../../../../models/room';
import {RoomService} from '../../../../services/room.service';
import {Song} from '../../../../models/song';
import {PlaylistService} from '../../../../services/playlist.service';
import {PlaySongService} from '../../../../services/play-song.service';

@Component({
  selector: 'app-playlist',
  templateUrl: './playlist.component.html',
  styleUrls: ['./playlist.component.scss'],
})
export class PlaylistComponent implements OnInit {

  room: Room;
  listeningRoom: Room;
  collapsed: boolean;

  constructor(
      private playlistService: PlaylistService,
      private playSongService: PlaySongService,
      private route: ActivatedRoute,
      private roomService: RoomService
  ) { }

  ngOnInit() {
      this.roomService.roomValue.subscribe(
          value => {
              this.room = value;
              this.playSongService.roomValue.subscribe( value2 => {
                  this.listeningRoom = value2;
              });
          }
      );
  }

    deleteFromPlaylist(song: Song) {
        this.playlistService.deleteSongFromPlaylist(this.room.id, song.url)
            .pipe(first())
            .subscribe(data => {
                console.log(data);
                // this.room.playlist.songList.splice(this.room.playlist.songList.findIndex(x => x.url === song.url), 1);
            });
    }

    collapseList() {
        this.collapsed = !this.collapsed;
    }
}

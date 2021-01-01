import {AfterViewInit, Component, ElementRef, OnInit, Output, ViewChild} from '@angular/core';
import {debounceTime, distinctUntilChanged, filter, first, map, pluck} from 'rxjs/operators';
import {fromEvent} from 'rxjs';
import {Song} from '../../../models/song';
import {PlaylistService} from '../../../services/playlist.service';
import {ActivatedRoute, Params, Router} from '@angular/router';

@Component({
  selector: 'app-add-song',
  templateUrl: './add-song.component.html',
  styleUrls: ['./add-song.component.scss'],
})
export class AddSongComponent implements OnInit, AfterViewInit {

  @ViewChild('input') inputElement: ElementRef;
  songs: Array<Song>;
  roomId: number;

  constructor(
      private playlistService: PlaylistService,
      private route: ActivatedRoute,
      private router: Router
  ) {}

  ngOnInit() {
      this.route.params.subscribe(
          (params: Params) => {
              this.roomId = parseInt(params.id3);
          }
      );
  }

  ngAfterViewInit() {
    fromEvent(this.inputElement.nativeElement, 'keyup')
        .pipe(
            debounceTime(500),
            pluck('target', 'value'),
            distinctUntilChanged(),
            filter((value: string) => value.length > 3),
            map((value) => value)
        )
        .subscribe(value => {
          console.log(value);
          this.playlistService.getVideos(value)
              .pipe(first())
              .subscribe(
                  data => {
                    console.log(data[0].snippet);
                    this.songs = [];
                    for (const video of data) {
                      this.playlistService.getVideoDuration(video.id.videoId)
                          .pipe(first())
                          .subscribe(data2 => {
                            let duration = data2[0].contentDetails.duration;
                            let a = duration.match(/\d+/g);

                            if (duration.indexOf('M') >= 0 && duration.indexOf('H') === -1 && duration.indexOf('S') === -1) {
                              a = [0, a[0], 0];
                            }
                            if (duration.indexOf('H') >= 0 && duration.indexOf('M') === -1) {
                              a = [a[0], 0, a[1]];
                            }
                            if (duration.indexOf('H') >= 0 && duration.indexOf('M') === -1 && duration.indexOf('S') === -1) {
                              a = [a[0], 0, 0];
                            }
                            duration = 0;

                            if (a.length === 3) {
                              duration = duration + parseInt(a[0]) * 3600;
                              duration = duration + parseInt(a[1]) * 60;
                              duration = duration + parseInt(a[2]);
                            }
                            if (a.length === 2) {
                              duration = duration + parseInt(a[0]) * 60;
                              duration = duration + parseInt(a[1]);
                            }
                            if (a.length === 1) {
                              duration = duration + parseInt(a[0]);
                            }

                            this.songs.push(new Song(
                                video.snippet.title,
                                video.snippet.channelTitle,
                                video.id.videoId,
                                video.snippet.thumbnails.high.url,
                                duration
                            ));
                          });
                    }
                  });
        });
  }

  addToPlaylist(song: Song) {
      this.playlistService.addSong(this.roomId, song)
          .pipe(first())
          .subscribe(data => {
              console.log(data);
          });
    }
}

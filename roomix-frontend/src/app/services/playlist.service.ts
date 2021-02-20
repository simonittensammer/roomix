import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {User} from '../models/user';
import {GlobalConstants} from '../helpers/globalConstants';
import {Song} from '../models/song';
import {toNumbers} from '@angular/compiler-cli/src/diagnostics/typescript_version';

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {

  private API_URL = 'https://www.googleapis.com/youtube/v3/';
  private API_TOKEN = 'AIzaSyBQ4OBlQ9v34aLeJxrimhMt7PKrO4uxaDw';
  public addSongVisible: boolean;

  constructor(private http: HttpClient) {}

  getVideos(query: string): Observable <any> {
    const url = GlobalConstants.APIURL + '/song/YT/search/' + query;
    return this.http.get(url)
        .pipe(
            map((response: any) => response.items)
        );
  }

  getVideoDuration(videoId: string) {
    const url = GlobalConstants.APIURL + '/song/YT/duration/' + videoId;
    return this.http.get(url)
        .pipe(
            map((response: any) => response.items)
        );
  }

  addSong(roomId: number, song: Song) {
    return this.http.post(GlobalConstants.APIURL + '/room/song', { roomId, song});
  }

  deleteSongFromPlaylist(roomId: number, songUrl: string) {
    return this.http.delete<Song>(GlobalConstants.APIURL + '/room/' + roomId + '/song/delete/' + songUrl);
  }

  showAddSong() {
    this.addSongVisible = !this.addSongVisible;
  }
}

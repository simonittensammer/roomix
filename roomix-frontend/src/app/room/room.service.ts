import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/user';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Room} from '../models/room';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  constructor(
      private http: HttpClient,
  ) { }

  getRoom(id) {
    return this.http.get<Room>(GlobalConstants.apiUrl + '/room/' + id)
        .pipe(map(room => {
          return room;
        }));
  }
}

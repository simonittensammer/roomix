import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {User} from '../models/user';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {GlobalConstants} from '../helpers/globalConstants';
import {map} from 'rxjs/operators';
import {Member} from '../models/member';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private userSubject: BehaviorSubject<User>;

  constructor(
      private router: Router,
      private http: HttpClient
  ) {
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
  }

  public get userValue(): User {
    return this.userSubject.value;
  }

  public updateUserValue(user: User) {
    localStorage.setItem('user', JSON.stringify(user));
    this.userSubject.next(user);
  }

  getProperMemberList(username) {
    return this.http.get<Member[]>(GlobalConstants.apiUrl + '/user/' + username + '/members');
  }

  login(username, password) {
    return this.http.post<User>(GlobalConstants.apiUrl + '/user/login', { username, password })
        .pipe(map(user => {
          return user;
        }));
  }

  logout() {
    // remove user from local storage and set current user to null
    localStorage.removeItem('user');
    this.userSubject.next(null);
    this.router.navigate(['/login']);
  }

  register(user: User) {
    return this.http.post(GlobalConstants.apiUrl + '/user', user);
  }
}

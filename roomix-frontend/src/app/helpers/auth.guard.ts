import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {AccountService} from '../services/account.service';
import {User} from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  user: User;

  constructor(
      private router: Router,
      private accountService: AccountService
  ) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.accountService.userValue.subscribe(
        value => {
          this.user = value;
        }
    );
    if (this.user) {
      return true;
    }
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
    return false;
  }
}

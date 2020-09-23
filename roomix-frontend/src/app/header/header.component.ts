import {Component, OnChanges, OnInit} from '@angular/core';
import {AccountService} from '../account/account.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {User} from '../models/user';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {

  isLoggedIn: Observable<boolean>;
  user: User;

  constructor(
      private accountService: AccountService,
      private router: Router,
      private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.isLoggedIn = this.accountService.isLoggedIn;
    this.user = this.accountService.userValue;
  }

  logout() {
    this.user = null;
    this.accountService.logout();
  }

}

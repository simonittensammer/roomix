import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AccountService} from '../account.service';
import {first} from 'rxjs/operators';
import {Router} from '@angular/router';
import {User} from '../../models/user';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  constructor(
      private accountService: AccountService,
      private router: Router
  ) { }

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    });

    const user = this.accountService.userValue;
    if (user) {
        console.log(user.username + ' | ' + user.password);
        this.login(user.username, user.password);
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.login(this.loginForm.value.username, this.loginForm.value.password);
    }
  }

  login(username: string, password: string) {
      this.accountService.login(username, password)
          .pipe(first())
          .subscribe(data => {
              this.accountService.getProperMemberList(data.username)
                  .pipe(first())
                  .subscribe(data2 => {
                      data.memberList = data2;
                      this.accountService.updateUserValue(data);
                      this.accountService.updateIsLoggedIn(true);
                      this.router.navigate(['roomlist']);
                  });
          });
  }

}

import { Component, OnInit } from '@angular/core';
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
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.accountService.login(this.loginForm.value.username, this.loginForm.value.password)
          .pipe(first())
          .subscribe(data => {
              this.accountService.getProperMemberList(data.username)
                  .pipe(first())
                  .subscribe(data2 => {
                      data.memberList = data2;
                      this.accountService.updateUserValue(data);
                  });
              this.router.navigate(['roomlist']);
      });
    }
  }

}

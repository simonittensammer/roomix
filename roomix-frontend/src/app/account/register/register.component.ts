import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {first} from 'rxjs/operators';
import {AccountService} from '../account.service';
import {LoginComponent} from '../login/login.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {

  regForm: FormGroup;

  constructor(
      private accountService: AccountService,
      private router: Router,
  ) { }

  ngOnInit(): void {
    this.regForm = new FormGroup({
      username: new FormControl('', Validators.required),
      displayname: new FormControl('', Validators.required),
      password: new FormControl('', Validators.required)
    });
  }

  onSubmit() {
    if (this.regForm.valid) {
      this.accountService.register(this.regForm.value)
          .pipe(first())
          .subscribe(registerData => {
            const username = this.regForm.value.username;
            const password = this.regForm.value.password;
            this.accountService.login(username, password)
                .pipe(first())
                .subscribe(data => {
                  this.accountService.getProperMemberList(data.username)
                      .pipe(first())
                      .subscribe(data2 => {
                        data.memberList = data2;
                        this.accountService.updateUserValue(data);
                        this.router.navigate(['roomlist', true]);
                      });
                });
          });
    }
  }
}

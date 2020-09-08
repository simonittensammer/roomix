import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {first} from 'rxjs/operators';
import {AccountService} from '../account.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {

  regForm: FormGroup;

  constructor(private accountService: AccountService) { }

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
          .subscribe(data => {});
    }
  }
}

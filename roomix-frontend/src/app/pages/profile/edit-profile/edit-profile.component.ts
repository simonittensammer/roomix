import { Component, OnInit } from '@angular/core';
import {AccountService} from '../../../services/account.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss'],
})
export class EditProfileComponent implements OnInit {

  constructor(
      public accountService: AccountService
  ) { }

  ngOnInit() {}

  onSubmit() {

  }
}

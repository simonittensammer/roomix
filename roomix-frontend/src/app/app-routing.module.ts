import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import {AuthGuard} from './helpers/auth.guard';
import {LoginComponent} from './account/login/login.component';
import {RegisterComponent} from './account/register/register.component';
import {RoomlistComponent} from './roomlist/roomlist/roomlist.component';
import {RoomComponent} from './room/room/room.component';
import {CreateRoomComponent} from './roomlist/create-room/create-room.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'roomlist',
    component: RoomlistComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'room',
    component: RoomComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'roomlist/create-room',
    component: CreateRoomComponent,
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }

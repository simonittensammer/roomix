import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import {AuthGuard} from './helpers/auth.guard';
import {LoginComponent} from './account/login/login.component';
import {RegisterComponent} from './account/register/register.component';
import {RoomlistComponent} from './pages/roomlist/roomlist/roomlist.component';
import {RoomComponent} from './pages/room/room/room.component';
import {CreateRoomComponent} from './pages/roomlist/create-room/create-room.component';
import {PlaylistComponent} from './pages/room/playlist/playlist/playlist.component';
import {AddSongComponent} from './pages/room/playlist/add-song/add-song.component';
import {FriendlistComponent} from './pages/profile/friendlist/friendlist/friendlist.component';
import {ProfileComponent} from './pages/profile/profile/profile.component';
import {FriendRequestsComponent} from './pages/profile/friendlist/friend-requests/friend-requests.component';
import {JoinRoomComponent} from './pages/join-room/join-room.component';
import {LandingPageComponent} from './pages/landing-page/landing-page.component';

const routes: Routes = [
  {
    path: '',
    component: LandingPageComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  // {
  //   path: '',
  //   redirectTo: 'login',
  //   pathMatch: 'full'
  // },
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
    path: 'room/:id',
    component: RoomComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'room/:id/playlist',
    component: PlaylistComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'room/:id/playlist/:id2',
    component: PlaylistComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'room/:id/playlist/:id/add-song',
    component: AddSongComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'room/:id/playlist/:id/add-song/:id3',
    component: AddSongComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'roomlist/create-room',
    component: CreateRoomComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'roomlist/join-room',
    component: JoinRoomComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'friendlist',
    component: FriendlistComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'friendlist/requests',
    component: FriendRequestsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'profile',
    component: ProfileComponent,
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

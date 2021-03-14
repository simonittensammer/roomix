import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { RegisterComponent } from './account/register/register.component';
import {LoginComponent} from './account/login/login.component';
import {RoomComponent} from './pages/room/room/room.component';
import {RoomlistComponent} from './pages/roomlist/roomlist/roomlist.component';
import {CreateRoomComponent} from './pages/roomlist/create-room/create-room.component';
import {PlaylistComponent} from './pages/room/playlist/playlist/playlist.component';
import {AddSongComponent} from './pages/room/playlist/add-song/add-song.component';
import {HeaderComponent} from './header/header.component';
import {ControlBarComponent} from './control-bar/control-bar.component';
import {FriendlistComponent} from './pages/profile/friendlist/friendlist/friendlist.component';
import {FriendRequestsComponent} from './pages/profile/friendlist/friend-requests/friend-requests.component';
import {ProfileComponent} from './pages/profile/profile/profile.component';
import {JoinRoomComponent} from './pages/join-room/join-room.component';
import {InviteFriendComponent} from './pages/room/invite-friend/invite-friend.component';
import {AddFriendComponent} from './pages/profile/friendlist/add-friend/add-friend.component';
import {ChatComponent} from './pages/room/chat/chat.component';
import {EditProfileComponent} from './pages/profile/edit-profile/edit-profile.component';
import {EditRoomComponent} from './pages/room/edit-room/edit-room.component';
import {LandingPageComponent} from './pages/landing-page/landing-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
    declarations: [
        AppComponent,
        LoginComponent,
        RegisterComponent,
        RoomComponent,
        RoomlistComponent,
        CreateRoomComponent,
        JoinRoomComponent,
        PlaylistComponent,
        AddSongComponent,
        HeaderComponent,
        ControlBarComponent,
        FriendlistComponent,
        FriendRequestsComponent,
        ProfileComponent,
        InviteFriendComponent,
        AddFriendComponent,
        ChatComponent,
        EditProfileComponent,
        EditRoomComponent,
        LandingPageComponent
    ],
  entryComponents: [],
  imports: [
      BrowserModule,
      IonicModule.forRoot(),
      AppRoutingModule,
      HttpClientModule,
      FormsModule,
      ReactiveFormsModule,
      BrowserAnimationsModule
  ],
  providers: [
    StatusBar,
    SplashScreen,
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

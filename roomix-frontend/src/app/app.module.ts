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
import {RoomComponent} from './room/room/room.component';
import {RoomlistComponent} from './roomlist/roomlist/roomlist.component';
import {CreateRoomComponent} from './roomlist/create-room/create-room.component';
import {PlaylistComponent} from './room/playlist/playlist/playlist.component';
import {AddSongComponent} from './room/playlist/add-song/add-song.component';

@NgModule({
  declarations: [
      AppComponent,
      LoginComponent,
      RegisterComponent,
      RoomComponent,
      RoomlistComponent,
      CreateRoomComponent,
      PlaylistComponent,
      AddSongComponent
  ],
  entryComponents: [],
  imports: [
      BrowserModule,
      IonicModule.forRoot(),
      AppRoutingModule,
      HttpClientModule,
      FormsModule,
      ReactiveFormsModule,
  ],
  providers: [
    StatusBar,
    SplashScreen,
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}

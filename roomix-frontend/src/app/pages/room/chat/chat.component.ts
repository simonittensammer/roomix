import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ChatMessageDTO} from '../../../models/dto/chatMessageDTO';
import {RoomService} from '../../../services/room.service';
import {Room} from '../../../models/room';
import {User} from '../../../models/user';
import {UserService} from '../../../services/user.service';
import {UserSocketService} from '../../../services/user-socket.service';
import {PlaySongService} from '../../../services/play-song.service';
import {AccountService} from '../../../services/account.service';

@Component({
    selector: 'app-chat',
    templateUrl: './chat.component.html',
    styleUrls: ['./chat.component.scss'],
})
export class ChatComponent implements OnInit {

    @ViewChild('scrollBox') private scrollBox: ElementRef;
    room: Room;
    user: User;
    messages: ChatMessageDTO[];

    message: string;
    scrolledDown: boolean;

    constructor(
        private roomService: RoomService,
        private userService: UserService,
        private playSongService: PlaySongService,
        public accountService: AccountService
    ) {
    }

    ngOnInit() {
        this.roomService.roomValue.subscribe(
            room => {
                this.roomService.getMembers(room.id).subscribe(members => {
                    room.memberList = members;
                    this.room = room;
                });
            }
        );

        this.userService.userValue.subscribe(
            value => {
                this.user = value;
            }
        );
    }

    sendMessage() {
        this.playSongService.sendChatMessage(this.message);
        this.message = '';
    }

    userIsMember() {
        for (const member of this.room.memberList) {
            if (member.user.id === this.user.id) {
                console.log('is member');
                return true;
            }
        }
        console.log('is not member');
        return false;
    }

    scrollDown() {
        try {
            this.scrollBox.nativeElement.scrollTop = this.scrollBox.nativeElement.scrollHeight;
        } catch (err) {
        }
    }

    getDisplaynameByUsername(sender: string) {
        return this.room.memberList.find(member => member.user.username === sender).user.displayname;
    }
}

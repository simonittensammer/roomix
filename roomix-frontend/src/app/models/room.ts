import {Member} from './member';
import {Playlist} from './playlist';
import {ChatMessageDTO} from './dto/chatMessageDTO';
import {Tag} from './tag';

export class Room {
    id: number;
    name: string;
    picUrl: string;
    memberList: Array<Member>;
    messageList: Array<ChatMessageDTO>;
    playlist: Playlist;
    // activeMemberAmount: number;
    private: boolean;
    tagList: Tag[];

    constructor(name) {
        this.name = name;
        this.memberList = [];
        this.messageList = [];
        this.tagList = [];
    }
}

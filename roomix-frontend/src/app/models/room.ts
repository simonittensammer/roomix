import {Member} from './member';
import {Playlist} from './playlist';

export class Room {
    id: number;
    name: string;
    picUrl: string;
    memberList: Array<Member>;
    // messageList: Array<Message>;
    playlist: Playlist;
    // activeMemberAmount: number;
    isPrivate: boolean;

    constructor(name) {
        this.name = name;
    }
}

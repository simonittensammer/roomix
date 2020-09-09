import {Member} from './member';

export class Room {
    id: number;
    name: string;
    picUrl: string;
    memberList: Array<Member>;
    /*
    messageList: Array<Message>;
    playlist: PlayList;
    */
    activeMemberAmount: number;
    isPrivate: boolean;
}

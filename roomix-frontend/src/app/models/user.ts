import {Member} from './member';

export class User {
    id: number;
    username: string;
    displayname: string;
    profilePicUrl: string;
    // friendList: Array<User>;
    memberList: Array<Member>;
    /*
    friendRequestList: Array<FriendRequest>;
    roomInviteList: Array<RoomInvite>;
     */
    activeMember: Member;
    password: string;
}

import {Tag} from '../tag';

export class RoomDTO {
    username: string;
    roomname: string;
    isPrivate: boolean;
    picUrl: string;
    tagList: Tag[];

    constructor(username: string, roomname: string, isPrivate: boolean, picUrl: string, tagList: Tag[]) {
        this.username = username;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
        this.tagList = tagList;
    }
}

import {Tag} from '../tag';

export class RoomUpdateDTO {
    roomId: number;
    roomname: string;
    isPrivate: boolean;
    picUrl: string;
    tagList: Tag[];

    constructor(roomId: number, roomname: string, isPrivate: boolean, picUrl: string, tagList: Tag[]) {
        this.roomId = roomId;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
        this.tagList = tagList;
    }
}

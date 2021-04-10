import {Tag} from '../tag';

export class RoomUpdateDTO {
    roomId: number;
    roomname: string;
    isPrivate: boolean;
    picUrl: string;
    tagList: Tag[];
    resetPic: boolean;

    constructor(roomId: number, roomname: string, isPrivate: boolean, picUrl: string, tagList: Tag[], resetPic: boolean) {
        this.roomId = roomId;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
        this.tagList = tagList;
        this.resetPic = resetPic;
    }
}

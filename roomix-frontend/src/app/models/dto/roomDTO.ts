export class RoomDTO {
    username: string;
    roomname: string;
    isPrivate: boolean;
    picUrl: string;

    constructor(username: string, roomname: string, isPrivate: boolean, picUrl: string) {
        this.username = username;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
    }
}

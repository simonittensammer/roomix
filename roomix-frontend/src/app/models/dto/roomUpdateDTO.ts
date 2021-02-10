export class RoomUpdateDTO {
    roomId: number;
    roomname: string;
    isPrivate: boolean;
    picUrl: string;

    constructor(roomId: number, roomname: string, isPrivate: boolean, picUrl: string) {
        this.roomId = roomId;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
    }
}

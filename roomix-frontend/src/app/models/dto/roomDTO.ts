export class RoomDTO {
    username: string;
    roomname: string;
    isPrivate: boolean;


    constructor(username: string, roomname: string, isPrivate: boolean) {
        this.username = username;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
    }
}

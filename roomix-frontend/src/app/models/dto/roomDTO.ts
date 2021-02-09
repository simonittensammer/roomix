export class RoomDTO {
    username: string;
    roomname: string;
    isPirvate: boolean;


    constructor(username: string, roomname: string, isPirvate: boolean) {
        this.username = username;
        this.roomname = roomname;
        this.isPirvate = isPirvate;
    }
}

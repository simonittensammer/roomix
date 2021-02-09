export class RoomUpdateDTO {
    roomId: number;
    roomname: string;
    isPrivate: boolean;


    constructor(roomId: number, roomname: string, isPrivate: boolean) {
        this.roomId = roomId;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
    }
}

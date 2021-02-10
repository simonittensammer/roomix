export class RoomInviteDTO {
    sender: string;
    receiver: string;
    roomId: number;

    constructor(sender: string, receiver: string, roomId: number) {
        this.sender = sender;
        this.receiver = receiver;
        this.roomId = roomId;
    }
}

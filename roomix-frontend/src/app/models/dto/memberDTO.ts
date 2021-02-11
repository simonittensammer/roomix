export class MemberDTO {
    username: string;
    roomId: number;

    constructor(username: string, roomId: number) {
        this.username = username;
        this.roomId = roomId;
    }
}

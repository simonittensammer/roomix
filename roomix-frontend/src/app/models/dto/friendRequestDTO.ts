export class FriendRequestDTO {
    sender: string;
    receiver: string;


    constructor(sender: string, receiver: string) {
        this.sender = sender;
        this.receiver = receiver;
    }
}

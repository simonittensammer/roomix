export class ChatMessageDTO {
    sender: string;
    timestamp: Date;
    content: string;

    constructor(username: string, timestamp: Date, content: string) {
        this.sender = username;
        this.timestamp = timestamp;
        this.content = content;
    }
}

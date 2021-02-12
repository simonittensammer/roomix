export class SocketMessageDTO {
    type: string;
    message: object;

    constructor(type: string, message: object) {
        this.type = type;
        this.message = message;
    }
}

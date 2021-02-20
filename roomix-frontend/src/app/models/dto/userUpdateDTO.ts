export class UserUpdateDTO {
    username: string;
    displayname: string;
    password: string;
    picUrl: string;

    constructor(username: string, displayname: string, password: string, picUrl: string) {
        this.username = username;
        this.displayname = displayname;
        this.password = password;
        this.picUrl = picUrl;
    }
}

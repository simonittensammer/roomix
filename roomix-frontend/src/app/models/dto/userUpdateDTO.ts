export class UserUpdateDTO {
    username: string;
    displayname: string;
    password: string;
    picUrl: string;
    resetProfilePic: boolean;

    constructor(username: string, displayname: string, password: string, picUrl: string, resetProfilePic: boolean) {
        this.username = username;
        this.displayname = displayname;
        this.password = password;
        this.picUrl = picUrl;
        this.resetProfilePic = resetProfilePic;
    }
}

import {User} from './user';
import {Room} from './room';

export class Member {
    id: number;
    user: User;
    room: Room;
    role: string;

    constructor(user, room, role) {
        this.user = user;
        this.room = room;
        this.role = role;
    }
}

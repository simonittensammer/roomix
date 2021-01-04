import {User} from './user';
import {Room} from './room';

export class Member {
    id: number;
    user: User;
    room: Room;
    role: string;

    constructor(id, user, room, role) {
        this.id = id;
        this.user = user;
        this.room = room;
        this.role = role;
    }
}

import {User} from './user';
import {Room} from './room';

export class RoomInvite {

    id: number;
    sender: User;
    receiver: User;
    room: Room;
}

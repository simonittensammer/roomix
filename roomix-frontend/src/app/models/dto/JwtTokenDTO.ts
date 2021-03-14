import {User} from '../user';

export interface JwtTokenDTO {
    token: string;
    expires_at: number;
    user: User;
}

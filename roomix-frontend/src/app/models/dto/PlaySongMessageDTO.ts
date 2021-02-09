import {Song} from '../song';

export interface PlaySongMessageDTO {
    song: Song;
    time: number;
}

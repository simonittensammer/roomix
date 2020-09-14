export class Song {

    id: number;
    title: string;
    artist: string;
    url: string;
    picUrl: string;
    length: number;

    constructor(title: string, artist: string, url: string, picUrl: string, length: number) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.picUrl = picUrl;
        this.length = length;
    }
}

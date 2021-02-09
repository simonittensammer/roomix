package at.htl.dto;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class RoomDTO {

    private String username;
    private String roomname;
    private boolean isPrivate;

    public RoomDTO() {
    }

    @JsonbCreator
    public RoomDTO(String username, String roomname, @JsonbProperty("isPrivate") boolean isPrivate) {
        this.username = username;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}

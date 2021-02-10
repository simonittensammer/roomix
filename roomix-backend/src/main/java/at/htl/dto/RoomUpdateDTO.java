package at.htl.dto;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class RoomUpdateDTO implements Serializable {

    private Long roomId;
    private String roomname;
    private boolean isPrivate;

    public RoomUpdateDTO() {
    }

    @JsonbCreator
    public RoomUpdateDTO(Long roomId, String roomname, @JsonbProperty("isPrivate") boolean isPrivate) {
        this.roomId = roomId;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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
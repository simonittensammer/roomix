package at.htl.dto;

import at.htl.entity.Tag;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.LinkedList;
import java.util.List;

public class RoomDTO {

    private String username;
    private String roomname;
    private boolean isPrivate;
    private String picUrl;
    private List<Tag> tagList;

    public RoomDTO() {
        tagList = new LinkedList<>();
    }

    @JsonbCreator
    public RoomDTO(String username, String roomname, @JsonbProperty("isPrivate") boolean isPrivate, String picUrl, List<Tag> tagList) {
        this.username = username;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
        this.tagList = tagList;
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

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }
}

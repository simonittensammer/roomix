package at.htl.dto;

import at.htl.entity.Tag;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class RoomUpdateDTO implements Serializable {

    private Long roomId;
    private String roomname;
    private boolean isPrivate;
    private String picUrl;
    private List<Tag> tagList;
    private boolean resetPic;

    public RoomUpdateDTO() {
        tagList = new LinkedList<>();
    }

    @JsonbCreator
    public RoomUpdateDTO(Long roomId, String roomname,  @JsonbProperty("isPrivate") boolean isPrivate, String picUrl, List<Tag> tagList, boolean resetPic) {
        this.roomId = roomId;
        this.roomname = roomname;
        this.isPrivate = isPrivate;
        this.picUrl = picUrl;
        this.tagList = tagList;
        this.resetPic = resetPic;
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

    public boolean isResetPic() {
        return resetPic;
    }

    public void setResetPic(boolean resetPic) {
        this.resetPic = resetPic;
    }
}

package at.htl.entity;

import at.htl.dto.RoomUpdateDTO;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RM_ID")
    Long id;

    @Column(name = "RM_NAME")
    String name;

    @Column(name = "RM_PIC")
    @Lob
    String picUrl;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    List<Member> memberList;

    @OneToMany(mappedBy = "room", cascade = CascadeType.MERGE)
    List<Message> messageList;

    @OneToOne
    @JoinColumn(name = "RM_PLL_ID")
    Playlist playlist;

    @ManyToMany(cascade = CascadeType.MERGE)
    List<Tag> tagList;

    int activeMemberCount;

    boolean isPrivate;

    public Room() {
        this.memberList = new LinkedList<>();
        this.messageList = new LinkedList<>();
        this.playlist = new Playlist();
        this.tagList = new LinkedList<>();
    }

    public Room(String name) {
        this.name = name;
        this.memberList = new LinkedList<>();
        this.messageList = new LinkedList<>();
        this.playlist = new Playlist();
        this.tagList = new LinkedList<>();
    }

    public Room(String name, String picUrl, boolean isPrivate) {
        this.name = name;
        this.picUrl = picUrl;
        this.isPrivate = isPrivate;
        this.memberList = new LinkedList<>();
        this.messageList = new LinkedList<>();
        this.tagList = new LinkedList<>();
        this.activeMemberCount = 0;
        this.playlist = new Playlist();
    }

    public Room update(RoomUpdateDTO roomUpdateDTO) {
        this.name = roomUpdateDTO.getRoomname();
        this.picUrl = roomUpdateDTO.getPicUrl();
        this.isPrivate = roomUpdateDTO.isPrivate();
        this.tagList = roomUpdateDTO.getTagList();
        return  this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public int getActiveMemberCount() {
        return activeMemberCount;
    }

    public void setActiveMemberCount(int activeMemberCount) {
        this.activeMemberCount = activeMemberCount;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}

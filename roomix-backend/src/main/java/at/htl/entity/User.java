package at.htl.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USR_ID")
    Long id;

    @Column(name = "USR_NAME")
    String username;

    @Column(name = "USR_PWD")
    String password;

    @Column(name = "USR_DPNAME")
    String displayname;

    @Column(name = "USR_PIC")
    String picUrl;

    @OneToMany
    List<User> firendList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Member> memberList;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    List<FriendRequest> friendRequestList;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    List<RoomInvite> roomInviteList;

    @OneToOne
    @JoinColumn(name = "USR_ACTV_MBR_ID")
    Member activeMember;

    public User() {
        this.firendList = new LinkedList<>();
        this.memberList = new LinkedList<>();
        this.friendRequestList = new LinkedList<>();
        this.roomInviteList = new LinkedList<>();
    }

    public User(String username, String password, String displayname, String picUrl) {
        this.username = username;
        this.password = password;
        this.displayname = displayname;
        this.picUrl = picUrl;
        this.firendList = new LinkedList<>();
        this.memberList = new LinkedList<>();
        this.friendRequestList = new LinkedList<>();
        this.roomInviteList = new LinkedList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<User> getFirendList() {
        return firendList;
    }

    public void setFirendList(List<User> firendList) {
        this.firendList = firendList;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public List<FriendRequest> getFriendRequestList() {
        return friendRequestList;
    }

    public void setFriendRequestList(List<FriendRequest> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    public List<RoomInvite> getRoomInviteList() {
        return roomInviteList;
    }

    public void setRoomInviteList(List<RoomInvite> roomInviteList) {
        this.roomInviteList = roomInviteList;
    }

    public Member getActiveMember() {
        return activeMember;
    }

    public void setActiveMember(Member activeMember) {
        this.activeMember = activeMember;
    }
}

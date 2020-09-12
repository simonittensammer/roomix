package at.htl.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "USR")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USR_ID")
    Long id;

    @Column(name = "USR_NAME", unique = true)
    String username;

    @Column(name = "USR_PWD")
    String password;

    @Column(name = "USR_DPNAME")
    String displayname;

    @Column(name = "USR_PIC")
    String picUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Member> memberList;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonbTransient
    List<FriendRequest> friendRequestList;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonbTransient
    List<RoomInvite> roomInviteList;

    @OneToOne
    @JoinColumn(name = "USR_ACTV_MBR_ID")
    Member activeMember;

    @JoinTable(name = "USR_FRIENDS", joinColumns = {
            @JoinColumn(name = "ADDING_USER", referencedColumnName = "USR_ID", nullable =   false)}, inverseJoinColumns = {
            @JoinColumn(name = "ADDED_USER", referencedColumnName = "USR_ID", nullable = false)})
    @ManyToMany
    @JsonbTransient
    List<User> friendList;

    @ManyToMany(mappedBy = "friendList")
    @JsonbTransient
    List<User> addUser;

    public User() {
        this.memberList = new LinkedList<>();
        this.friendRequestList = new LinkedList<>();
        this.roomInviteList = new LinkedList<>();
        this.friendList = new LinkedList<>();
        this.addUser = new LinkedList<>();
    }

    public User(String username, String password, String displayname, String picUrl) {
        this.username = username;
        this.password = password;
        this.displayname = displayname;
        this.picUrl = picUrl;
        this.memberList = new LinkedList<>();
        this.friendRequestList = new LinkedList<>();
        this.roomInviteList = new LinkedList<>();
        this.friendList = new LinkedList<>();
        this.addUser = new LinkedList<>();
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

    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public List<User> getAddUser() {
        return addUser;
    }

    public void setAddUser(List<User> addUser) {
        this.addUser = addUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", displayname='" + displayname + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", memberList=" + memberList +
                ", friendRequestList=" + friendRequestList +
                ", roomInviteList=" + roomInviteList +
                ", activeMember=" + activeMember +
                '}';
    }
}

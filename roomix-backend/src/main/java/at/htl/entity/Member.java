package at.htl.entity;

import javax.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MBR_ID")
    Long id;

    @ManyToOne
    @JoinColumn(name = "MBR_USR_ID")
    User user;

    @ManyToOne
    @JoinColumn(name = "MBR_RM_ID")
    Room room;

    @Column(name = "MBR_ROLE")
    String role;

    public Member() { }

    public Member(User user, Room room, String role) {
        this.user = user;
        this.room = room;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

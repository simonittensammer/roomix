package at.htl.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSG_ID")
    Long id;

    @ManyToOne
    @JoinColumn(name = "MSG_MBR_ID")
    Member member;

    @ManyToOne
    @JoinColumn(name = "MSG_RM_ID")
    Room room;

    @Column(name = "MSG_CONTENT")
    String content;

    @Column(name = "MSG_DATE")
    LocalDateTime creationDate;

    public Message() {
        this.creationDate = LocalDateTime.now();
    }

    public Message(Member member, Room room, String content, LocalDateTime creationDate) {
        this.member = member;
        this.room = room;
        this.content = content;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}

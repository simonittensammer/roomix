package at.htl.entity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MSG_ID")
    Long id;

    @Column(name = "MSG_SENDER")
    String sender;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonbTransient
    @JoinColumn(name = "MSG_RM_ID")
    Room room;

    @Column(name = "MSG_CONTENT")
    String content;

    @Column(name = "MSG_DATE")
    LocalDateTime creationDate;

    public Message() {
        this.creationDate = LocalDateTime.now();
    }

    public Message(String sender, Room room, String content) {
        this();
        this.sender = sender;
        this.room = room;
        this.content = content;
    }

    public Message(String sender, Room room, String content, LocalDateTime creationDate) {
        this.sender = sender;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

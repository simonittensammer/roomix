package at.htl.entity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class RoomInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RIV_ID")
    Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonbTransient
    @JoinColumn(name = "RIV_SND_ID")
    User sender;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonbTransient
    @JoinColumn(name = "RIV_RCV_ID")
    User receiver;

    @OneToOne
    @JsonbTransient
    @JoinColumn(name = "RIV_RM_ID")
    Room room;

    @Column(name = "RIV_DATE")
    LocalDateTime creationDate;

    public RoomInvite() {
        this.creationDate = LocalDateTime.now();
    }

    public RoomInvite(User sender, User receiver, Room room) {
        this.sender = sender;
        this.receiver = receiver;
        this.room = room;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}

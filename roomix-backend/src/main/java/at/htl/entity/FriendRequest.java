package at.htl.entity;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRQ_ID")
    Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonbTransient
    @JoinColumn(name = "FRQ_SND_ID")
    User sender;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonbTransient
    @JoinColumn(name = "FRQ_RCV_ID")
    User receiver;

    @Column(name = "FRQ_DATE")
    LocalDateTime creationDate;

    public FriendRequest() {
        this.creationDate = LocalDateTime.now();
    }

    public FriendRequest(User sender, User receiver, LocalDateTime creationDate) {
        this.sender = sender;
        this.receiver = receiver;
        this.creationDate = creationDate;
        this.creationDate = LocalDateTime.now();
    }
}

package at.htl.entity;

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
    String picUrl;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    List<Member> memberList;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    List<Message> messageList;

    @OneToOne
    @JoinColumn(name = "RM_PLL_ID")
    Playlist playlist;

    int activeMemberCount;

    boolean isPrivate;

    public Room() {
        this.memberList = new LinkedList<>();
        this.messageList = new LinkedList<>();
    }

    public Room(String name, String picUrl, boolean isPrivate) {
        this.name = name;
        this.picUrl = picUrl;
        this.isPrivate = isPrivate;
        this.memberList = new LinkedList<>();
        this.messageList = new LinkedList<>();
        this.activeMemberCount = 0;
        this.playlist = new Playlist();
    }
}

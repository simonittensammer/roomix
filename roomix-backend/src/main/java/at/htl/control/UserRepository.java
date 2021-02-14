package at.htl.control;

import at.htl.entity.Room;
import at.htl.entity.RoomInvite;
import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class UserRepository implements PanacheRepository<User> {

    @Inject
    MemberRepository memberRepository;

    public User findByName(String username) {
        User user = find("USR_NAME", username).firstResult();

        if (user != null) {
            Hibernate.initialize(user);
            Hibernate.initialize(user.getFriendRequestList());
            Hibernate.initialize(user.getMemberList());
            Hibernate.initialize(user.getRoomInviteList());
            Hibernate.initialize(user.getFriendList());
            Hibernate.initialize(user.getAddUser());
        }

        return user;
    }

    public void updateUser(User user, JsonObject jsonObject) {

        if (jsonObject.containsKey("username")) {
            if (findByName(jsonObject.getString("username")) != null) {
                throw new PersistenceException();
            }
        }

        jsonObject.forEach((s, jsonValue) -> {
            String value = jsonValue.toString().replace("\"", "");
            switch (s) {
                case "displayname":
                    user.setDisplayname(value);
                    break;
                case "username":
                    user.setUsername(value);
                    break;
                case "password":
                    user.setPassword(value);
                    break;
                case "picUrl":
                    user.setPicUrl(value);
                    break;
                default:
                    break;
            }
        });
    }

    public List<Room> findAllRoomsOfUser(String username) {
        List<Room> roomList = new LinkedList<>();

        User user = findByName(username);

        if (user != null) {
            user.getMemberList().stream().peek(o -> {
                Hibernate.initialize(o.getRoom());
                Hibernate.initialize(o.getRoom().getPlaylist());
                Hibernate.initialize(o.getRoom().getPlaylist().getSongList());
                Hibernate.initialize(o.getRoom().getMemberList());
                Hibernate.initialize(o.getRoom().getMessageList());
            }).collect(Collectors.toList());

            user.getMemberList().forEach(member -> {
                roomList.add(member.getRoom());
            });
        }

        return roomList;
    }

    public List<JsonObject> findAllSerializedMembers(String username) {
        List<JsonObject> serializedMembers = new LinkedList<>();

        User user = findByName(username);

        if(user != null) {
//            user.getMemberList().stream().peek(o -> {
//                Hibernate.initialize(o.getRoom());
//                Hibernate.initialize(o.getRoom().getMemberList());
//                Hibernate.initialize(o.getRoom().getMessageList());
//                Hibernate.initialize(o.getUser());
//                Hibernate.initialize(o.getUser().getMemberList());
//                Hibernate.initialize(o.getUser().getRoomInviteList());
//                Hibernate.initialize(o.getUser().getFriendRequestList());
//            }).collect(Collectors.toList());

            user.getMemberList().forEach(member -> {
                serializedMembers.add(memberRepository.getSerializedMember(member.getId()));
            });
        }

        return serializedMembers;
    }

    public List<User> findAllFriends(String username) {
        List<User> friends = new LinkedList<>();

        User user = findByName(username);

        if (user != null) {
            friends = user.getFriendList();
        }

        friends.stream().peek(o -> {
            Hibernate.initialize(o);
            Hibernate.initialize(o.getFriendRequestList());
            Hibernate.initialize(o.getMemberList());
            Hibernate.initialize(o.getRoomInviteList());
            Hibernate.initialize(o.getFriendList());
            Hibernate.initialize(o.getAddUser());
        }).collect(Collectors.toList());

        return friends;
    }

    public List<RoomInvite> getAllRoomInvites(String username) {
        List<RoomInvite> roomInviteList = new LinkedList<>();
        User user = findByName(username);

        if (user != null) {
            user.getRoomInviteList().stream().peek(o -> {
                Hibernate.initialize(o.getSender());
                Hibernate.initialize(o.getReceiver());
                Hibernate.initialize(o.getRoom());
                Hibernate.initialize(o.getRoom().getMemberList());
                Hibernate.initialize(o.getRoom().getMessageList());
                Hibernate.initialize(o.getRoom().getPlaylist());
                Hibernate.initialize(o.getRoom().getPlaylist().getSongList());
            }).collect(Collectors.toList());

            roomInviteList = user.getRoomInviteList();
        }

        return roomInviteList;

    }

    public void friendUsers(User user1, User user2) {
        user1.getAddUser().add(user2);
        user2.getFriendList().add(user1);
        user2.getAddUser().add(user1);
        user1.getFriendList().add(user2);
    }

    public User initUser(User user) {
        Hibernate.initialize(user);
        Hibernate.initialize(user.getFriendRequestList());
        Hibernate.initialize(user.getMemberList());
        Hibernate.initialize(user.getRoomInviteList());
        Hibernate.initialize(user.getFriendList());
        Hibernate.initialize(user.getAddUser());
        return user;
    }

}

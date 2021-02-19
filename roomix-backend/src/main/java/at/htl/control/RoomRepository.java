package at.htl.control;

import at.htl.dto.ChatMessageDTO;
import at.htl.dto.RoomUpdateDTO;
import at.htl.entity.Room;
import at.htl.entity.Song;
import at.htl.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.hibernate.Hibernate;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class RoomRepository implements PanacheRepository<Room> {

    @Override
    public Room findById(Long id) {
        Room room = find("RM_ID", id).firstResult();

        if (room != null) {
            room = initRoom(room);
        }
        return room;
    }

    public List<Room> findAllRoomsWithSong(Song song) {
        List<Room> rooms = new LinkedList<>();

        List<Room> allRooms = streamAll().map(this::initRoom).collect(Collectors.toList());

        allRooms.forEach(room -> {
            if (room.getPlaylist().getSongList().contains(song)) {
                rooms.add(room);
            }
        });

        return rooms;
    }

    public List<Room> findPopularPublicRooms(int limit) {
        List<Room> rooms = list("isprivate", false).stream()
                .map(this::initRoom)
                .sorted((o1, o2) -> o2.getMemberList().size() - o1.getMemberList().size())
                .limit(limit)
                .collect(Collectors.toList());
        return rooms;
    }

    public Room update(RoomUpdateDTO roomUpdateDTO) {
        Room room = findById(roomUpdateDTO.getRoomId());

        if (room == null) return null;

        room.setName(roomUpdateDTO.getRoomname());
        room.setPrivate(roomUpdateDTO.isPrivate());

        return room;
    }

    public List<ChatMessageDTO> getAllMessagesInRoom(Long roomId) {
        Room room = findById(roomId);

        if (room == null) return null;

        List<ChatMessageDTO> messages = room.getMessageList().stream()
                .map(message -> new ChatMessageDTO(
                        message.getSender(),
                        message.getCreationDate(),
                        message.getContent()))
                .limit(50)
                .collect(Collectors.toList());

        return messages;
    }

    public Room initRoom(Room room) {
        Hibernate.initialize(room);
        Hibernate.initialize(room.getMessageList());
        Hibernate.initialize(room.getMemberList());
        Hibernate.initialize(room.getPlaylist());
        Hibernate.initialize(room.getPlaylist().getSongList());
        Hibernate.initialize(room.getPlaylist().getCurrentSong());
        return room;
    }
}

package at.htl.dto;

public class MemberDTO {

    String username;
    Long roomId;

    public MemberDTO() {
    }

    public MemberDTO(String username, Long roomId) {
        this.username = username;
        this.roomId = roomId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}

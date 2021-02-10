package at.htl.dto;

public class RoomInviteDTO {

    private String sender;
    private String receiver;
    private Long roomId;

    public RoomInviteDTO() {
    }

    public RoomInviteDTO(String sender, String receiver, Long roomId) {
        this.sender = sender;
        this.receiver = receiver;
        this.roomId = roomId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}

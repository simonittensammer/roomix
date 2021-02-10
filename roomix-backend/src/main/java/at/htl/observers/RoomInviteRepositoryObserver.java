package at.htl.observers;

public interface RoomInviteRepositoryObserver {

    public void sendRoomInvite(String receiver);

    public void respontToRoomInvite(String sender, String receiver, Long roomId);
}

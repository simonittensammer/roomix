package at.htl.observers;

public interface FriendRequestRepositoryObserver {

    public void sendFriendRequest(String receiver);

    public void respondToFriendRequest(String sender, String receiver);

    public void unfriendUsers(String sender, String receiver);
}

package at.htl.observers;

import at.htl.entity.Member;
import at.htl.entity.Song;

public interface MemberRepositoryObserver {

    void update(Long roomId);
}

package com.zrkworld.sns.friend.dao;


import com.zrkworld.sns.friend.pojo.NoFriend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoFriendDao extends JpaRepository<NoFriend, String> {
    NoFriend findByUserIdAndFriendId(String userId, String friendId);
}

package com.zrkworld.sns.friend.pojo;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tb_friend")
@IdClass(Friend.class)  // 表示是联合主键
public class Friend implements Serializable {
    @Id
    private String userId;
    @Id
    private String friendId;

    private String islike;

    @Override
    public String toString() {
        return "Friend{" +
                "userId='" + userId + '\'' +
                ", friendId='" + friendId + '\'' +
                ", islike='" + islike + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getIslike() {
        return islike;
    }

    public void setIslike(String islike) {
        this.islike = islike;
    }
}

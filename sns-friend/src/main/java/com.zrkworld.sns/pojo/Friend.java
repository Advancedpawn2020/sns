package com.tensquare.friend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;


@TableName("tb_friend")
public class Friend implements Serializable {
    @TableId(type = IdType.INPUT)
    private String userId;

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

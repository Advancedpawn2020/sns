package com.zrkworld.sns.notice.pojo;


import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_notice_fresh")
public class NoticeFresh {
    @Id
    private String userId;
    @Id
    private String noticeId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }
}
package com.zrkworld.sns.notice.dao;

import com.zrkworld.sns.notice.pojo.NoticeFresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/31 0031 22:01
 */
public interface NoticeFreshDao extends JpaRepository<NoticeFresh,String>, JpaSpecificationExecutor<NoticeFresh> {
    void deleteByUserIdAndNoticeId(String userId, String noticeId);
}

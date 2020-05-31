package com.zrkworld.sns.notice.dao;

import com.zrkworld.sns.notice.pojo.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/31 0031 19:18
 */
public interface NoticeDao extends JpaRepository<Notice, String> ,JpaSpecificationExecutor<Notice> {
}

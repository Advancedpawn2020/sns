package com.zrkworld.sns.article.dao;

import com.zrkworld.sns.article.pojo.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 频道
 * @author zrk
 *
 */
public interface ChannelDao extends JpaRepository<Channel,String>, JpaSpecificationExecutor<Channel> {
	
}

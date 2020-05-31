package com.zrkworld.sns.article.dao;

import com.zrkworld.sns.article.pojo.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 数据访问接口
 * @author zrk
 *
 */
public interface ColumnDao extends JpaRepository<Column,String>, JpaSpecificationExecutor<Column> {
	
}

package com.zrkworld.sns.article.dao;

import com.zrkworld.sns.article.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 文章
 * @author zrk
 *
 *
 */
public interface ArticleDao extends JpaRepository<Article,String>, JpaSpecificationExecutor<Article> {
    /**
     * 文章审核
     * @param id
     */
    @Modifying
    @Query(value = "UPDATE tb_article SET state = 1 WHERE id = ?", nativeQuery = true)
    void updateState(String id);

    @Modifying
    @Query(value = "UPDATE tb_article SET thumbup = thumbup + 1 WHERE id = ?", nativeQuery = true)
    void addThumbup(String id);
}

package com.zrkworld.sns.search.dao;

import com.zrkworld.sns.search.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleDao extends ElasticsearchRepository<Article, String> {
    Page<Article> findByTitleOrContentLike(String title, String content, Pageable pageable);
}

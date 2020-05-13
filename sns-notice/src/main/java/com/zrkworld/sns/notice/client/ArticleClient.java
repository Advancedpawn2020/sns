package com.zrkworld.sns.notice.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("sns-article")
public interface ArticleClient {

    //根据文章id查询文章数据
    // GET /article/{articleId} 根据ID查询文章
    @RequestMapping(value = "article/{articleId}", method = RequestMethod.GET)
    public Result findById(@PathVariable("articleId") String articleId);
}

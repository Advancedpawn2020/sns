package com.zrkworld.sns.article.client;

import com.zrkworld.sns.article.pojo.Notice;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author zrk
 */
@FeignClient("sns-notice")
public interface NoticeClient {

    // 新增通知
    // http://127.0.0.1:9014/notice  POST
    @RequestMapping(value = "notice", method = RequestMethod.POST)
    public Result save(@RequestBody Notice notice);
}

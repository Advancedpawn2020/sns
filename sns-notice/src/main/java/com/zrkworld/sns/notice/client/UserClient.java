package com.zrkworld.sns.notice.client;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("tensquare-user")
public interface UserClient {

    //根据用户id查询用户
    //http://127.0.0.1:9008/user/{userId}   GET
    @RequestMapping(value = "user/{userId}", method = RequestMethod.GET)
    public Result selectById(@PathVariable("userId") String userId);
}

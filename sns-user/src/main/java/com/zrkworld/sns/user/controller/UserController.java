package com.zrkworld.sns.user.controller;

import com.zrkworld.sns.user.pojo.User;
import com.zrkworld.sns.user.service.UserService;
import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/12 0012 17:47
 */
@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    @Resource
    UserService userService;

    //根据id查询用户
    //http:127.0.0.1:9008/user/{userId}
    @RequestMapping(value="{userId}", method= RequestMethod.GET)
    public Result selectById(@PathVariable String userId){
        User user = userService.selectUserById(userId);
        return new Result(true, StatusCode.OK,"查询成功",user);
    }
}

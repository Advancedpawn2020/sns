package com.zrkworld.sns.user.service;

import com.zrkworld.sns.user.mapper.UserMapper;
import com.zrkworld.sns.user.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/12 0012 17:46
 */
@Service
public class UserService {
    @Resource
    UserMapper userMapper;


    //根据id查询用户
    public User selectUserById(String userId) {
        return userMapper.selectById(userId);
    }
}

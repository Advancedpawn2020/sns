package com.zrkworld.sns.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/12 0012 17:39
 */
@SpringBootApplication
@MapperScan("com.zrkworld.sns.user.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}

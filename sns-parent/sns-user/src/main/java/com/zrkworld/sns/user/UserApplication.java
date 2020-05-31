package com.zrkworld.sns.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import utils.IdWorker;
import utils.JwtUtil;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/12 0012 17:39
 */
@SpringBootApplication
@EnableEurekaClient

public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);

    }
    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1, 1);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}

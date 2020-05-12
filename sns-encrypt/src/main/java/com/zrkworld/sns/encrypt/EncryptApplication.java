package com.zrkworld.sns.encrypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/13 0013 0:03
 */
@SpringBootApplication
@EnableEurekaClient
//开启zuul网关代理
@EnableZuulProxy
public class EncryptApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptApplication.class,args);
    }
}

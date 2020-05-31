package com.zrkworld.sns.encrypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/13 0013 0:03
 * 用来对前端加密的内容解密,利用zuul网关代理技术,防止抓包软件抓取明文数据,保护了数据的安全性
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

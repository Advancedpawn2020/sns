package com.zrkworld.sns.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import utils.IdWorker;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/10 0010 18:32
 */
@SpringBootApplication
@EnableEurekaClient
//添加Fegin客户端
@EnableFeignClients
public class ArticleApplication {
    public static void main(String [] args){
        SpringApplication.run(ArticleApplication.class, args);

    }

    //Bean注解需要卸载Configuration注解的配置类中的，实际上启动类就是一个配置类（注解中包含了）
    //加上Bean注解可以在spring注入的时候创建实例(因为这个类没有被@Service之类的注解，相当于没有被spring容器托管)
    @Bean
    public IdWorker createIdWorker(){
        //第一个id号，第二个序列号
        return new IdWorker(1,1);
    }
}

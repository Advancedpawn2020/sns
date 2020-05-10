package com.zrkworld.sns.article.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/11 0011 1:05
 * 配置分页插件
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public PaginationInterceptor createPaginationInterceptor(){
        return new PaginationInterceptor();
    }
}

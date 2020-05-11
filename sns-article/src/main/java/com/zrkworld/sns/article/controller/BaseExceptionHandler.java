package com.zrkworld.sns.article.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zrk
 * @version 1.0
 * @date 2020/5/11 0011 20:56
 * 公共异常处理类，相当于ExeptionResolver
 */
@ControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handler(Exception e){
        if(e instanceof NullPointerException){
            System.out.println("空指针");
        }
        System.out.println("处理异常");
        return new Result(false, StatusCode.ERROR,e.getMessage());
    }

}

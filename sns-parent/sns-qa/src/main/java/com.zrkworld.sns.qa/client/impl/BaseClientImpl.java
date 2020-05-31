package com.zrkworld.sns.qa.client.impl;

import com.zrkworld.sns.qa.client.BaseClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class BaseClientImpl implements BaseClient {
    @Override
    public Result findById(String id) {
        return new Result(false, StatusCode.ERROR.getCode(), "熔断器触发了");
    }
}

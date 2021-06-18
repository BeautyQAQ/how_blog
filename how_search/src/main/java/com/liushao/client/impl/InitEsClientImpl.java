package com.liushao.client.impl;

import com.liushao.client.InitEsClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class InitEsClientImpl implements InitEsClient {
    @Override
    public Result init() {
        System.out.println("init熔断器启动了");
        return new Result(false, StatusCode.ERROR,"init熔断器启动了");
    }
}

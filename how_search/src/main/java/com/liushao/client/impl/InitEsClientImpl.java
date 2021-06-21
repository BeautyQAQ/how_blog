package com.liushao.client.impl;

import com.liushao.client.InitEsClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitEsClientImpl implements InitEsClient {
    @Override
    public Result init() {
        log.info("init熔断器启动了");
        return new Result(false, StatusCode.ERROR,"init熔断器启动了");
    }
}

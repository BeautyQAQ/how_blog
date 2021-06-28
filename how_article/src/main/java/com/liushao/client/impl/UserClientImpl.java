package com.liushao.client.impl;

import com.liushao.client.UserClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientImpl implements UserClient {
    @Override
    public Result findNicknameById(String id) {
        log.info("熔断器启动了");
        return new Result(false, StatusCode.ERROR,"熔断器启动了");
    }
}

package com.liushao.client.impl;

import com.liushao.client.UserClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {
    @Override
    public Result findNicknameById(String id) {
        System.out.println("熔断器启动了");
        return new Result(false, StatusCode.ERROR,"熔断器启动了");
    }
}

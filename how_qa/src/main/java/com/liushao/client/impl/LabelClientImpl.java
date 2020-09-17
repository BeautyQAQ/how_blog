package com.liushao.client.impl;

import com.liushao.client.LabelClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String id) {
        return new Result(false, StatusCode.ERROR,"熔断器启动了");
    }
}

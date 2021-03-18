package com.liushao.client.impl;

import com.liushao.client.LabelClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import org.springframework.stereotype.Component;

/**
 * @author huangshen
 */
@Component
public class LabelClientImpl implements LabelClient {
    @Override
    public Result findById(String id) {
        System.out.println("熔断器启动了");
        return new Result(false, StatusCode.ERROR,"熔断器启动了");
    }
}

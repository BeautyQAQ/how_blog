package com.liushao.client;

import com.liushao.client.impl.InitEsClientImpl;
import com.liushao.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 提供初始化elasticsearch所需的基础数据(所有能搜索的数据,目前主要来自文章服务)
 * @author huangshen
 */
@FeignClient(value = "how-article", fallback = InitEsClientImpl.class)
public interface InitEsClient {

    /**
     * 从文章服务拿到所有的数据库数据
     * @return Result
     */
    @RequestMapping(value="/init/data", method = RequestMethod.POST)
    Result init();
}

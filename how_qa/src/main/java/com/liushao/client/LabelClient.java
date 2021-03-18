package com.liushao.client;

import com.liushao.client.impl.LabelClientImpl;
import com.liushao.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author huangshen
 */
@FeignClient(value = "how-base", fallback = LabelClientImpl.class)
public interface LabelClient {
    @RequestMapping(value="/base/label/{id}", method = RequestMethod.GET)
    Result findById(@PathVariable("id") String id);
}


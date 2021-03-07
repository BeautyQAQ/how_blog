package com.liushao.client;

import com.liushao.client.impl.UserClientImpl;
import com.liushao.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author huangshen
 */
@FeignClient(value = "how-user", fallback = UserClientImpl.class)
public interface UserClient {
    /**
     * 根据id查询用户
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/user/nickname/{id}", method = RequestMethod.GET)
    Result findNicknameById(@PathVariable("id") String id);
}


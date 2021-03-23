package com.liushao.client.impol;

import com.liushao.client.UserClient;
import org.springframework.stereotype.Component;

/**
 * @author huangshen
 */
@Component
public class UserClientImpl implements UserClient {
    @Override
    public void incFanscount(String userid, int x) {
        System.out.println("User服务熔断器启动了,添加粉丝失败");
    }

    @Override
    public void incFollowcount(String userid, int x) {
        System.out.println("User服务熔断器启动了，添加关注失败");
    }
}

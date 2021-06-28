package com.liushao.listener;

import com.liushao.util.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信监听类
 * @author huangshen
 */
@Component
//这个模式为自动创建队列
@RabbitListener(queuesToDeclare = @Queue("sms"))
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;
    @Value("${aliyun.sms.template_code}")
    private String template_code;//模板编号
    @Value("${aliyun.sms.sign_name}")
    private String sign_name;//签名

    private static final Logger log = LoggerFactory.getLogger(SmsListener.class);

    /**
     * 发送短信
     * @param map
     */
    @RabbitHandler
    public void sendSms(Map<String,String> map){
        log.info("手机号："+map.get("mobile"));
        log.info("验证码："+map.get("code"));
        smsUtil.sendSms(map.get("mobile"),template_code,sign_name,"{\"code\":\""+ map.get("code") +"\"}");
    }
}

package com.liushao.listener;

import com.aliyuncs.CommonResponse;
import com.liushao.util.SmsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 短信监听类
 * @author huangshen
 */
@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private JsonMapper jsonMapper;
    @Value("${aliyun.sms.template_code}")
    private String template_code;//模板编号
    @Value("${aliyun.sms.sign_name}")
    private String sign_name;//签名

    private static final Logger log = LoggerFactory.getLogger(SmsListener.class);

    /**
     * 发送短信
     * @param map
     */
    @KafkaListener(topics = "${messaging.sms.topic:sms}")
    public void sendSms(Map<String,String> map,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp){
        // Kafka 会保留历史记录；不再发送超过 Redis 五分钟有效期的验证码。
        if (System.currentTimeMillis() - timestamp >= TimeUnit.MINUTES.toMillis(5)) {
            log.info("忽略已过期的短信验证码消息");
            return;
        }
        String mobile = map.get("mobile");
        String code = map.get("code");
        if (mobile == null || mobile.isBlank() || code == null || !code.matches("[0-9]{6}")) {
            throw new IllegalArgumentException("短信验证码消息格式不正确");
        }
        CommonResponse response = smsUtil.sendSms(mobile, template_code, sign_name,
                "{\"code\":\"" + code + "\"}");
        if (response == null || !"OK".equals(jsonMapper.readTree(response.getData()).path("Code").asString())) {
            throw new IllegalStateException("阿里云短信发送失败");
        }
    }
}

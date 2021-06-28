package com.liushao.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 短信工具类
 * @author huangshen
 */
@Component
public class SmsUtil {

    @Autowired
    private Environment env;

    private static final Logger log = LoggerFactory.getLogger(SmsUtil.class);

    /**
    * 发送短信
    * @param mobile 手机号
    * @param template_code 模板号
    * @param sign_name 签名
    * @param param 参数
    * @return CommonResponse
    */
    public CommonResponse sendSms(String mobile,String template_code,String sign_name,String param){
        String accessKeyId =env.getProperty("aliyun.sms.accessKeyId");
        String accessKeySecret = env.getProperty("aliyun.sms.accessKeySecret");

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //必填:待发送手机号
        request.putQueryParameter("PhoneNumbers", mobile);
        //必填:短信签名‐可在短信控制台中找到
        request.putQueryParameter("SignName", sign_name);
        //必填:短信模板‐可在短信控制台中找到
        request.putQueryParameter("TemplateCode", template_code);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.putQueryParameter("TemplateParam", param);
        try {
            return client.getCommonResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    public CommonResponse querySendDetails(String mobile,String bizId) {
        String accessKeyId =env.getProperty("aliyun.sms.accessKeyId");
        String accessKeySecret = env.getProperty("aliyun.sms.accessKeySecret");

        //初始化acsClient,暂不支持region化
        DefaultProfile profile = DefaultProfile.getProfile("cn‐hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("QuerySendDetails");
        request.putQueryParameter("RegionId", "cn-hangzhou");

        //必填:待发送手机号
        request.putQueryParameter("PhoneNumbers", mobile);
        //可选‐流水号
        request.putQueryParameter("BizId", bizId);
        //必填‐发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.putQueryParameter("SendDate", ft.format(new Date()));
        //必填‐页大小
        request.putQueryParameter("PageSize", "1");
        //必填‐当前页码从1开始计数
        request.putQueryParameter("CurrentPage", "1");

        try {
            return client.getCommonResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}

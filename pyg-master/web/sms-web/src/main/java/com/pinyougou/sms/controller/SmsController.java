package com.pinyougou.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sms.service.SmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-10-22 20:45
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Reference(timeout = 10000)
    private SmsService smsService;

    /**
     * 发送短信的方法
     *
     * @param phone         手机号码
     * @param signName      签名
     * @param templateCode  短信模板
     * @param templateParam 模板参数（json格式）{"name" : "", "code" : ""}
     * @return JSON格式字符串：{"success" : true|false}
     * 说明：true: 代表发送成功、false:代表发送失败
     */
    @PostMapping("/sendSms")
    public Map<String, Object> sendSms(String phone, String signName,
                                       String templateCode, String templateParam) {
        // 发送短信
        boolean success = smsService.sendSms(phone,
                signName, templateCode, templateParam);
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        return map;
    }

}

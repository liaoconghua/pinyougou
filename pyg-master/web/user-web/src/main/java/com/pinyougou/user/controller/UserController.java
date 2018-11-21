package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.User;
import com.pinyougou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-10-22 21:03
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    /**
     * 发送短信验证码
     */
    @GetMapping("/sendCode")
    public boolean sendCode(String phone) {
        try {
            if (StringUtils.isNoneBlank(phone)) {
                // 发送验证码
                return userService.sendCode(phone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 校验手机验证码和随机验证码是否正确
     *
     * @param verifyCode 随机验证码
     * @param phone      手机号
     * @param smsCode    短信验证码
     * @param request    请求对象
     * @return 校验结果
     */
    @GetMapping("/checkSmsCode")
    public boolean checkSmsCode(String verifyCode, String phone, String smsCode, HttpServletRequest request) {
        String verifyCodeSession = (String) request.getSession().getAttribute("verifyCode");
        boolean ok = false;
        if (verifyCodeSession.equalsIgnoreCase(verifyCode)) {
            ok = userService.checkSmsCode(phone, smsCode);
        }
        return ok;
    }

    /**
     * 注册用户
     */
    @PostMapping("/save")
    public boolean save(@RequestBody User user, String smsCode) {
        try {
            boolean ok = userService.checkSmsCode(user.getPhone(), smsCode);
            if (ok) {
                userService.save(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 分页查询该用户的所有订单
     *
     * @param params 参数集合
     * @return 结果数据集
     */
    @PostMapping("/findOrderByPage")
    public Map<String, Object> findOrderByPage(@RequestBody Map<String, Object> params) {
        Object username = params.get("username");
        if (username == null) {
            return null;
        }
        return userService.findOrderByPage(params);
    }

}

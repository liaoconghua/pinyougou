package com.pinyougou.user.service;

import com.pinyougou.common.pojo.User;

import java.util.Map;

/**
 * @author Fa
 * @date 2018-10-22 21:06
 */
public interface UserService {

    /**
     * 注册用户
     *
     * @param user 用户
     */
    void save(User user);

    /**
     * 发送短信验证码
     *
     * @param phone 接收短信的手机号
     */
    boolean sendCode(String phone);

    /**
     * 检查短信验证码是否正确
     *
     * @param phone   接收短信的手机号
     * @param smsCode 短信验证码
     * @return 验证是否成功
     */
    boolean checkSmsCode(String phone, String smsCode);

    /**
     * 分页查询该用户的所有订单
     *
     * @param params 参数集合
     * @return 结果数据集
     */
    Map<String, Object> findOrderByPage(Map<String, Object> params);
}

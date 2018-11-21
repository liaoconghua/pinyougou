package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.cart.mapper.OrderItemMapper;
import com.pinyougou.cart.mapper.OrderMapper;
import com.pinyougou.common.pojo.User;
import com.pinyougou.common.utils.HttpClientUtils;
import com.pinyougou.user.mapper.UserMapper;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Fa
 * @date 2018-10-22 21:11
 */
@Transactional
@Service(interfaceName = "com.pinyougou.user.service.UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${sms.url}")
    private String smsUrl;
    @Value("${sms.signName}")
    private String signName;
    @Value("${sms.templateCode}")
    private String templateCode;

    /**
     * 注册用户
     *
     * @param user 用户
     */
    @Override
    public void save(User user) {
        try {
            // 创建日期
            user.setCreated(new Date());
            // 修改日期
            user.setUpdated(user.getCreated());
            // 密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送短信验证码
     *
     * @param phone 接收短信的手机号
     */
    @Override
    public boolean sendCode(String phone) {
        try {
            //生成6位随机数
            String code = UUID.randomUUID().toString()
                    .replaceAll("-", "")
                    .replaceAll("[a-z|A-Z]", "")
                    .substring(0, 6);
            System.out.println("验证码：" + code);
            //调用短信发送接口
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            // 创建Map集合封装请求参数
            Map<String, String> param = new HashMap<>(4);
            param.put("phone", phone);
            param.put("signName", signName);
            param.put("templateCode", templateCode);
            param.put("templateParam", "{\"code\":\"" + code + "\"}");
            // 发送Post请求
            String content = httpClientUtils.sendPost(smsUrl, param);
            // 把json字符串转化成Map
            Map<String, Object> resMap = JSON.parseObject(content, Map.class);
            //存入Redis中(90秒)
            redisTemplate.boundValueOps(phone).set(code, 90, TimeUnit.SECONDS);
            return (boolean) resMap.get("success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查短信验证码是否正确
     *
     * @param phone   接收短信的手机号
     * @param smsCode 短信验证码
     * @return 验证是否成功
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //获取Redis中存储的验证码
        String sysCode = redisTemplate.boundValueOps(phone).get();
        return StringUtils.isNoneBlank(sysCode) && sysCode.equals(smsCode);
    }

    /**
     * 分页查询该用户的所有订单
     *
     * @param params 参数集合
     * @return 结果数据集
     */
    @Override
    public Map<String, Object> findOrderByPage(Map<String, Object> params) {

        //获取用户名
        String username = (String) params.get("username");
        // 获取当前页码
        Integer pageNum = (Integer) params.get("pageNum");
        if (pageNum == null) {
            pageNum = 1;
        }
        //获取每页显示的记录数
        Integer pageSize = (Integer) params.get("pageSize");
        if (pageSize == null) {
            pageSize = 20;
        }

        Map<String, Object> resultDataMap = new HashMap<>(2);
        PageInfo<Map<String, Object>> orderPageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> orderMapper.findOrderByUsername(params));
        resultDataMap.put("orderList", orderPageInfo.getList());
        resultDataMap.put("totalPages", orderPageInfo.getPages());
        return resultDataMap;
    }
}

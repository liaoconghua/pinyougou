package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.Areas;
import com.pinyougou.common.pojo.Cities;
import com.pinyougou.common.pojo.Provinces;
import com.pinyougou.common.pojo.User;
import com.pinyougou.user.service.UserService;
import com.pinyougou.user.service.UserSettingService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-11-01 20:19
 */
@RestController
@RequestMapping("/userSetting")
public class UserSettingController {

    @Reference(timeout = 10000)
    private UserSettingService userSettingService;
    @Reference(timeout = 10000)
    private UserService userService;

    /**
     * 获取当前用户的详情
     *
     * @return 用户信息
     */
    @GetMapping("/userInfo")
    public User userInfo(HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            return userSettingService.userInfo(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有地址列表
     *
     * @return 地址列表
     */
    @GetMapping("/findAddressList")
    public Map<String, Object> findAddressList() {
        try {
            return userSettingService.findAddressList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询省份列表
     *
     * @return 省份列表
     */
    @GetMapping("/findProvinceList")
    public List<Provinces> findProvinceList() {
        try {
            return userSettingService.findProvinceList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询市级列表
     *
     * @return 市级列表
     */
    @GetMapping("/findCityList")
    public List<Cities> findCityList(String provinceId) {
        try {
            return userSettingService.findCityList(provinceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询区域列表
     *
     * @return 区域列表
     */
    @GetMapping("/findAreaList")
    public List<Areas> findAreaList(String cityId) {
        try {
            return userSettingService.findAreaList(cityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存用户修改的信息
     *
     * @param user 用户信息
     * @return 修改结果
     */
    @PostMapping("/updateUserInfo")
    public boolean updateUserInfo(@RequestBody User user) {
        return userSettingService.updateUserInfo(user);
    }

    /**
     * 修改用户密码
     *
     * @param user    用户
     * @param request 请求对象
     * @return 修改结果
     */
    @PostMapping("/updatePwd")
    public boolean updatePwd(@RequestBody User user, HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            user.setUsername(username);
            return userSettingService.updatePwd(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新用户手机号
     *
     * @return 更新结果
     */
    @PostMapping("/updatePhone")
    public boolean updatePhone(@RequestBody Map<String, String> dataMap, HttpServletRequest request) {
        boolean ok = false;
        try {
//            int i = 1 / 0;
            String verifyCodeSession = (String) request.getSession().getAttribute("verifyCode");
            if (verifyCodeSession.equalsIgnoreCase(dataMap.get("verifyCode"))) {
                ok = userService.checkSmsCode(dataMap.get("newPhone"), dataMap.get("smsCode"));
            }
        } catch (Exception e) {
            throw new RuntimeException("校验失败！");
        }
        boolean updateOk = false;
        try {
            if (ok) {
                String username = request.getRemoteUser();
                User user = new User();
                user.setUsername(username);
                user.setPhone(dataMap.get("newPhone"));
                updateOk = userSettingService.updatePhone(user);
            }
        } catch (Exception e) {
            throw new RuntimeException("更新失败！");
        }
        return ok && updateOk;
    }
}

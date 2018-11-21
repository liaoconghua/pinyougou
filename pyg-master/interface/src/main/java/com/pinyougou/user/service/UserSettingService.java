package com.pinyougou.user.service;

import com.pinyougou.common.pojo.Areas;
import com.pinyougou.common.pojo.Cities;
import com.pinyougou.common.pojo.Provinces;
import com.pinyougou.common.pojo.User;

import java.util.List;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-11-01 20:26
 */
public interface UserSettingService {

    /**
     * 获取用户信息
     *
     * @param username 当前登录用户名
     * @return 用户信息
     */
    User userInfo(String username);

    /**
     * 获取地址列表
     *
     * @return 地址列表
     */
    Map<String, Object> findAddressList();

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return 保存结果
     */
    boolean updateUserInfo(User user);

    /**
     * 查询所有省级列表
     *
     * @return 省级列表
     */
    List<Provinces> findProvinceList();

    /**
     * 查询当前省份下的城市
     *
     * @param provinceId 省份id
     * @return 城市列表
     */
    List<Cities> findCityList(String provinceId);

    /**
     * 查询所有当前城市下的区域列表
     *
     * @param cityId 城市id
     * @return 区域列表
     */
    List<Areas> findAreaList(String cityId);

    /**
     * 修改用户密码
     *
     * @param user 用户
     * @return 修改结果
     */
    boolean updatePwd(User user);

    /**
     * 更新手机号
     *
     * @param user 当前登录用户
     * @return 更新结果
     */
    boolean updatePhone(User user);
}

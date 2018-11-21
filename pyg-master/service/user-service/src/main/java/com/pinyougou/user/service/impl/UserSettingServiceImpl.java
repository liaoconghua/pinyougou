package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.pojo.Areas;
import com.pinyougou.common.pojo.Cities;
import com.pinyougou.common.pojo.Provinces;
import com.pinyougou.common.pojo.User;
import com.pinyougou.user.mapper.AreasMapper;
import com.pinyougou.user.mapper.CitiesMapper;
import com.pinyougou.user.mapper.ProvincesMapper;
import com.pinyougou.user.mapper.UserSettingMapper;
import com.pinyougou.user.service.UserSettingService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fa
 * @date 2018-11-01 20:25
 */
@Transactional
@Service(interfaceName = "com.pinyougou.user.service.UserSettingService")
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    private UserSettingMapper userSettingMapper;
    @Autowired
    private ProvincesMapper provincesMapper;
    @Autowired
    private CitiesMapper citiesMapper;
    @Autowired
    private AreasMapper areasMapper;

    /**
     * 获取用户信息
     *
     * @param username 当前登录用户名
     * @return 用户信息
     */
    @Override
    public User userInfo(String username) {
        try {
            User user = new User();
            user.setUsername(username);
            return userSettingMapper.selectOne(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取地址列表
     *
     * @return 地址列表
     */
    @Override
    public Map<String, Object> findAddressList() {
        //定义结果数据模型
        HashMap<String, Object> addressMap = new HashMap<>(3);
        try {
            List<Provinces> provincesList = provincesMapper.selectAll();
            List<Cities> citiesList = citiesMapper.selectAll();
            List<Areas> areasList = areasMapper.selectAll();
            addressMap.put("provincesList", provincesList);
            addressMap.put("citiesList", citiesList);
            addressMap.put("areasList", areasList);
            return addressMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询所有省级列表
     *
     * @return 省级列表
     */
    @Override
    public List<Provinces> findProvinceList() {
        return provincesMapper.selectAll();
    }

    /**
     * 查询当前省份下的城市
     *
     * @param provinceId 省份id
     * @return 城市列表
     */
    @Override
    public List<Cities> findCityList(String provinceId) {
        Example example = new Example(Cities.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("provinceId", provinceId);
        return citiesMapper.selectByExample(example);
    }

    /**
     * 查询所有当前城市下的区域列表
     *
     * @param cityId 城市id
     * @return 区域列表
     */
    @Override
    public List<Areas> findAreaList(String cityId) {
        Example example = new Example(Areas.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cityId", cityId);
        return areasMapper.selectByExample(example);
    }

    /**
     * 修改用户密码
     *
     * @param user 用户
     * @return 修改结果
     */
    @Override
    public boolean updatePwd(User user) {
        try {
            // 密码加密
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("username", user.getUsername());
            int row = userSettingMapper.updateByExampleSelective(user, example);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存用户信息
     *
     * @param user 用户信息
     * @return 保存结果
     */
    @Override
    public boolean updateUserInfo(User user) {
        try {
            int row = userSettingMapper.updateByPrimaryKeySelective(user);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新手机号
     *
     * @param user 当前登录用户
     * @return 更新结果
     */
    @Override
    public boolean updatePhone(User user) {
        try {
            Example example = new Example(User.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("username", user.getUsername());
            int row = userSettingMapper.updateByExampleSelective(user, example);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.pojo.Address;
import com.pinyougou.user.mapper.AddressMapper;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Fa
 * @date 2018-10-28 16:37
 */
@Transactional
@Service(interfaceName = "com.pinyougou.user.service.AddressService")
public class AddressServiceImpl implements AddressService {

    /**
     * 注入数据访问接口代理对象
     */
    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户编号查询地址
     *
     * @param userId 用户编号
     * @return 地址集合
     */
    @Override
    public List<Address> findAddressByUser(String userId) {
        try {
            // 创建地址对象封装查询条件
            Address address = new Address();
            address.setUserId(userId);

            return addressMapper.select(address);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加用户地址
     *
     * @param address 用户地址详情数据
     * @return 是否添加成功
     */
    @Override
    public boolean addAddress(Address address) {
        try {
            address.setCreateDate(new Date());
            address.setIsDefault("0");
            int row = addressMapper.insertSelective(address);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 更新用户地址
     *
     * @param address 地址详情
     * @return 是否添加成功
     */
    @Override
    public boolean updateAddress(Address address) {
        try {
            int row = addressMapper.updateByPrimaryKeySelective(address);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除用户地址
     *
     * @param id 用户唯一id
     * @return 是否删除成功
     */
    @Override
    public boolean deleteAddress(Long id) {
        try {
            int row = addressMapper.deleteByPrimaryKey(id);
            return row > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置默认地址
     *
     * @param ids 需修改的地址id
     */
    @Override
    public void setDefaultAddress(Long[] ids) {
        try {
            Address defaultAddress = new Address();
            defaultAddress.setId(ids[0]);
            defaultAddress.setIsDefault("1");
            if (ids.length > 1) {
                Address address = new Address();
                address.setId(ids[1]);
                address.setIsDefault("0");
                addressMapper.updateByPrimaryKeySelective(address);
            }
            addressMapper.updateByPrimaryKeySelective(defaultAddress);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

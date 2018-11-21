package com.pinyougou.user.service;

import com.pinyougou.common.pojo.Address;

import java.util.List;

/**
 * @author Fa
 * @date 2018-10-28 16:34
 */
public interface AddressService {

    /**
     * 根据用户编号查询地址
     *
     * @param userId 用户编号
     * @return 地址集合
     */
    List<Address> findAddressByUser(String userId);

    /**
     * 添加用户地址
     *
     * @param address 用户地址详情数据
     * @return 是否添加成功
     */
    boolean addAddress(Address address);

    /**
     * 更新用户地址
     *
     * @param address 地址详情
     * @return 是否添加成功
     */
    boolean updateAddress(Address address);

    /**
     * 删除用户地址
     * @param id 用户唯一id
     * @return 是否删除成功
     */
    boolean deleteAddress(Long id);

    /**
     * 设置默认地址
     * @param ids 需修改的地址id
     */
    void setDefaultAddress(Long[] ids);
}

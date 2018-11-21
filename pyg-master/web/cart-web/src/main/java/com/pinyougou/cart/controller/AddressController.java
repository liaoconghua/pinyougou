package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.common.pojo.Address;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Fa
 * @date 2018-10-28 16:33
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Reference(timeout = 10000)
    private AddressService addressService;

    /**
     * 获取登录用户的地址列表
     */
    @GetMapping("/findAddressByUser")
    public List<Address> findAddressByUser(HttpServletRequest request) {
        // 获取登录用户名
        String userId = request.getRemoteUser();
        return addressService.findAddressByUser(userId);
    }

    /**
     * 添加地址详情
     *
     * @param address 地址详情
     * @return 是否添加成功
     */
    @PostMapping("/addAddress")
    public boolean addAddress(@RequestBody Address address, HttpServletRequest request) {
        String userName = request.getRemoteUser();
        address.setUserId(userName);
        return addressService.addAddress(address);
    }

    /**
     * 更新地址详情
     *
     * @param address 地址详情
     * @return 是否添加成功
     */
    @PostMapping("/updateAddress")
    public boolean updateAddress(@RequestBody Address address) {
        return addressService.updateAddress(address);
    }

    /**
     * 删除用户地址
     *
     * @param id 用户唯一id
     * @return 是否删除成功
     */
    @GetMapping("/deleteAddress")
    public boolean deleteAddress(Long id) {
        return addressService.deleteAddress(id);
    }
}

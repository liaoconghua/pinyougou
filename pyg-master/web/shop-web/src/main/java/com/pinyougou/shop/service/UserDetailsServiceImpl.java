package com.pinyougou.shop.service;

import com.pinyougou.common.pojo.Seller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户认证服务类
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * 注入商家服务接口代理对象
     */
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        // 创建List集合封装角色
        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        // 添加角色
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        // 根据登录名查询商家
        Seller seller = sellerService.findOne(username);
        // 判断商家是否为空 并且 商家已审核
        if (seller != null && "1".equals(seller.getStatus())) {
            // 返回用户信息对象
            return new User(username, seller.getPassword(), grantedAuths);
        }
        return null;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}

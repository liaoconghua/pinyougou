package com.pinyougou.user.controller;


import com.pinyougou.common.utils.VerifyCodeUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author Fa
 * @date 2018-10-23 22:42
 */
@RestController
public class LoginController {

    @GetMapping("/user/showName")
    public Map<String, String> showName() {
        // 获取用户登录名
        String name = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        Map<String, String> map = new HashMap<>();
        map.put("loginName", name);
        return map;
    }


    /**
     * 获取验证码图片
     */
    @RequestMapping("/user/verifyCodeImage")
    public void verifyCodeImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            //在内存中创建一个图片，默认黑色背景
            BufferedImage bufferedImage = VerifyCodeUtil.paintImage(100, 36);
            String verifyCode = VerifyCodeUtil.getCode();

            //服务器通知浏览器不要缓存
            response.setHeader("pragma", "no-cache");
            response.setHeader("cache-control", "no-cache");
            response.setHeader("expires", "0");

            //将验证码放入HttpSession中
            request.getSession().setAttribute("verifyCode", verifyCode);
            //输出验证码图片给前端
            ImageIO.write(bufferedImage, "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

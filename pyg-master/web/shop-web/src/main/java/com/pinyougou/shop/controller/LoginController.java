package com.pinyougou.shop.controller;

import com.pinyougou.common.utils.VerifyCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
//@RequestMapping("/login")
public class LoginController {

    /**
     * 注入身份认证管理器
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 显示登录用户名
     */
    @GetMapping("/showLoginName")
    @ResponseBody
    public Map<String, String> showLoginName() {
        // 获取登录用户名
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> data = new HashMap<>();
        data.put("loginName", loginName);
        return data;
    }

    /**
     * 自定义认证入口
     */
    @RequestMapping("/user/login")
    public String login(String username, String password, String verifyCode, HttpServletRequest request) {
        if ("post".equalsIgnoreCase(request.getMethod())) {
            // 创建用户名与密码认证对象
            UsernamePasswordAuthenticationToken token
                    = new UsernamePasswordAuthenticationToken(username, password);
            try {
                // 调用认证方法，返回认证对象
                Authentication authenticate = authenticationManager
                        .authenticate(token);
                // 判断是否认证成功
                String verifyCode1 = (String) request.getSession().getAttribute("verifyCode");
                if (authenticate.isAuthenticated() && verifyCode.equalsIgnoreCase(verifyCode1)) {
//                if (authenticate.isAuthenticated()) {
                    // 设置用户认证成功，往Session中添加认证通过信息
                    SecurityContextHolder.getContext()
                            .setAuthentication(authenticate);
                    // 重定向到登录成功页面
                    return "redirect:/admin/index.html";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/shoplogin.html";
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

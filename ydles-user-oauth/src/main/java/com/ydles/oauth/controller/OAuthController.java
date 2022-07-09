package com.ydles.oauth.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.oauth.service.AuthService;
import com.ydles.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Scz
 * @date 2022/4/16 21:38
 */
@Controller
@RequestMapping("/oauth")
public class OAuthController {
    @Autowired
    AuthService authService;
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @RequestMapping("/login")
    @ResponseBody
    public Result<String> login(String username, String password, HttpServletResponse response){
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);

        // 三 向response的cookie中存放jti
        Cookie cookie = new Cookie("uid", authToken.getJti());
        cookie.setMaxAge(cookieMaxAge);
        // cookie的作用域 整个网站
        cookie.setPath("/");
        cookie.setDomain(cookieDomain);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        return new Result<>(true, StatusCode.OK, "登录成功", authToken.getJti());
    }


    /***
     * 跳转到登录页面
     * @return main_login.html
     */
    @RequestMapping("/toLogin")
    public String toLogin(@RequestParam(value = "from", required = false) String from, Model model){
        model.addAttribute("from", from);
        return "main_login";
    }
}

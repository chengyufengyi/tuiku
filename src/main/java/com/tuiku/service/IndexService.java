package com.tuiku.service;

import com.tuiku.dao.Blog;
import com.tuiku.dao.BlogDao;
import com.tuiku.util.AuthenticationUtil;
import com.tuiku.util.CookieUtil;
import com.tuiku.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IndexService - 首页类
 */
@Controller
@RequestMapping
public class IndexService {

    // 静态数据
    private static int INDEXSIZE = 10;
    // issue #0: 不能用静态数据存储index
    //private static int index = 0;

    // blogDao操作类
    @Autowired
    private BlogDao blogDao;

    @RequestMapping("/index")
    public String index(HttpServletRequest request, Map<String, Object> map) {
        List<Blog> blogs = new ArrayList<Blog>();
        int        index = 0;

        // 登录检测
        AuthenticationUtil.hasCookieAndSession(request, map);

        // 读取blog表
        blogs = blogDao.getAll();
        int blogSize = blogs.size();

        // 检测当前index数值
        String indexStr = request.getParameter("index");
        if (indexStr != null && !indexStr.equals("")) {
            if (indexStr.equals("prev")) {
                index--;
            }
            else if (indexStr.equals("next")) {
                index++;
            }
            else {
                index = Integer.valueOf(indexStr);
                index--; // 1表示第一页，表示下标0
            }

            if (index < 0) {
                index = 0;
            }
        }

        int startIndex = INDEXSIZE * index;
        int endIndex = startIndex + INDEXSIZE;
        if (startIndex > blogSize) {
            startIndex = blogSize;
        }
        if (endIndex > blogSize) {
            endIndex = blogSize;
        }

        map.put("blogs", blogs.subList(startIndex, endIndex));
        return ("index");
    }

    @RequestMapping("/")
    public String index0(HttpServletRequest request, Map<String, Object> map) {
        return index(request, map);
    }

    /**
     * 登录服务
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, Map<String, Object> map) {
        // 登录检测
        AuthenticationUtil.hasCookieAndSession(request, map);

        return "login";
    }

    /**
     * 注销服务
     */
    @RequestMapping("/logout")
    public String logout(@RequestParam(value="userId")String userId, HttpServletRequest request,
                         HttpServletResponse response, Map<String, Object> map) {

        CookieUtil.delUserCookie(response);
        SessionUtil.delUserSession(request, userId);
        map.put("info", "注销成功，2秒后跳转到主页");
        response.addHeader("refresh", "2;url=" + request.getContextPath() + "/index");
        return "info";
    }

    /**
     * 注册服务
     */
    @RequestMapping("/register")
    public String register(HttpServletRequest request, Map<String, Object> map) {
        // 登录检测
        AuthenticationUtil.hasCookieAndSession(request, map);

        return "register";
    }

    /**
     * 写博客服务
     */
    @RequestMapping("/post")
    public String post(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) {
        int userId = CookieUtil.getUserCookie(request);
        if (userId <= 0) {
            // 用户还未登录
            map.put("info", "你好，你还未登录，3秒中后跳转到登录页面");
            response.addHeader("refresh", "3;url=" + request.getContextPath() + "/login");
            return "info";
        }

        // 登录检测
        AuthenticationUtil.hasCookieAndSession(request, map);

        return "post";
    }

}

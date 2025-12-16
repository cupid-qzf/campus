package com.shanzhu.st.controller;

import com.shanzhu.st.entity.User;
import com.shanzhu.st.enums.ErrorMsg;
import com.shanzhu.st.service.UserService;
import com.shanzhu.st.vo.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * 用户相关控制层
 * 处理用户注册、登录、退出、信息管理等操作的API请求
 *
 * @author: ShanZhu
 * @date: 2024-01-05
 */
@CrossOrigin  // 允许跨域请求
@RestController  // 声明为REST风格控制器
@RequestMapping("user")  // 基础请求路径
public class UserController {

    @Resource  // 自动注入用户服务层
    private UserService userService;

    /**
     * 注册账号
     * 处理用户注册请求，设置默认头像和注册时间
     *
     * @param user 用户注册信息（包含账号、密码、昵称等）
     * @return 注册结果，成功返回用户信息，失败返回错误信息
     */
    @PostMapping("sign-in")  // POST请求，路径：/user/sign-in
    public R signIn(@RequestBody User user) {
        // 设置用户注册时间为当前系统时间
        user.setSignInTime(new Timestamp(System.currentTimeMillis()));
        
        // 如果用户未提供头像，设置默认头像
        if (user.getAvatar() == null || "".equals(user.getAvatar())) {
            user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }
        
        // 调用服务层进行注册
        if (userService.userSignIn(user)) {
            return R.success(user);  // 注册成功，返回用户信息
        }
        return R.fail(ErrorMsg.REGISTER_ERROR);  // 注册失败，返回错误信息
    }

    /**
     * 用户登录
     * 处理用户登录请求，验证账号密码并设置登录状态
     *
     * @param accountNumber 账号
     * @param userPassword  密码
     * @param response      HttpServletResponse对象，用于设置Cookie
     * @return 登录结果，成功返回用户信息并设置Cookie，失败返回错误信息
     */
    @RequestMapping("login")  // 支持GET/POST请求，路径：/user/login
    public R login(
            @RequestParam("accountNumber") @NotEmpty @NotNull String accountNumber,  // 账号参数，不能为空
            @RequestParam("userPassword") @NotEmpty @NotNull String userPassword,  // 密码参数，不能为空
            HttpServletResponse response
    ) {
        // 调用服务层进行登录验证
        User user = userService.userLogin(accountNumber, userPassword);
        
        // 验证失败
        if (null == user) {
            return R.fail(ErrorMsg.EMAIL_LOGIN_ERROR);
        }
        
        // 账号被封禁
        if (user.getUserStatus() != null && user.getUserStatus().equals((byte) 1)) {
            return R.fail(ErrorMsg.ACCOUNT_Ban);
        }
        
        // 设置登录Cookie
        Cookie cookie = new Cookie("shUserId", String.valueOf(user.getId()));
        cookie.setPath("/");  // Cookie作用域
        cookie.setHttpOnly(false);  // 允许前端访问
        response.addCookie(cookie);  // 添加Cookie到响应
        
        return R.success(user);  // 登录成功，返回用户信息
    }

    /**
     * 退出登录
     * 清除用户登录状态，使Cookie失效
     *
     * @param shUserId 用户id（从Cookie获取）
     * @param response HttpServletResponse对象，用于操作Cookie
     * @return 退出结果
     */
    @RequestMapping("logout")  // 支持GET/POST请求，路径：/user/logout
    public R logout(
            @CookieValue("shUserId")
            @NotNull(message = "登录异常 请重新登录")
            @NotEmpty(message = "登录异常 请重新登录") String shUserId, HttpServletResponse response
    ) {
        // 创建与登录Cookie同名的Cookie，设置最大存活时间为0使其立即失效
        Cookie cookie = new Cookie("shUserId", shUserId);
        cookie.setMaxAge(0);  // 设置Cookie立即失效
        cookie.setPath("/");  // Cookie作用域
        cookie.setHttpOnly(true);  // 增强安全性
        response.addCookie(cookie);  // 发送Cookie到客户端
        
        return R.success();  // 退出成功
    }

    /**
     * 获取用户信息
     * 根据用户ID获取用户详细信息
     *
     * @param id 用户ID（从Cookie获取）
     * @return 用户信息
     */
    @GetMapping("info")  // GET请求，路径：/user/info
    public R getOneUser(
            @CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
            @NotEmpty(message = "登录异常 请重新登录")
            String id
    ) {
        // 调用服务层获取用户信息
        return R.success(userService.getUser(Long.valueOf(id)));
    }

    /**
     * 修改用户公开信息
     * 更新用户的公开信息，如昵称、头像、个人简介等
     *
     * @param id   用户id（从Cookie获取）
     * @param user 用户信息（包含需要更新的字段）
     * @return 修改结果
     */
    @PostMapping("/info")  // POST请求，路径：/user/info
    public R updateUserPublicInfo(@CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录")
                                  String id, @RequestBody User user) {
        // 设置用户ID，确保只能修改自己的信息
        user.setId(Long.valueOf(id));
        
        // 调用服务层更新用户信息
        if (userService.updateUserInfo(user)) {
            return R.success();  // 更新成功
        }
        return R.fail(ErrorMsg.SYSTEM_ERROR);  // 更新失败
    }

    /**
     * 修改密码
     * 验证旧密码后更新为新密码
     *
     * @param id          用户id（从Cookie获取）
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @GetMapping("/password")  // GET请求，路径：/user/password
    public R updateUserPassword(
            @CookieValue("shUserId") @NotNull(message = "登录异常 请重新登录")
            @NotEmpty(message = "登录异常 请重新登录") String id,
            @RequestParam("oldPassword") @NotEmpty @NotNull String oldPassword,  // 旧密码参数，不能为空
            @RequestParam("newPassword") @NotEmpty @NotNull String newPassword  // 新密码参数，不能为空
    ) {
        // 调用服务层修改密码
        if (userService.updatePassword(newPassword, oldPassword, Long.valueOf(id))) {
            return R.success();  // 修改成功
        }
        return R.fail(ErrorMsg.PASSWORD_RESET_ERROR);  // 修改失败
    }
}

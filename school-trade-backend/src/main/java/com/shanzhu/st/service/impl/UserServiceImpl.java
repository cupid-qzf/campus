package com.shanzhu.st.service.impl;

import com.shanzhu.st.entity.User;
import com.shanzhu.st.mapper.UserMapper;
import com.shanzhu.st.service.UserService;
import com.shanzhu.st.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户服务实现类
 * 实现UserService接口，处理用户相关的业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper; // 用户数据访问接口

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户实体对象
     */
    public User getUser(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    /**
     * 用户登录验证
     * @param accountNumber 账号
     * @param userPassword 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    public User userLogin(String accountNumber, String userPassword) {
        return userMapper.userLogin(accountNumber, userPassword);
    }

    /**
     * 用户注册
     * @param user 用户注册信息
     * @return 注册成功返回true，失败返回false
     */
    public boolean userSignIn(User user) {
        return userMapper.insert(user) == 1;
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateUserInfo(User user) {
        return userMapper.updateByPrimaryKeySelective(user) == 1;
    }

    /**
     * 更新用户密码
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     * @param id 用户ID
     * @return 更新成功返回true，失败返回false
     */
    public boolean updatePassword(String newPassword, String oldPassword, Long id) {
        return userMapper.updatePassword(newPassword, oldPassword, id) == 1;
    }

    /**
     * 根据用户状态获取用户列表（分页）
     * @param status 用户状态：0-正常，1-封禁
     * @param page 当前页码
     * @param nums 每页数量
     * @return 分页用户列表
     */
    public PageVo<User> getUserByStatus(int status, int page, int nums) {
        List<User> list;
        int count = 0;
        if (status == 0) {
            count = userMapper.countNormalUser(); // 获取正常用户总数
            list = userMapper.getNormalUser((page - 1) * nums, nums); // 获取正常用户列表
        } else {
            count = userMapper.countBanUser(); // 获取封禁用户总数
            list = userMapper.getBanUser((page - 1) * nums, nums); // 获取封禁用户列表
        }
        return new PageVo<>(list, count);
    }

}

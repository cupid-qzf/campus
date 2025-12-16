package com.shanzhu.st.service.impl;

import com.shanzhu.st.entity.IdleItem;
import com.shanzhu.st.entity.User;
import com.shanzhu.st.mapper.IdleItemMapper;
import com.shanzhu.st.mapper.UserMapper;
import com.shanzhu.st.service.IdleItemService;
import com.shanzhu.st.vo.PageVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 闲置物品服务实现类
 * 实现IdleItemService接口，处理闲置物品相关的业务逻辑
 */
@Service
public class IdleItemServiceImpl implements IdleItemService {

    @Resource
    private IdleItemMapper idleItemMapper; // 闲置物品数据访问接口

    @Resource
    private UserMapper userMapper; // 用户数据访问接口，用于关联查询

    /**
     * 添加闲置物品
     * @param idleItem 闲置物品信息
     * @return 添加成功返回true，失败返回false
     */
    public boolean addIdleItem(IdleItem idleItem) {
        return idleItemMapper.insert(idleItem) == 1;
    }

    /**
     * 根据ID获取闲置物品详情
     * @param id 闲置物品ID
     * @return 闲置物品实体对象，包含用户信息
     */
    public IdleItem getIdleItem(Long id) {
        IdleItem idleItem = idleItemMapper.selectByPrimaryKey(id);
        if (idleItem != null) {
            // 关联查询用户信息
            idleItem.setUser(userMapper.selectByPrimaryKey(idleItem.getUserId()));
        }
        return idleItem;
    }

    /**
     * 根据用户ID获取所有闲置物品
     * @param userId 用户ID
     * @return 闲置物品列表
     */
    public List<IdleItem> getAllIdelItem(Long userId) {
        return idleItemMapper.getAllIdleItem(userId);
    }

    /**
     * 根据关键词搜索闲置物品（分页）
     * @param findValue 搜索关键词
     * @param page 当前页码
     * @param nums 每页数量
     * @return 分页闲置物品列表，包含用户信息
     */
    public PageVo<IdleItem> findIdleItem(String findValue, int page, int nums) {
        List<IdleItem> list = idleItemMapper.findIdleItem(findValue, (page - 1) * nums, nums);
        if (list.size() > 0) {
            // 批量获取用户ID
            List<Long> idList = new ArrayList<>();
            for (IdleItem i : list) {
                idList.add(i.getUserId());
            }
            // 批量查询用户信息
            List<User> userList = userMapper.findUserByList(idList);
            // 构建用户ID到用户对象的映射
            Map<Long, User> map = new HashMap<>();
            for (User user : userList) {
                map.put(user.getId(), user);
            }
            // 为闲置物品设置用户信息
            for (IdleItem i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }
        // 获取总记录数
        int count = idleItemMapper.countIdleItem(findValue);
        return new PageVo<>(list, count);
    }

    /**
     * 根据分类标签搜索闲置物品（分页）
     * @param idleLabel 分类标签：0-全部，1-数码，2-家电，3-户外，4-图书，5-其他
     * @param page 当前页码
     * @param nums 每页数量
     * @return 分页闲置物品列表，包含用户信息
     */
    public PageVo<IdleItem> findIdleItemByLable(int idleLabel, int page, int nums) {
        List<IdleItem> list = idleItemMapper.findIdleItemByLable(idleLabel, (page - 1) * nums, nums);
        if (list.size() > 0) {
            // 批量获取用户ID
            List<Long> idList = new ArrayList<>();
            for (IdleItem i : list) {
                idList.add(i.getUserId());
            }
            // 批量查询用户信息
            List<User> userList = userMapper.findUserByList(idList);
            // 构建用户ID到用户对象的映射
            Map<Long, User> map = new HashMap<>();
            for (User user : userList) {
                map.put(user.getId(), user);
            }
            // 为闲置物品设置用户信息
            for (IdleItem i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }
        // 获取总记录数
        int count = idleItemMapper.countIdleItemByLable(idleLabel);
        return new PageVo<>(list, count);
    }

    /**
     * 更新闲置物品信息
     * @param idleItem 闲置物品信息
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateIdleItem(IdleItem idleItem) {
        return idleItemMapper.updateByPrimaryKeySelective(idleItem) == 1;
    }

    /**
     * 管理员获取闲置物品列表（分页）
     * @param status 物品状态：0-上线，1-下架
     * @param page 当前页码
     * @param nums 每页数量
     * @return 分页闲置物品列表，包含用户信息
     */
    public PageVo<IdleItem> adminGetIdleList(int status, int page, int nums) {
        List<IdleItem> list = idleItemMapper.getIdleItemByStatus(status, (page - 1) * nums, nums);
        if (list.size() > 0) {
            // 批量获取用户ID
            List<Long> idList = new ArrayList<>();
            for (IdleItem i : list) {
                idList.add(i.getUserId());
            }
            // 批量查询用户信息
            List<User> userList = userMapper.findUserByList(idList);
            // 构建用户ID到用户对象的映射
            Map<Long, User> map = new HashMap<>();
            for (User user : userList) {
                map.put(user.getId(), user);
            }
            // 为闲置物品设置用户信息
            for (IdleItem i : list) {
                i.setUser(map.get(i.getUserId()));
            }
        }
        int count = idleItemMapper.countIdleItemByStatus(status);
        return new PageVo<>(list, count);
    }

}

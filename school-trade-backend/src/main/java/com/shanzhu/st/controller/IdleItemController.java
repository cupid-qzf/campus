package com.shanzhu.st.controller;

import com.shanzhu.st.entity.IdleItem;
import com.shanzhu.st.enums.ErrorMsg;
import com.shanzhu.st.service.IdleItemService;
import com.shanzhu.st.vo.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 闲置物品控制器
 * 处理闲置物品相关的API请求，包括添加、查询、更新等操作
 */
@CrossOrigin  // 允许跨域请求
@RestController  // 声明为REST风格控制器
@RequestMapping("idle")  // 基础请求路径
public class IdleItemController {

    @Resource  // 自动注入闲置物品服务层
    private IdleItemService idleItemService;

    /**
     * 添加闲置商品
     * 处理用户发布闲置物品的请求
     *
     * @param shUserId 用户id（从Cookie获取）
     * @param idleItem 闲置物品信息
     * @return 添加结果，成功返回物品信息，失败返回错误信息
     */
    @PostMapping("add")  // POST请求，路径：/idle/add
    public R addIdleItem(
            @CookieValue("shUserId")
            @NotNull(message = "登录异常 请重新登录")
            @NotEmpty(message = "登录异常 请重新登录") String shUserId,
            @RequestBody IdleItem idleItem
    ) {
        // 设置物品所属用户ID
        idleItem.setUserId(Long.valueOf(shUserId));
        // 设置物品状态为已上架（1表示上架）
        idleItem.setIdleStatus((byte) 1);
        // 设置发布时间为当前系统时间
        idleItem.setReleaseTime(new Date());
        
        // 调用服务层添加闲置物品
        if (idleItemService.addIdleItem(idleItem)) {
            return R.success(idleItem);  // 添加成功，返回物品信息
        }
        return R.fail(ErrorMsg.SYSTEM_ERROR);  // 添加失败，返回错误信息
    }

    /**
     * 获取闲置物品详情
     * 根据ID查询单个闲置物品的详细信息
     *
     * @param id 闲置物品ID
     * @return 闲置物品信息
     */
    @GetMapping("info")  // GET请求，路径：/idle/info
    public R getIdleItem(@RequestParam Long id) {
        return R.success(idleItemService.getIdleItem(id));
    }

    /**
     * 查询用户所有闲置物品
     * 获取当前登录用户发布的所有闲置物品
     *
     * @param shUserId 用户id（从Cookie获取）
     * @return 用户的所有闲置物品信息
     */
    @GetMapping("all")  // GET请求，路径：/idle/all
    public R getAllIdleItem(
            @CookieValue("shUserId")
            @NotNull(message = "登录异常 请重新登录")
            @NotEmpty(message = "登录异常 请重新登录") String shUserId
    ) {
        return R.success(idleItemService.getAllIdelItem(Long.valueOf(shUserId)));
    }

    /**
     * 搜索闲置物品
     * 根据关键词搜索闲置物品，支持分页
     *
     * @param findValue 搜索关键词（可选）
     * @param page      当前页码（可选，默认1）
     * @param nums      每页记录数（可选，默认8）
     * @return 搜索结果
     */
    @GetMapping("find")  // GET请求，路径：/idle/find
    public R findIdleItem(
            @RequestParam(value = "findValue", required = false) String findValue,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "nums", required = false) Integer nums
    ) {
        // 如果搜索关键词为空，设置为空字符串
        if (null == findValue) {
            findValue = "";
        }
        
        // 设置默认页码和每页记录数
        int p = 1;
        int n = 8;
        
        // 如果页码参数有效，使用传入的页码
        if (null != page) {
            p = page > 0 ? page : 1;
        }
        
        // 如果每页记录数参数有效，使用传入的值
        if (null != nums) {
            n = nums > 0 ? nums : 8;
        }
        
        // 调用服务层进行搜索
        return R.success(idleItemService.findIdleItem(findValue, p, n));
    }

    /**
     * 按标签查询闲置物品
     * 根据物品分类标签查询闲置物品，支持分页
     *
     * @param idleLabel 物品标签（必填）
     * @param page      当前页码（可选，默认1）
     * @param nums      每页记录数（可选，默认8）
     * @return 查询结果
     */
    @GetMapping("lable")  // GET请求，路径：/idle/lable
    public R findIdleItemByLable(
            @RequestParam(value = "idleLabel", required = true) Integer idleLabel,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "nums", required = false) Integer nums
    ) {
        // 设置默认页码和每页记录数
        int p = 1;
        int n = 8;
        
        // 如果页码参数有效，使用传入的页码
        if (null != page) {
            p = page > 0 ? page : 1;
        }
        
        // 如果每页记录数参数有效，使用传入的值
        if (null != nums) {
            n = nums > 0 ? nums : 8;
        }
        
        // 调用服务层按标签查询
        return R.success(idleItemService.findIdleItemByLable(idleLabel, p, n));
    }

    /**
     * 更新闲置物品信息
     * 修改闲置物品的详细信息
     *
     * @param shUserId 用户id（从Cookie获取）
     * @param idleItem 闲置物品信息
     * @return 更新结果，成功返回成功信息，失败返回错误信息
     */
    @PostMapping("update")  // POST请求，路径：/idle/update
    public R updateIdleItem(
            @CookieValue("shUserId")
            @NotNull(message = "登录异常 请重新登录")
            @NotEmpty(message = "登录异常 请重新登录") String shUserId,
            @RequestBody IdleItem idleItem
    ) {
        // 设置物品所属用户ID，确保只能修改自己的物品
        idleItem.setUserId(Long.valueOf(shUserId));
        
        // 调用服务层更新闲置物品
        if (idleItemService.updateIdleItem(idleItem)) {
            return R.success();  // 更新成功
        }
        return R.fail(ErrorMsg.SYSTEM_ERROR);  // 更新失败
    }

}

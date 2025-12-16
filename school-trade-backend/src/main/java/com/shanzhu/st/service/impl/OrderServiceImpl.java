package com.shanzhu.st.service.impl;

import com.shanzhu.st.entity.IdleItem;
import com.shanzhu.st.entity.Order;
import com.shanzhu.st.mapper.IdleItemMapper;
import com.shanzhu.st.mapper.OrderMapper;
import com.shanzhu.st.service.OrderService;
import com.shanzhu.st.utils.OrderTask;
import com.shanzhu.st.utils.OrderTaskHandler;
import com.shanzhu.st.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 订单业务服务层实现
 * 
 * 处理订单的创建、查询、更新等业务逻辑，包含事务管理和并发控制
 */
@Service
public class OrderServiceImpl implements OrderService {

    /**
     * 订单数据访问接口
     */
    @Resource
    private OrderMapper orderMapper;

    /**
     * 闲置物品数据访问接口
     */
    @Resource
    private IdleItemMapper idleItemMapper;

    /**
     * 锁映射表，用于控制并发操作
     * 采用分段锁机制，根据idleId的哈希值分配锁
     */
    private static HashMap<Integer, ReentrantLock> lockMap = new HashMap<>();

    /**
     * 静态初始化锁映射表
     * 创建100个可重入锁，用于并发控制
     */
    static {
        for (int i = 0; i < 100; i++) {
            lockMap.put(i, new ReentrantLock(true)); // true表示公平锁
        }
    }

    /**
     * 新增订单
     * 
     * @param order 订单信息
     * @return 是否创建成功
     */
    public boolean addOrder(Order order) {
        // 检查闲置物品状态是否可购买
        IdleItem idleItemModel = idleItemMapper.selectByPrimaryKey(order.getIdleId());
        if (idleItemModel.getIdleStatus() != 1) { // 1表示可购买
            return false;
        }
        
        // 创建闲置物品更新对象，准备将状态改为已售出
        IdleItem idleItem = new IdleItem();
        idleItem.setId(order.getIdleId());
        idleItem.setUserId(idleItemModel.getUserId());
        idleItem.setIdleStatus((byte) 2); // 2表示已售出

        // 计算锁的key，采用分段锁机制
        int key = (int) (order.getIdleId() % 100);
        ReentrantLock lock = lockMap.get(key);
        boolean flag;
        
        // 使用锁保证并发安全
        try {
            lock.lock();
            flag = addOrderHelp(idleItem, order);
        } finally {
            lock.unlock();
        }
        return flag;
    }


    /**
     * 新增订单的核心方法（事务内）
     * 
     * @param idleItem 闲置物品信息
     * @param order    订单信息
     * @return 是否创建成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean addOrderHelp(IdleItem idleItem, Order order) {
        // 再次检查闲置物品状态，防止并发问题
        IdleItem idleItemModel = idleItemMapper.selectByPrimaryKey(order.getIdleId());
        if (idleItemModel.getIdleStatus() != 1) {
            return false;
        }
        
        // 更新闲置物品状态为已售出
        if (idleItemMapper.updateByPrimaryKeySelective(idleItem) == 1) {
            // 插入订单信息
            if (orderMapper.insert(order) == 1) {
                order.setOrderStatus((byte) 4); // 4表示待支付
                // 半小时未支付则取消订单
                OrderTaskHandler.addOrder(new OrderTask(order, 30 * 60)); // 30分钟 = 30*60秒
                return true;
            } else {
                // 订单插入失败，抛出异常回滚事务
                throw new RuntimeException("订单插入失败");
            }
        }
        return false;
    }

    /**
     * 获取订单信息
     * 
     * @param id 订单id
     * @return 订单信息（包含关联的闲置物品信息）
     */
    public Order getOrder(Long id) {
        // 查询订单信息
        Order order = orderMapper.selectByPrimaryKey(id);
        // 查询关联的闲置物品信息
        order.setIdleItem(idleItemMapper.selectByPrimaryKey(order.getIdleId()));
        return order;
    }

    /**
     * 更新订单信息
     * 
     * @param order 订单信息
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrder(Order order) {
        // 设置不可修改的字段为null
        order.setOrderNumber(null);  // 订单号不可修改
        order.setUserId(null);       // 用户ID不可修改
        order.setIdleId(null);       // 闲置物品ID不可修改
        order.setCreateTime(null);   // 创建时间不可修改
        
        // 处理取消订单的特殊情况
        if (order.getOrderStatus() == 4) { // 4表示取消订单
            // 查询当前订单信息
            Order o = orderMapper.selectByPrimaryKey(order.getId());
            if (o.getOrderStatus() != 0) { // 只有待支付状态可以取消
                return false;
            }
            
            // 查询关联的闲置物品信息
            IdleItem idleItemModel = idleItemMapper.selectByPrimaryKey(o.getIdleId());
            if (idleItemModel.getIdleStatus() == 2) { // 2表示已售出
                // 将闲置物品状态改回可购买
                IdleItem idleItem = new IdleItem();
                idleItem.setId(o.getIdleId());
                idleItem.setUserId(idleItemModel.getUserId());
                idleItem.setIdleStatus((byte) 1); // 1表示可购买
                
                // 更新订单状态
                if (orderMapper.updateByPrimaryKeySelective(order) == 1) {
                    // 更新闲置物品状态
                    if (idleItemMapper.updateByPrimaryKeySelective(idleItem) == 1) {
                        return true;
                    } else {
                        throw new RuntimeException("闲置物品状态更新失败");
                    }
                }
                return false;
            } else {
                // 直接更新订单状态
                if (orderMapper.updateByPrimaryKeySelective(order) == 1) {
                    return true;
                } else {
                    throw new RuntimeException("订单状态更新失败");
                }
            }
        }
        
        // 更新其他订单信息
        return orderMapper.updateByPrimaryKeySelective(order) == 1;
    }

    /**
     * 获取某个用户买到的闲置的订单列表
     * 
     * @param userId 用户id
     * @return 订单列表（包含关联的闲置物品信息）
     */
    public List<Order> getMyOrder(Long userId) {
        // 查询用户的订单列表
        List<Order> list = orderMapper.getMyOrder(userId);
        
        // 批量查询关联的闲置物品信息
        if (list.size() > 0) {
            List<Long> idleIdList = new ArrayList<>();
            for (Order i : list) {
                idleIdList.add(i.getIdleId());
            }
            
            // 一次性查询所有闲置物品
            List<IdleItem> idleItemList = idleItemMapper.findIdleByList(idleIdList);
            
            // 构建闲置物品ID到对象的映射
            Map<Long, IdleItem> map = new HashMap<>();
            for (IdleItem idle : idleItemList) {
                map.put(idle.getId(), idle);
            }
            
            // 关联闲置物品信息到订单
            for (Order i : list) {
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        return list;
    }

    /**
     * 获取某个用户卖出的闲置的订单信息
     * 
     * @param userId 用户id
     * @return 出售订单列表（包含关联的闲置物品信息）
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Order> getMySoldIdle(Long userId) {
        // 查询用户发布的所有闲置物品
        List<IdleItem> list = idleItemMapper.getAllIdleItem(userId);
        List<Order> orderList = null;
        
        // 批量查询关联的订单信息
        if (list.size() > 0) {
            List<Long> idleIdList = new ArrayList<>();
            for (IdleItem i : list) {
                idleIdList.add(i.getId());
            }
            
            // 一次性查询所有相关订单
            orderList = orderMapper.findOrderByIdleIdList(idleIdList);
            
            // 构建闲置物品ID到对象的映射
            Map<Long, IdleItem> map = new HashMap<>();
            for (IdleItem idle : list) {
                map.put(idle.getId(), idle);
            }
            
            // 关联闲置物品信息到订单
            for (Order o : orderList) {
                o.setIdleItem(map.get(o.getIdleId()));
            }
        }
        return orderList;
    }

    /**
     * 获取所有订单（分页）
     * 
     * @param page 页码
     * @param nums 每页数量
     * @return 订单分页信息
     */
    public PageVo<Order> getAllOrder(int page, int nums) {
        // 查询分页订单列表
        List<Order> list = orderMapper.getAllOrder((page - 1) * nums, nums);
        
        // 批量查询关联的闲置物品信息
        if (list.size() > 0) {
            List<Long> idleIdList = new ArrayList<>();
            for (Order i : list) {
                idleIdList.add(i.getIdleId());
            }
            
            // 一次性查询所有闲置物品
            List<IdleItem> idleItemList = idleItemMapper.findIdleByList(idleIdList);
            
            // 构建闲置物品ID到对象的映射
            Map<Long, IdleItem> map = new HashMap<>();
            for (IdleItem idle : idleItemList) {
                map.put(idle.getId(), idle);
            }
            
            // 关联闲置物品信息到订单
            for (Order i : list) {
                i.setIdleItem(map.get(i.getIdleId()));
            }
        }
        
        // 查询订单总数
        int count = orderMapper.countAllOrder();
        
        // 构建并返回分页结果
        return new PageVo<>(list, count);
    }

    /**
     * 删除订单
     * 
     * @param id 订单id
     * @return 是否删除成功
     */
    public boolean deleteOrder(long id) {
        return orderMapper.deleteByPrimaryKey(id) == 1;
    }
}

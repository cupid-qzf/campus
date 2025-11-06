import request from '@/utils/request';

// 获取商品列表（分页+搜索）
export function getGoodsList(params) {
    return request({
        url: '/goods/list',
        method: 'GET',
        params
    });
}

// 获取商品详情
export function getGoodsDetail(id) {
    return request({
        url: `/goods/${id}`,
        method: 'GET'
    });
}
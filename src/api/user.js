import request from '@/utils/request';

// 用户注册
export function userRegister(data) {
    return request({
        url: '/user/register',
        method: 'POST',
        data
    });
}

// 用户登录
export function userLogin(data) {
    return request({
        url: '/user/login',
        method: 'POST',
        data
    });
}

// 获取当前用户信息
export function getUserInfo() {
    return request({
        url: '/user/info',
        method: 'GET'
    });
}
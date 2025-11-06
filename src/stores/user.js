import { defineStore } from 'pinia';

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '', // Token存储在本地缓存
        userInfo: JSON.parse(localStorage.getItem('userInfo')) || {} // 用户信息
    }),
    actions: {
        // 设置Token
        setToken(token) {
            this.token = token;
            localStorage.setItem('token', token); // 持久化到本地存储
        },
        // 设置用户信息
        setUserInfo(userInfo) {
            this.userInfo = userInfo;
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
        },
        // 退出登录
        logout() {
            this.token = '';
            this.userInfo = {};
            localStorage.clear(); // 清空本地缓存
        }
    }
});
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';
import router from '@/router';

// 创建axios实例
const service = axios.create({
    baseURL: 'http://localhost:8080', // 后端接口地址（必须和后端端口一致）
    timeout: 5000 // 请求超时时间
});

// 请求拦截器：添加Token
service.interceptors.request.use(
    (config) => {
        const userStore = useUserStore();
        if (userStore.token) {
            config.headers['Authorization'] = `Bearer ${userStore.token}`; // JWT Token格式
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// 响应拦截器：统一处理结果
service.interceptors.response.use(
    (response) => {
        const res = response.data;
        // 假设后端返回格式：{ code: 200, data: {}, message: '' }
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败');
            return Promise.reject(res);
        }
        return res;
    },
    (error) => {
        ElMessage.error(error.message || '服务器错误');
        // Token过期，跳登录页
        if (error.response?.status === 401) {
            const userStore = useUserStore();
            userStore.logout();
            router.push('/login');
        }
        return Promise.reject(error);
    }
);

export default service;
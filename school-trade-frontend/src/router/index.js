// 导入Vue框架
import Vue from 'vue';
// 导入Vue Router路由管理工具
import Router from 'vue-router';

// 重写Router的replace方法，解决导航重复时的错误
const originalReplace = Router.prototype.replace;
Router.prototype.replace = function replace(location) {
    return originalReplace.call(this, location).catch(err => err);
};

// 重写Router的push方法，解决导航重复时的错误
const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
};

// 注册Vue Router插件
Vue.use(Router);

// 导出Router实例，配置应用的路由规则
export default new Router({
    routes: [
        // 默认路由，重定向到首页
        {
            path: '/',
            redirect: '/index'
        },
        // 首页路由
        {
            path: '/index',
            component: () => import('../components/page/index.vue'), // 懒加载首页组件
            meta: { title: '二手物品交易平台' } // 页面标题
        },
        // 搜索页面路由
        {
            path: '/search',
            component: () => import('../components/page/search.vue'), // 懒加载搜索组件
            meta: { title: '闲置二手物品 | 二手物品交易平台' }
        },
        // 个人中心路由
        {
            path: '/me',
            component: () => import('../components/page/me.vue'), // 懒加载个人中心组件
            meta: { title: '个人中心 | 二手物品交易平台' }
        },
        // 消息页面路由
        {
            path: '/message',
            component: () => import('../components/page/message.vue'), // 懒加载消息组件
            meta: { title: '消息 | 二手物品交易平台' }
        },
        // 发布二手物品路由
        {
            path: '/release',
            component: () => import('../components/page/release.vue'), // 懒加载发布组件
            meta: { title: '发布二手物品 | 二手物品交易平台' }
        },
        // 闲置物品详情路由
        {
            path: '/details',
            component: () => import('../components/page/idle-details.vue'), // 懒加载详情组件
            meta: { title: '二手物品详情 | 二手物品交易平台' }
        },
        // 订单详情路由
        {
            path: '/order',
            component: () => import('../components/page/order.vue'), // 懒加载订单组件
            meta: { title: '订单详情 | 二手物品交易平台' }
        },
        // 登录页面路由
        {
            path: '/login',
            component: () => import('../components/page/login.vue'), // 懒加载登录组件
            meta: { title: '登录 | 二手物品交易平台' }
        },
        // 注册页面路由
        {
            path: '/sign-in',
            component: () => import('../components/page/sign-in.vue'), // 懒加载注册组件
            meta: { title: '注册 | 二手物品交易平台' }
        },
        // 后台登录路由
        {
            path: '/login-admin',
            component: () => import('../components/page/login-admin.vue'), // 懒加载后台登录组件
            meta: { title: '后台登陆' }
        },
        // 后台管理路由
        {
            path: '/platform-admin',
            component: () => import('../components/page/platform-admin.vue'), // 懒加载后台管理组件
            meta: { title: '后台管理' }
        },
        // 404路由，重定向到首页
        {
            path: '*',
            redirect: '/' 
        }
    ]
});

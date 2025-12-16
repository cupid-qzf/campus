// 导入Vue框架
import Vue from 'vue';
// 导入根组件
import App from './App.vue';
// 导入路由配置
import router from './router';
// 导入Element UI组件库
import ElementUI from 'element-ui';
// 导入jQuery
import $ from 'jquery'
// 导入Element UI样式
import 'element-ui/lib/theme-chalk/index.css';
// 导入Babel Polyfill，用于兼容旧浏览器
import 'babel-polyfill';

// 导入API配置
import api from './api/index.js';
// 将API挂载到Vue原型，全局可用
Vue.prototype.$api = api;

// 定义全局数据
let globalData={
    userInfo:{
        nickname:''  // 用户昵称
    }
};

// 定义全局状态
let sta={
    isLogin:false,  // 是否登录
    adminName:''     // 管理员名称
};

// 将全局状态挂载到Vue原型
Vue.prototype.$sta = sta;

// 将全局数据挂载到Vue原型
Vue.prototype.$globalData=globalData;

// 关闭Vue生产提示
Vue.config.productionTip = false;

// 使用Element UI组件库，设置默认尺寸为medium
Vue.use(ElementUI, {
    size: 'medium'
});


// 路由守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    document.title = `${to.meta.title}`;
    
    // 获取当前用户昵称
    const nickname = Vue.prototype.$globalData.userInfo.nickname;
    
    // 判断是否需要登录访问的页面
    if (!nickname
        &&(to.path === '/me'
        || to.path === '/message'
        || to.path === '/release'
        || to.path === '/order')) {
        // 调用API获取用户信息
        api.getUserInfo().then(res=>{
           console.log('getUserInfo:',res);
           if(res.status_code!==1){
               // 未登录，跳转到登录页面
               next('/login');
           }else {
               // 已登录，更新全局用户信息
               res.data.signInTime=res.data.signInTime.substring(0,10);
               Vue.prototype.$globalData.userInfo=res.data;
               next();
           }
        }).catch(e=>{
            // 请求失败，跳转到登录页面
            next('/login');
        });

    }else{
        // 已登录或无需登录，直接进入
        next();
    }
});

// 创建Vue应用实例
new Vue({
    router,  // 注入路由
    render: h => h(App)  // 渲染根组件
}).$mount('#app');  // 挂载到DOM元素

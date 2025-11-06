import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

// 创建App实例
const app = createApp(App);

// 挂载依赖
app.use(createPinia()); // 状态管理
app.use(router); // 路由
app.use(ElementPlus); // UI组件库

// 全局注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

// 挂载到页面
app.mount('#app');
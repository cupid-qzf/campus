import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '@/stores/user';

// 导入页面组件
import Login from '@/views/Login/index.vue';
import Register from '@/views/Register/index.vue';
import GoodsList from '@/views/Goods/GoodsList.vue';
import GoodsDetail from '@/views/Goods/GoodsDetail.vue';
import UserCenter from '@/views/User/UserCenter.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/goods' }, // 根路径跳商品列表
    { path: '/login', component: Login, meta: { requiresAuth: false } },
    { path: '/register', component: Register, meta: { requiresAuth: false } },
    { path: '/goods', component: GoodsList, meta: { requiresAuth: true } },
    { path: '/goods/:id', component: GoodsDetail, meta: { requiresAuth: true } },
    { path: '/user/center', component: UserCenter, meta: { requiresAuth: true } },
    { path: '/:pathMatch(.*)*', redirect: '/login' } // 404跳登录页
  ]
});

// 路由守卫：未登录拦截
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login'); // 未登录跳登录页
  } else {
    next(); // 已登录/无需登录，放行
  }
});

export default router;
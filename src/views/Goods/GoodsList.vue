<template>
  <div style="height: 100%; display: flex; flex-direction: column;">
    <!-- 顶部导航 -->
    <div style="height: 60px; line-height: 60px; background: #fff; box-shadow: 0 2px 8px #eee; padding: 0 20px;">
      <h1 style="float: left; margin: 0; color: #1989fa; font-size: 20px;">校园二手商品列表</h1>
      <button @click="logout" style="float: right; border: none; background: #ff4d4f; color: #fff; padding: 5px 10px; border-radius: 4px;">退出登录</button>
    </div>

    <!-- 商品列表 -->
    <div style="flex: 1; padding: 20px; overflow-y: auto;">
      <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 20px;">
        <!-- 循环显示测试商品（无需调用API，直接硬编码，确保能看到） -->
        <el-card v-for="(item, index) in goodsList" :key="index" style="cursor: pointer;">
          <img :src="item.img" style="width: 100%; height: 180px; object-fit: cover;" alt="商品图">
          <div style="padding: 10px;">
            <h3 style="font-size: 16px; margin: 0 0 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ item.name }}</h3>
            <p style="color: #ff4d4f; font-size: 18px; margin: 0 0 10px;">¥{{ item.price }}</p>
            <p style="font-size: 12px; color: #666; margin: 0;">发布人：{{ item.publisher }}</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();

// 硬编码测试商品（无需调用后端API，确保页面能显示内容）
const goodsList = ref([
  { name: '罗技G304无线鼠标', price: 89.90, publisher: '小明', img: 'https://picsum.photos/id/96/300/200' },
  { name: '2025考研英语真题', price: 35.00, publisher: '小明', img: 'https://picsum.photos/id/42/300/200' },
  { name: 'Kindle Paperwhite 32G', price: 899.00, publisher: '小红', img: 'https://picsum.photos/id/20/300/200' },
  { name: '九阳迷你电饭煲', price: 129.00, publisher: '小红', img: 'https://picsum.photos/id/28/300/200' },
  { name: '大学物理教材', price: 45.00, publisher: '小红', img: 'https://picsum.photos/id/24/300/200' },
  { name: '宿舍折叠桌', price: 59.00, publisher: '小红', img: 'https://picsum.photos/id/163/300/200' }
]);

// 退出登录
const logout = () => {
  ElMessage.success('退出成功');
  router.push('/login');
};
</script>
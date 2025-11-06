<template>
  <div class="goods-detail-page">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-container">
        <div class="logo" @click="goToGoodsList">校园二手交易平台</div>
        <div class="nav">
          <el-menu :default-active="activeNav" mode="horizontal" background-color="transparent">
            <el-menu-item index="goods" @click="goToGoodsList">商品列表</el-menu-item>
            <el-menu-item index="center" @click="goToUserCenter">个人中心</el-menu-item>
            <el-menu-item index="logout" @click="handleLogout">退出登录</el-menu-item>
          </el-menu>
        </div>
      </div>
    </el-header>

    <!-- 详情内容 -->
    <el-main class="main">
      <div class="detail-container" v-if="goodsDetail">
        <div class="detail-left">
          <el-image
              :src="goodsDetail.image_urls ? goodsDetail.image_urls.split(',')[0] : 'https://picsum.photos/600/400'"
              fit="contain"
              class="goods-big-img"
              preview-teleport="body"
          ></el-image>
        </div>

        <div class="detail-right">
          <h1 class="goods-title">{{ goodsDetail.title }}</h1>
          <div class="goods-price">¥{{ goodsDetail.price.toFixed(2) }}</div>

          <div class="goods-meta">
            <div class="meta-item">
              <span class="label">发布人：</span>
              <span class="value">{{ goodsDetail.sellerNickname || '匿名' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">发布时间：</span>
              <span class="value">{{ formatTime(goodsDetail.create_time) }}</span>
            </div>
            <div class="meta-item">
              <span class="label">商品分类：</span>
              <span class="value">{{ goodsDetail.category || '未分类' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">商品状态：</span>
              <span class="value">
                <el-tag :type="getStatusTagType(goodsDetail.status)">
                  {{ getStatusText(goodsDetail.status) }}
                </el-tag>
              </span>
            </div>
          </div>

          <div class="goods-desc">
            <h3 class="desc-title">商品描述</h3>
            <div class="desc-content">{{ goodsDetail.description || '暂无描述' }}</div>
          </div>

          <div class="operate-btn-group">
            <el-button
                type="primary"
                size="large"
                class="buy-btn"
                :disabled="goodsDetail.status !== 1"
                @click="handleBuy"
            >
              <el-icon><ShoppingCart /></el-icon>
              {{ goodsDetail.status === 1 ? '立即购买' : '已售出' }}
            </el-button>
            <el-button
                type="default"
                size="large"
                class="back-btn"
                @click="goToGoodsList"
            >
              返回列表
            </el-button>
          </div>
        </div>
      </div>

      <!-- 加载中 -->
      <div class="loading" v-if="isLoading">
        <el-loading-spinner size="large"></el-loading-spinner>
        <p>加载中...</p>
      </div>

      <!-- 加载失败 -->
      <div class="load-fail" v-if="!isLoading && !goodsDetail">
        <el-empty description="商品加载失败"></el-empty>
        <el-button type="primary" @click="loadGoodsDetail">重试</el-button>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { getGoodsDetail } from '@/api/goods';
import { ElMessage } from 'element-plus';
import { ShoppingCart } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const goodsDetail = ref(null);
const isLoading = ref(false);
const activeNav = ref('goods');

// 格式化日期
const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间';
  const date = new Date(timestamp);
  return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')} ${String(date.getHours()).padStart(2,'0')}:${String(date.getMinutes()).padStart(2,'0')}:${String(date.getSeconds()).padStart(2,'0')}`;
};

// 商品状态文本（适配新表）
const getStatusText = (status) => {
  const statusMap = { 1: '在售', 2: '已下单', 3: '已成交', 4: '已下架' };
  return statusMap[status] || '未知状态';
};

// 商品状态标签类型
const getStatusTagType = (status) => {
  const typeMap = { 1: 'success', 2: 'warning', 3: 'info', 4: 'danger' };
  return typeMap[status] || 'default';
};

// 加载商品详情
const loadGoodsDetail = async () => {
  try {
    isLoading.value = true;
    const goodsId = route.params.id;
    const res = await getGoodsDetail(goodsId);
    goodsDetail.value = res.data;
  } catch (error) {
    ElMessage.error('商品详情加载失败');
  } finally {
    isLoading.value = false;
  }
};

// 页面挂载时加载
onMounted(() => {
  loadGoodsDetail();
});

// 跳转到商品列表
const goToGoodsList = () => {
  router.push('/goods');
};

// 跳转到个人中心
const goToUserCenter = () => {
  router.push('/user/center');
};

// 退出登录
const handleLogout = () => {
  userStore.logout();
  ElMessage.success('退出成功');
  router.push('/login');
};

// 立即购买（模拟）
const handleBuy = () => {
  ElMessage.success('购买功能已触发，后续可对接订单接口');
};
</script>

<style scoped lang="scss">
.goods-detail-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;

  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    width: 1200px;
    margin: 0 auto;

    .logo {
      font-size: 20px;
      font-weight: 600;
      color: #1989fa;
      cursor: pointer;
    }
  }
}

.main {
  flex: 1;
  width: 1200px;
  margin: 0 auto;
  padding: 30px 0;
  overflow-y: auto;
}

.detail-container {
  display: flex;
  gap: 40px;
}

.detail-left {
  flex: 0 0 600px;
}

.goods-big-img {
  width: 100%;
  height: 400px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
}

.detail-right {
  flex: 1;
  min-width: 300px;
}

.goods-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 16px;
  line-height: 1.5;
}

.goods-price {
  font-size: 32px;
  color: #ff4d4f;
  font-weight: 700;
  margin-bottom: 20px;
}

.goods-meta {
  margin-bottom: 30px;
  padding: 16px;
  background-color: #fafafa;
  border-radius: 8px;

  .meta-item {
    display: flex;
    margin-bottom: 12px;

    .label {
      font-size: 14px;
      color: #666;
      width: 80px;
    }

    .value {
      font-size: 14px;
      color: #333;
    }
  }
}

.goods-desc {
  margin-bottom: 30px;

  .desc-title {
    font-size: 18px;
    font-weight: 500;
    margin-bottom: 12px;
  }

  .desc-content {
    font-size: 14px;
    color: #333;
    line-height: 1.8;
    white-space: pre-line;
  }
}

.operate-btn-group {
  display: flex;
  gap: 16px;
}

.buy-btn {
  flex: 1;
}

.back-btn {
  flex: 0 0 120px;
}

.loading {
  text-align: center;
  padding: 100px 0;
  color: #666;
}

.load-fail {
  text-align: center;
  padding: 100px 0;

  button {
    margin-top: 20px;
  }
}
</style>
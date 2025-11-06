<template>
  <div class="user-center-page">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-container">
        <div class="logo" @click="goToGoodsList">校园二手交易平台</div>
        <div class="nav">
          <el-menu :default-active="activeNav" mode="horizontal" background-color="transparent">
            <el-menu-item index="goods" @click="goToGoodsList">商品列表</el-menu-item>
            <el-menu-item index="center">个人中心</el-menu-item>
            <el-menu-item index="logout" @click="handleLogout">退出登录</el-menu-item>
          </el-menu>
        </div>
      </div>
    </el-header>

    <!-- 个人中心内容 -->
    <el-main class="main">
      <div class="center-container">
        <!-- 左侧用户信息 -->
        <div class="user-info-card">
          <div class="avatar-container">
            <el-avatar :size="120" class="avatar">
              {{ userInfo.nickname ? userInfo.nickname.charAt(0) : '用' }}
            </el-avatar>
          </div>
          <div class="user-base-info">
            <h2 class="nickname">{{ userInfo.nickname || '未设置昵称' }}</h2>
            <p class="username">用户名：{{ userInfo.username }}</p>
            <p class="role">身份：{{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
            <p class="phone">手机号：{{ userInfo.phone || '未绑定' }}</p>
          </div>
        </div>

        <!-- 右侧内容 -->
        <div class="user-content">
          <el-card class="content-card">
            <h3 class="card-title">个人资料</h3>
            <el-form :model="userInfo" label-width="100px" class="profile-form">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled></el-input>
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userInfo.nickname"></el-input>
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone"></el-input>
              </el-form-item>
              <el-form-item label="注册时间">
                <el-input :value="formatTime(userInfo.create_time)" disabled></el-input>
              </el-form-item>
              <el-form-item label="账号状态">
                <el-tag type="success">正常</el-tag>
              </el-form-item>
              <el-form-item>
                <el-button type="primary">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <el-card class="content-card" style="margin-top: 20px;">
            <h3 class="card-title">我的订单</h3>
            <div class="order-list">
              <el-table :data="orderList" border style="width: 100%">
                <el-table-column label="订单号" prop="order_no"></el-table-column>
                <el-table-column label="商品名称" prop="goods_name"></el-table-column>
                <el-table-column label="订单金额" prop="price" :formatter="formatPrice"></el-table-column>
                <el-table-column label="订单状态" prop="status">
                  <template #default="scope">
                    <el-tag :type="getStatusTagType(scope.row.status)">
                      {{ getStatusText(scope.row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="创建时间" prop="create_time" :formatter="formatTime"></el-table-column>
                <el-table-column label="操作">
                  <template #default="scope">
                    <el-button type="text" size="small">查看详情</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-card>
        </div>
      </div>
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { getUserInfo } from '@/api/user';
import { ElMessage } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();

const userInfo = ref({});
const orderList = ref([]);
const activeNav = ref('center');

// 格式化日期
const formatTime = (timestamp) => {
  if (!timestamp) return '未知时间';
  const date = new Date(timestamp);
  return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')} ${String(date.getHours()).padStart(2,'0')}:${String(date.getMinutes()).padStart(2,'0')}:${String(date.getSeconds()).padStart(2,'0')}`;
};

// 格式化价格
const formatPrice = (row, column) => {
  return '¥' + row.price.toFixed(2);
};

// 订单状态文本
const getStatusText = (status) => {
  const statusMap = { 1: '待确认', 2: '已确认', 3: '已完成', 4: '已取消' };
  return statusMap[status] || '未知状态';
};

// 订单状态标签类型
const getStatusTagType = (status) => {
  const typeMap = { 1: 'warning', 2: 'info', 3: 'success', 4: 'danger' };
  return typeMap[status] || 'default';
};

// 加载用户信息和订单
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo();
    userInfo.value = res.data;
    // 加载订单（后续对接后端订单列表接口）
    orderList.value = res.data.orders || [];
  } catch (error) {
    ElMessage.error('用户信息加载失败');
  }
};

// 页面挂载时加载
onMounted(() => {
  loadUserInfo();
});

// 跳转到商品列表
const goToGoodsList = () => {
  router.push('/goods');
};

// 退出登录
const handleLogout = () => {
  userStore.logout();
  ElMessage.success('退出成功');
  router.push('/login');
};
</script>

<style scoped lang="scss">
.user-center-page {
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

.center-container {
  display: flex;
  gap: 30px;
}

.user-info-card {
  flex: 0 0 300px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  text-align: center;
}

.avatar-container {
  margin-bottom: 20px;
}

.avatar {
  background-color: #1989fa;
  font-size: 48px;
  color: #fff;
}

.user-base-info {
  .nickname {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 8px;
  }

  .username, .role, .phone {
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
  }
}

.user-content {
  flex: 1;
}

.content-card {
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;

  .card-title {
    font-size: 18px;
    font-weight: 500;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f0f0f0;
  }
}

.profile-form {
  max-width: 600px;
}

.order-list {
  margin-top: 10px;
}
</style>
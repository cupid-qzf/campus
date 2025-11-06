<template>
  <div class="register-page">
    <el-card class="register-card">
      <h2 class="register-title">校园二手交易平台 - 注册</h2>
      <p class="register-subtitle">填写信息完成注册</p>

      <el-form
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          label-width="80px"
          class="register-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="registerForm.username"
              placeholder="请输入3-20位用户名"
              clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入6-20位密码"
              clearable
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              clearable
          >
            <template #prefix>
              <el-icon><LockFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input
              v-model="registerForm.nickname"
              placeholder="请输入2-10位昵称"
              clearable
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item class="register-btn-group">
          <el-button
              type="primary"
              @click="handleRegister"
              :loading="isRegisterLoading"
              class="register-btn"
          >
            注册
          </el-button>
          <el-button type="text" @click="goToLogin">已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { userRegister } from '@/api/user';
import { ElMessage } from 'element-plus';
import { User, Lock, LockFilled, UserFilled } from '@element-plus/icons-vue';

const router = useRouter();
const registerFormRef = ref(null);
const isRegisterLoading = ref(false);

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: ''
});

const registerRules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 10, message: '昵称长度为 2-10 个字符', trigger: 'blur' }
  ]
});

const goToLogin = () => {
  router.push('/login');
};

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate();
    isRegisterLoading.value = true;
    await userRegister(registerForm.value);
    ElMessage.success('注册成功！即将跳转到登录页');
    setTimeout(() => router.push('/login'), 1500);
  } catch (error) {
    ElMessage.error(error.message || '注册失败，请重试');
  } finally {
    isRegisterLoading.value = false;
  }
};
</script>

<style scoped lang="scss">
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.register-card {
  width: 450px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  font-size: 24px;
  font-weight: 600;
  color: #1989fa;
  margin-bottom: 8px;
}

.register-subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 24px;
}

.register-form {
  margin-top: 20px;
}

.register-btn-group {
  margin-top: 16px;
}

.register-btn {
  width: 100%;
}
</style>
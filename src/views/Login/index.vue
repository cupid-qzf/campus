<template>
  <div style="height: 100%; display: flex; justify-content: center; align-items: center; background: #f5f5f5;">
    <el-card style="width: 400px; padding: 20px;">
      <h2 style="text-align: center; color: #1989fa; margin-bottom: 20px;">校园二手交易平台</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" style="width: 100%;">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const formRef = ref(null);
const form = ref({ username: 'user1', password: '123456' }); // 默认填充测试账号
const rules = ref({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
});

const login = async () => {
  try {
    await formRef.value.validate();
    ElMessage.success('登录成功！');
    router.push('/goods'); // 登录后跳商品列表
  } catch (err) {
    ElMessage.error('登录失败');
  }
};
</script>
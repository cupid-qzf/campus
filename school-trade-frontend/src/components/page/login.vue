<template>
    <!-- 登录页面容器 -->
    <div class="login-container" >
        <!-- 登录卡片 -->
        <el-card class="box-card">
            <!-- 登录主体内容 -->
            <div class="login-body">
                <!-- 标题和Logo -->
                <div class="login-title" @click="toIndex" >
                    <img src="../../assets/logo.png" style="width: 40px;position: relative; top: 13px;right: 6px">
                    <span style='color: #e75c09'>校园二手交易平台</span>
                </div>
                <!-- 登录表单 -->
                <el-form ref="form" :model="userForm">
                    <!-- 账号输入框 -->
                    <el-input placeholder="请输入账号..." v-model="userForm.accountNumber" class="login-input">
                        <template slot="prepend">
                            <div class="el-icon-user-solid"></div>
                        </template>
                    </el-input>
                    <!-- 密码输入框 -->
                    <el-input placeholder="请输入密码..." v-model="userForm.userPassword" class="login-input"
                              @keyup.enter.native="login"  show-password>
                        <template slot="prepend">
                            <div class="el-icon-lock"></div>
                        </template>
                    </el-input>
                    <!-- 登录按钮区域 -->
                    <div class="login-submit" >
                        <el-button type="primary" @click="login">登录</el-button>
                        <el-button type="warning" autocomplete="off" @click="$router.push('/sign-in')" style="margin-left: 20px">注册</el-button>
                        <el-button type="success" autocomplete="off" @click="$router.push('/login-admin')" style="margin-left: 20px">管理员登录</el-button>

                    </div>
                </el-form>
            </div>
        </el-card>
    </div>
</template>

<script>
    /**
     * 登录组件
     * 用户登录页面，处理用户登录逻辑
     */
    export default {
        name: "login",
        data() {
            return {
                // 用户登录表单数据
                userForm: {
                    accountNumber: '',  // 账号
                    userPassword: ''     // 密码
                }
            };
        },

        methods: {
            /**
             * 登录方法
             * 调用登录接口，处理登录结果
             */
            login() {
                this.$api.userLogin({
                    accountNumber: this.userForm.accountNumber,
                    userPassword: this.userForm.userPassword
                }).then(res => {
                    console.log(res);
                    if (res.status_code === 1) {
                        // 格式化登录时间
                        res.data.signInTime=res.data.signInTime.substring(0,10);
                        // 保存用户信息到全局
                        this.$globalData.userInfo = res.data;
                        // 跳转到首页
                        this.$router.replace({path: '/index'});
                    } else {
                        this.$message.error(res.msg);
                    }
                });
            },
            /**
             * 跳转到首页
             */
            toIndex() {
                this.$router.replace({path: '/index'});
            }
        }
    }
</script>

<style scoped>
    /* 登录容器样式 */
    .login-container {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        width: 100%;
        background: url("../../assets/login-back.png") center top / cover no-repeat;
    }

    /* 登录主体样式 */
    .login-body {
        padding: 30px;
        width: 300px;
        height: 100%;
        opacity: 0.9;
    }

    /* 登录标题样式 */
    .login-title {
        padding-bottom: 30px;
        text-align: center;
        font-weight: 600;
        font-size: 20px;
        color: #409EFF;
        cursor: pointer;
    }

    /* 登录输入框样式 */
    .login-input {
        margin-bottom: 20px;
    }

    /* 登录按钮区域样式 */
    .login-submit {
        margin-top: 20px;
        display: flex;
        justify-content: center;
    }

    /* 注册容器样式 */
    .sign-in-container {
        padding: 0 10px;
    }

    /* 注册文本样式 */
    .sign-in-text {
        color: #409EFF;
        font-size: 16px;
        text-decoration: none;
        line-height:28px;
    }
    
    /* 其他提交按钮样式 */
    .other-submit{
        display:flex;
        justify-content: space-between;
        margin-top: 10px;
        margin-left: 0px;
    }
</style>

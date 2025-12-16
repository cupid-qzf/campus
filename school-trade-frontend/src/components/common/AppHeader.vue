<template>
    <!-- 应用头部导航组件 -->
    <div class="header">
        <!-- 头部容器 -->
        <div class="header-container">
            <!-- 应用名称和Logo -->
            <div class="app-name">
                <router-link to="/">
                    <img src="../../assets/logo.png" style="width: 40px;position: relative; top: 13px;right: 6px">
                    <b style="color: #e75c09">校园二手交易平台</b>
                </router-link>
            </div>
            
            <!-- 搜索框 -->
            <div class="search-container">
                <el-input placeholder="搜物品..." v-model="searchValue" @keyup.enter.native="searchIdle">
                    <el-button slot="append" icon="el-icon-search" @click="searchIdle"></el-button>
                </el-input>
            </div>
            
            <!-- 功能按钮区 -->
            <el-button type="primary" icon="el-icon-plus"  @click="toRelease">物品发布</el-button>
            <el-button type="primary" icon="el-icon-chat-dot-round" @click="toMessage">消息</el-button>
            
            <!-- 登录/用户信息区 -->
            <router-link v-if="!isLogin" class="user-name-text" to="/login">登录</router-link>
            <el-dropdown trigger="click" v-else>
                <div style="cursor:pointer;display: flex;align-items: center;">
                    <div style="font-size: 16px;color: #409EFF;padding-right: 5px;">{{nicknameValue?nicknameValue:nickname}}</div>
                    <el-avatar :src="avatarValue?avatarValue:avatar"></el-avatar>
                </div>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item><div @click="toMe">个人中心</div></el-dropdown-item>
                    <el-dropdown-item divided style="color: red;"><div @click="loginOut">退出登录</div></el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>
<script>
    /**
     * 应用头部导航组件
     * 显示应用Logo、搜索框、功能按钮和用户信息，实现页面导航和登录状态管理
     */
    export default {
        name: 'Header',
        // 接收父组件传递的属性
        props: [
            'searchInput', // 搜索框初始值
            'nicknameValue', // 昵称覆盖值
            'avatarValue' // 头像覆盖值
        ],
        data() {
            return {
                searchValue: this.searchInput, // 搜索框输入值
                nickname: '登录', // 用户昵称
                avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', // 用户头像
                isLogin: false // 登录状态
            };
        },
        /**
         * 组件创建时执行
         * 检查用户登录状态，获取用户信息
         */
        created() {
            // 检查全局数据中是否已有用户信息
            if (!this.$globalData.userInfo.nickname) {
                // 调用API获取用户信息
                this.$api.getUserInfo().then(res => {
                    console.log('Header getUserInfo:', res);
                    if (res.status_code === 1) {
                        // 更新用户信息
                        this.nickname = res.data.nickname;
                        this.avatar = res.data.avatar;
                        // 格式化注册时间
                        res.data.signInTime = res.data.signInTime.substring(0, 10);
                        // 保存到全局数据
                        this.$globalData.userInfo = res.data;
                        this.isLogin = true;
                    }
                })
            } else {
                // 使用全局数据中的用户信息
                this.nickname = this.$globalData.userInfo.nickname;
                this.avatar = this.$globalData.userInfo.avatar;
                this.isLogin = true;
            }
        },
        methods: {
            /**
             * 搜索闲置物品
             * 跳转到搜索页面并携带搜索关键词
             */
            searchIdle() {
                if ('/search' !== this.$route.path) {
                    // 如果不在搜索页面，跳转到搜索页面
                    this.$router.push({path: '/search', query: {searchValue: this.searchValue}});
                } else {
                    // 如果已在搜索页面，更新参数并刷新页面
                    this.$router.replace({path: '/search', query: {searchValue: this.searchValue}});
                    this.$router.go(0);
                }
            },
            /**
             * 跳转到个人中心页面
             */
            toMe() {
                if ('/me' !== this.$route.path) {
                    this.$router.push({path: '/me'});
                }
            },
            /**
             * 跳转到消息页面
             */
            toMessage() {
                if ('/message' !== this.$route.path) {
                    this.$router.push({path: '/message'});
                }
            },
            /**
             * 跳转到物品发布页面
             */
            toRelease() {
                if ('/release' !== this.$route.path) {
                    this.$router.push({path: '/release'});
                }
            },
            /**
             * 用户退出登录
             * 调用API退出登录，清除全局用户信息，跳转到首页
             */
            loginOut() {
                this.$api.logout().then(res => {
                    if (res.status_code === 1) {
                        // 清除全局用户信息
                        this.$globalData.userInfo = {};
                        console.log("login out");
                        // 根据当前页面决定刷新或跳转
                        if ('/index' === this.$route.path) {
                            this.$router.go(0);
                        } else {
                            this.$router.push({path: '/index'});
                        }
                    } else {
                        // 退出失败提示
                        this.$message.error('网络或系统异常，退出登录失败！');
                    }
                });
            }
        }
    };
</script>
<style scoped>
    /* 头部容器样式 */
    .header {
        position: fixed; /* 固定在顶部 */
        top: 0;
        left: 0;
        right: 0;
        width: 100%;
        height: 58px;
        background: #ffffff; /* 白色背景 */
        display: flex;
        justify-content: center;
        border-bottom: #eeeeee solid 2px; /* 底部边框 */
        z-index: 1000; /* 高优先级，确保在最上层 */
    }

    /* 头部内容容器 */
    .header-container {
        width: 1000px;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between; /* 两端对齐布局 */
    }

    /* 应用名称样式 */
    .app-name a {
        color: #409EFF;
        font-size: 24px;
        text-decoration: none;
    }

    /* 搜索框容器 */
    .search-container {
        width: 300px;
    }
    
    /* 登录按钮样式 */
    .user-name-text {
        font-size: 16px;
        font-weight: 600;
        color: #409EFF;
        cursor: pointer;
        text-decoration: none;
    }
</style>

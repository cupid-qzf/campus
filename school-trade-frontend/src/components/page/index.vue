<template>
    <div>
        <app-head></app-head> <!-- 头部组件 -->
        <app-body>
            <div style="min-height: 85vh;">
            <!-- 分类标签页 -->
            <el-tabs v-model="labelName" type="card" @tab-click="handleClick">
                <el-tab-pane label="全部" name="0"></el-tab-pane>
                <el-tab-pane label="数码" name="1"></el-tab-pane>
                <el-tab-pane label="家电" name="2"></el-tab-pane>
                <el-tab-pane label="户外" name="3"></el-tab-pane>
                <el-tab-pane label="图书" name="4"></el-tab-pane>
                <el-tab-pane label="其他" name="5"></el-tab-pane>
            </el-tabs>

             <!-- 备用的菜单导航（已注释） -->
             <!--<el-menu  mode="horizontal" @select="handleSelect">
                    <el-menu-item >全部</el-menu-item>
                    <el-submenu ><template slot="title">数码</template></el-submenu>
                    <el-submenu><template slot="title">家电</template></el-submenu>
                    <el-submenu><template slot="title">户外</template></el-submenu>
                    <el-submenu><template slot="title">图书</template></el-submenu>
                    <el-submenu><template slot="title">其他</template></el-submenu>
              </el-menu>-->
              
            <!-- 闲置物品卡片列表 -->
            <div style="margin: 0 20px;">
                <el-row :gutter="30">
                    <!-- 遍历闲置物品列表生成卡片 -->
                    <el-col :span="6" v-for="(idle,index) in idleList">
                        <div class="idle-card" @click="toDetails(idle)">
                            <!-- 物品图片 -->
                            <el-image
                                    style="width: 100%; height: 160px"
                                    :src="idle.imgUrl"
                                    fit="contain">
                                <div slot="error" class="image-slot">
                                    <i class="el-icon-picture-outline">无图</i>
                                </div>
                            </el-image>
                            <!-- 物品标题 -->
                            <div class="idle-title">
                                {{idle.idleName}}
                            </div>
                            <!-- 价格和地点 -->
                            <el-row style="margin: 5px 10px;">
                                <el-col :span="12">
                                    <div class="idle-prive">￥{{idle.idlePrice}}</div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="idle-place">{{idle.idlePlace}}</div>
                                </el-col>
                            </el-row>
                           <!-- 发布时间（已注释）-->
                           <!-- <div class="idle-time">{{idle.timeStr}}</div>-->
                            <!-- 用户信息 -->
                            <div class="user-info">
                                <!-- 用户头像 -->
                                <el-image
                                        style="width: 30px; height: 30px"
                                        :src="idle.user.avatar"
                                        fit="contain">
                                    <div slot="error" class="image-slot">
                                        <i class="el-icon-picture-outline">无图</i>
                                    </div>
                                </el-image>
                                <!-- 用户昵称 -->
                                <div class="user-nickname">{{idle.user.nickname}}</div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <!-- 分页组件 -->
            <div class="fenye">
                <el-pagination
                        background
                        @current-change="handleCurrentChange"
                        :current-page.sync="currentPage"
                        :page-size="8"
                        layout="prev, pager, next, jumper"
                        :total="totalItem">
                </el-pagination>
            </div>
            </div>
            <app-foot></app-foot> <!-- 底部组件 -->
        </app-body>
    </div>
</template>

<script>
    // 导入公共组件
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'

    /**
     * 系统首页组件
     * 展示闲置物品列表，支持分类筛选和分页功能
     */
    export default {
        name: "index",
        // 注册组件
        components: {
            AppHead,
            AppBody,
            AppFoot
        },
        data() {
            return {
                labelName: '0', // 当前分类标签，0-全部，1-数码，2-家电，3-户外，4-图书，5-其他
                idleList: [], // 闲置物品列表
                currentPage: 1, // 当前页码
                totalItem: 1 // 总记录数
            };
        },
        /**
         * 组件创建后调用，初始化加载第一页数据
         */
        created() {
            this.findIdleTiem(1)
        },
        /**
         * 监听路由变化
         * 当路由参数变化时，更新分类和页码并重新加载数据
         */
        watch:{
            $route(to,from){
                this.labelName = to.query.labelName;
                let val = parseInt(to.query.page) ? parseInt(to.query.page) : 1;
                this.currentPage = parseInt(to.query.page) ? parseInt(to.query.page) : 1;
                this.findIdleTiem(val);
            }
        },
        methods: {
            /**
             * 获取闲置物品列表
             * @param {Number} page - 当前页码
             */
            findIdleTiem(page){
                // 显示加载动画
                const loading = this.$loading({
                    lock: true,
                    text: '加载数据中',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0)'
                });
                
                // 根据是否有分类标签选择不同的API
                if(this.labelName > 0){
                    // 按分类标签获取物品列表
                    this.$api.findIdleTiemByLable({
                        idleLabel: this.labelName,
                        page: page,
                        nums: 8
                    }).then(res => {
                        console.log(res);
                        let list = res.data.list;
                        // 处理数据，格式化时间和图片URL
                        for (let i = 0; i < list.length; i++) {
                            list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
                            let pictureList = JSON.parse(list[i].pictureList);
                            list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                        }
                        this.idleList = list;
                        this.totalItem = res.data.count;
                        console.log(this.totalItem);
                    }).catch(e => {
                        console.log(e)
                    }).finally(()=>{
                        loading.close(); // 关闭加载动画
                    })
                } else {
                    // 获取所有物品列表
                    this.$api.findIdleTiem({
                        page: page,
                        nums: 8
                    }).then(res => {
                        console.log(res);
                        let list = res.data.list;
                        // 处理数据，格式化时间和图片URL
                        for (let i = 0; i < list.length; i++) {
                            list[i].timeStr = list[i].releaseTime.substring(0, 10) + " " + list[i].releaseTime.substring(11, 19);
                            let pictureList = JSON.parse(list[i].pictureList);
                            list[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                        }
                        this.idleList = list;
                        this.totalItem = res.data.count;
                        console.log(this.totalItem);
                    }).catch(e => {
                        console.log(e)
                    }).finally(()=>{
                        loading.close(); // 关闭加载动画
                    })
                }
            },
            /**
             * 标签页切换时触发
             * @param {Object} tab - 标签页对象
             * @param {Object} event - 事件对象
             */
            handleClick(tab, event) {
                console.log(this.labelName);
                // 更新路由参数，保持URL同步
                this.$router.replace({query: {page: 1, labelName: this.labelName}});
            },
            /**
             * 分页页码变化时触发
             * @param {Number} val - 当前页码
             */
            handleCurrentChange(val) {
                console.log(`当前页: ${val}`);
                // 更新路由参数，保持URL同步
                this.$router.replace({query: {page: val, labelName: this.labelName}});
            },
            /**
             * 跳转到物品详情页
             * @param {Object} idle - 闲置物品对象
             */
            toDetails(idle) {
                this.$router.push({path: '/details', query: {id: idle.id}});
            }
        }
    }
</script>

<style scoped>
    /* 闲置物品卡片样式 */
    .idle-card {
        height: 300px;
        border: #eeeeee solid 1px;
        margin-bottom: 15px;
        cursor: pointer;
    }

    /* 分页组件样式 */
    .fenye {
        display: flex;
        justify-content: center;
        height: 60px;
        align-items: center;
    }

    /* 物品标题样式 */
    .idle-title {
        font-size: 18px;
        font-weight: 600;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin: 10px;
    }

    /* 物品价格样式 */
    .idle-prive {
        font-size: 16px;
        color: red;
    }

    /* 物品地点样式 */
    .idle-place {
        font-size: 13px;
        color: #666666;
        float: right;
        padding-right: 20px;
    }

    /* 发布时间样式（已注释） */
    .idle-time {
        color: #666666;
        font-size: 12px;
        margin: 0 10px;
    }

    /* 用户昵称样式 */
    .user-nickname {
        color: #999999;
        font-size: 12px;
        display: flex;
        align-items: center;
        height: 30px;
        padding-left: 10px;
    }

    /* 用户信息容器样式 */
    .user-info {
        margin-top: 10px;
        float: right;
        padding: 5px 10px;
        height: 30px;
        display: flex;
    }
</style>
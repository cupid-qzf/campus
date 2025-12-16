<template>
    <div class="main-border">
        <!-- 标签页导航，用于切换上线/下架物品列表 -->
        <el-menu default-active="1" class="el-menu-demo" mode="horizontal" @select="handleSelect">
            <el-menu-item index="1">上线的二手物品</el-menu-item>
            <el-menu-item index="2">下架的二手物品</el-menu-item>
        </el-menu>
        
        <!-- 上线物品列表 -->
        <el-table v-if="this.mode == 1"
                :data="onlineGoods"
                stripe
                style="width: 100%;color: #5a5c61;">
            <el-table-column
                    prop="releaseTime"
                    label="发布日期"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="idleName"
                    label="二手物品名称"
                    show-overflow-tooltip
                    >
            </el-table-column>
            <el-table-column
                    prop="user.nickname"
                    label="发布用户"
                    show-overflow-tooltip
                    min-width="100"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="idlePrice"
                    label="价格"
                    show-overflow-tooltip
                    min-width="100"
                    width="100">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="danger"
                            @click="makeOfflineGoods(scope.$index)">违规下架</el-button>
                </template>
            </el-table-column>
        </el-table>
        
        <!-- 下架物品列表 -->
        <el-table v-show="this.mode == 2"
                  :data="OfflineGoods"
                  stripe
                  style="width: 100%;color: #5a5c61;">
            <el-table-column
                    prop="releaseTime"
                    label="发布日期"
                    width="200">
            </el-table-column>
            <el-table-column
                    prop="idleName"
                    label="二手物品名称"
                    show-overflow-tooltip
            >
            </el-table-column>
            <el-table-column
                    prop="user.nickname"
                    label="发布用户"
                    show-overflow-tooltip
                    min-width="100"
                    width="100">
            </el-table-column>
            <el-table-column
                    prop="idlePrice"
                    label="价格"
                    show-overflow-tooltip
                    min-width="100"
                    width="100">
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button
                            size="mini"
                            type="danger"
                            @click="deleteGoods(scope.$index)">永久删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        
        <!-- 分页组件 -->
        <div class="block">
            <el-pagination
                    @current-change="handleCurrentChange"
                    :current-page.sync="nowPage"
                    :page-size="8"
                    background
                    layout="prev, pager, next,jumper"
                    :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    /**
     * 闲置物品管理组件（管理员端）
     * 用于展示和管理所有上线和下架的二手物品
     */
    export default {
        name: "IdleGoods",
        data(){
            return {
                mode:1, // 1-上线物品，2-下架物品
                nowPage: 1, // 当前页码
                total: 0, // 总记录数
                onlineGoods: [], // 上线物品列表
                OfflineGoods:[], // 下架物品列表
            }
        },
        /**
         * 组件创建后调用，初始化加载上线物品列表
         */
        created() {
            this.getOnlineGoods();
        },
        methods: {
            /**
             * 分页页码变化时触发
             * @param {Number} val - 当前页码
             */
            handleCurrentChange(val) {
                this.nowPage = val;
                if(this.mode == 1){
                    this.getOnlineGoods();
                }
                if(this.mode == 2){
                    this.getOfflineGoods();
                }
            },
            /**
             * 标签页切换时触发
             * @param {Number} val - 标签页索引
             */
            handleSelect(val){
                if(this.mode !== val){
                    this.mode = val;
                    if(val == 1){
                        this.nowPage = 1;
                        this.getOnlineGoods();
                    }
                    if(val == 2){
                        this.nowPage = 1;
                        this.getOfflineGoods();
                    }
                }
            },
            /**
             * 将物品违规下架
             * @param {Number} i - 物品索引
             */
            makeOfflineGoods(i){
                this.$api.updateGoods({
                    id: this.onlineGoods[i].id,
                    status:2
                }).then(res => {
                    if(res.status_code==1){
                        this.getOnlineGoods(); // 更新列表
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            /**
             * 永久删除下架物品
             * @param {Number} i - 物品索引
             */
            deleteGoods(i){
                this.$api.updateGoods({
                    id: this.OfflineGoods[i].id,
                    status:0
                }).then(res => {
                    if(res.status_code==1){
                        this.getOfflineGoods(); // 更新列表
                    }else {
                        this.$message.error(res.msg)
                    }

                }).catch(e => {
                    console.log(e)
                })
            },
            /**
             * 获取上线物品列表
             */
            getOnlineGoods(){
                this.$api.getGoods({
                    status:1,
                    page: this.nowPage,
                    nums:8
                }).then(res => {
                    if(res.status_code==1){
                        this.onlineGoods = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            },
            /**
             * 获取下架物品列表
             */
            getOfflineGoods(){
                this.$api.getGoods({
                    status:2,
                    page: this.nowPage,
                    nums:8
                }).then(res => {
                    if(res.status_code==1){
                        this.OfflineGoods = res.data.list;
                        this.total = res.data.count;
                    }else {
                        this.$message.error(res.msg)
                    }
                }).catch(e => {
                    console.log(e)
                })
            }
        }

    }
</script>

<style scoped>
    /* 组件主容器样式 */
    .main-border{
        background-color: #FFF;
        padding: 10px 30px;
        box-shadow: 0 1px 15px -6px rgba(0,0,0,.5);
        border-radius: 5px;
    }
    /* 分页组件容器样式 */
    .block {
        display: flex;
        justify-content:center;
        padding-top: 15px;
        padding-bottom: 10px;
        width: 100%;
    }
</style>
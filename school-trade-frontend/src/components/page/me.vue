<template>
    <div>
        <!-- 头部组件 -->
        <app-head :nickname-value="userInfo.nickname"
                  :avatarValue="userInfo.avatar"></app-head>
        
        <!-- 主体内容 -->
        <app-body>
            <!-- 用户信息和闲置物品管理区域 -->
            <div v-show="!eidtAddress">
                <!-- 用户信息容器 -->
                <div class="user-info-container">
                    <div class="user-info-details">
                        <!-- 头像上传区域 -->
                        <el-upload
                                action="http://localhost:8080/file/"
                                :on-success="fileHandleSuccess"
                                :file-list="imgFileList"
                                accept="image/*"
                        >
                            <el-image
                                    style="width: 120px; height: 120px;border-radius: 10px;"
                                    :src="userInfo.avatar"
                                    fit="contain"></el-image>
                        </el-upload>
                        <div class="user-info-details-text">
                            <div class="user-info-details-text-nickname">{{userInfo.nickname}}</div>
                            <div class="user-info-details-text-time">{{userInfo.signInTime}} 加入平台</div>
                            <div class="user-info-details-text-edit">
                                <el-button type="primary" plain @click="userInfoDialogVisible = true">编辑个人信息</el-button>
                            </div>
                            <el-dialog
                                    @close="finishEdit"
                                    title="编辑个人信息"
                                    :visible.sync="userInfoDialogVisible"
                                    width="400px">
                                <div class="edit-tip">昵称</div>
                                <el-input
                                        v-model="userInfo.nickname"
                                        :disabled="notUserNicknameEdit"
                                        @change="saveUserNickname">
                                    <el-button slot="append" type="warning" icon="el-icon-edit"
                                               @click="notUserNicknameEdit = false">编辑
                                    </el-button>
                                </el-input>

                                <div v-if="userPasswordEdit">
                                    <div class="edit-tip">原密码</div>
                                    <el-input v-model="userPassword1" show-password></el-input>
                                    <div class="edit-tip">新密码</div>
                                    <el-input v-model="userPassword2" show-password></el-input>
                                    <div class="edit-tip">确认新密码</div>
                                    <el-input v-model="userPassword3" show-password></el-input>
                                    <div class="edit-tip"></div>
                                    <el-button @click="savePassword" plain>确认修改</el-button>
                                </div>
                                <div v-else>
                                    <div class="edit-tip">密码</div>
                                    <el-input
                                            value="123456"
                                            :disabled="true"
                                            show-password>
                                        <el-button slot="append" type="warning" icon="el-icon-edit"
                                                   @click="userPasswordEdit = true">编辑
                                        </el-button>
                                    </el-input>
                                </div>
                                <span slot="footer" class="dialog-footer">
                                <el-button @click="userInfoDialogVisible=false">完成</el-button>
                            </span>
                            </el-dialog>
                        </div>
                    </div>
                    <div class="user-info-splace">
                        <el-button type="primary" plain @click="eidtAddress=true">编辑收货地址</el-button>
                    </div>
                </div>
                <div class="idle-container">
                    <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane label="我发布的" name="1"></el-tab-pane>
                        <el-tab-pane label="我下架的" name="2"></el-tab-pane>
                        <el-tab-pane label="我收藏的" name="3"></el-tab-pane>
                        <el-tab-pane label="我卖出的" name="4"></el-tab-pane>
                        <el-tab-pane label="我买到的" name="5"></el-tab-pane>
                    </el-tabs>
                    <div class="idle-container-list">
                        <div v-for="(item,index) in dataList[activeName-1]" class="idle-container-list-item">
                            <div class="idle-container-list-item-detile" @click="toDetails(activeName,item)">
                                <el-image
                                        style="width: 100px; height: 100px;"
                                        :src="item.imgUrl"
                                        fit="cover">
                                    <div slot="error" class="image-slot">
                                        <i class="el-icon-picture-outline">无图</i>
                                    </div>
                                </el-image>
                                <div class="idle-container-list-item-text">
                                    <div class="idle-container-list-title">
                                        {{item.idleName}}
                                    </div>
                                    <div class="idle-container-list-idle-details" v-html="item.idleDetails">
                                        {{item.idleDetails}}
                                    </div>
                                    <div class="idle-container-list-idle-time">{{item.timeStr}}</div>

                                    <div class="idle-item-foot">
                                        <div class="idle-prive">￥{{item.idlePrice}}
                                            {{(activeName==='4'||activeName==='5')?orderStatus[item.orderStatus]:''}}
                                        </div>
                                        <el-button v-if="activeName!=='4'&&activeName!=='5'" type="danger" size="mini" slot="reference"
                                                   plain @click.stop="handle(activeName,item,index)">{{handleName[activeName-1]}}
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-show="eidtAddress" class="address-container">
                <el-page-header class="address-container-back" @back="eidtAddress=false"
                                content="收货地址"></el-page-header>
                <div class="address-container-add">
                    <div class="address-container-add-title">新增收货地址</div>
                    <div class="address-container-add-item">
                        <el-input placeholder="请输入收货人姓名" v-model="addressInfo.consigneeName" maxlength="10"
                                  show-word-limit>
                            <div slot="prepend">收货人姓名</div>
                        </el-input>
                    </div>
                    <div class="address-container-add-item">
                        <el-input placeholder="请输入收货人手机号" v-model="addressInfo.consigneePhone"
                                  onkeyup="this.value=this.value.replace(/[^\d.]/g,'');" maxlength="11" show-word-limit>
                            <div slot="prepend">手机号</div>
                        </el-input>
                    </div>

                    <div class="address-container-add-item">
                        <span class="demonstration">省/市/区</span>
                        <el-cascader
                                :options="options"
                                v-model="selectedOptions"
                                @change="handleAddressChange"
                                :separator="' '"
                        >
                        </el-cascader>
                    </div>
                    <div class="address-container-add-item">
                        <el-input placeholder="请输入详细地址（如道路、门牌号、小区、楼栋号等信息）" v-model="addressInfo.detailAddress"
                                  maxlength="50" show-word-limit>
                            <div slot="prepend">详细地址</div>
                        </el-input>
                    </div>
                    <el-checkbox v-model="addressInfo.defaultFlag">设置为默认地址</el-checkbox>
                    <el-button style="margin-left: 20px;" @click="saveAddress">保存</el-button>
                </div>
                <div class="address-container-list">
                    <div style="color: #409EFF;font-size: 15px;padding-left: 10px;">已有收货地址</div>
                    <el-table
                            stripe
                            :data="addressData"
                            style="width: 100%">
                        <el-table-column
                                prop="consigneeName"
                                label="收货人姓名"
                                width="100">
                        </el-table-column>
                        <el-table-column
                                prop="consigneePhone"
                                label="手机号"
                                width="120">
                        </el-table-column>
                        <el-table-column
                                prop="detailAddressText"
                                label="地址"
                                width="270">
                        </el-table-column>
                        <el-table-column label="操作">
                            <template slot-scope="scope">
                                <el-button
                                        size="mini"
                                        @click="handleEdit(scope.$index, scope.row)">编辑
                                </el-button>
                                <el-button
                                        size="mini"
                                        type="danger"
                                        @click="handleDelete(scope.$index, scope.row)">删除
                                </el-button>
                            </template>
                        </el-table-column>
                        <el-table-column label="是否默认地址" width="110">
                            <template slot-scope="scope">
                                <el-button v-if="!scope.row.defaultFlag"
                                           size="mini"
                                           @click="handleSetDefault(scope.$index, scope.row)">设为默认
                                </el-button>
                                <div v-else style="padding-left: 10px;color: #409EFF;">{{scope.row.defaultAddress}}
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </div>
            <app-foot></app-foot>
        </app-body>
    </div>
</template>

<script>
    import AppHead from '../common/AppHeader.vue';
    import AppBody from '../common/AppPageBody.vue'
    import AppFoot from '../common/AppFoot.vue'
    import options from '../common/country-data.js'

    /**
     * 个人中心组件
     * 展示和管理用户个人信息、闲置物品和收货地址
     */
    export default {
        name: "me",
        // 注册组件
        components: {
            AppHead,  // 头部组件
            AppBody,  // 主体内容组件
            AppFoot   // 底部组件
        },
        data() {
            return {
                imgFileList: [],  // 头像文件列表
                // 收货地址信息
                addressInfo: {
                    consigneeName: '',  // 收货人姓名
                    consigneePhone: '',  // 收货人手机号
                    provinceName: '',  // 省
                    cityName: '',  // 市
                    regionName: '',  // 区
                    detailAddress: '',  // 详细地址
                    defaultFlag: false  // 是否默认地址
                },
                activeName: '1',  // 当前激活的标签页
                handleName: ['下架', '删除', '取消收藏', '', ''],  // 操作按钮名称
                // 数据列表：[我发布的, 我下架的, 我收藏的, 我卖出的, 我买到的]
                dataList: [[], [], [], [], [], [], [], []],
                orderStatus: ['待付款', '待发货', '待收货', '已完成', '已取消'],  // 订单状态
                userInfoDialogVisible: false,  // 用户信息编辑对话框可见性
                notUserNicknameEdit: true,  // 是否禁止编辑昵称
                userPasswordEdit: false,  // 是否编辑密码
                userPassword1: '',  // 原密码
                userPassword2: '',  // 新密码
                userPassword3: '',  // 确认新密码
                eidtAddress: false,  // 是否编辑收货地址
                selectedOptions: [],// 存放默认地址选择
                options: options,   // 存放城市数据,
                userInfo: {
                    accountNumber: "",
                    avatar: "",
                    nickname: "",
                    signInTime: "",
                },
                addressData: []
            };
        },
        created() {
            if (!this.$globalData.userInfo.nickname) {
                this.$api.getUserInfo().then(res => {
                    if (res.status_code === 1) {
                        res.data.signInTime = res.data.signInTime.substring(0, 10);
                        console.log(res.data);
                        this.$globalData.userInfo = res.data;
                        this.userInfo = this.$globalData.userInfo;
                    }
                })
            } else {
                this.userInfo = this.$globalData.userInfo;
                console.log(this.userInfo);
            }
            this.getAddressData();
            this.getIdleItemData();
            this.getMyOrder();
            this.getMySoldIdle();
            this.getMyFavorite();
        },
        methods: {
            getMyFavorite(){
                this.$api.getMyFavorite().then(res=>{
                    console.log('getMyFavorite',res);
                    if (res.status_code === 1){
                        for (let i = 0; i < res.data.length; i++) {
                            let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                            this.dataList[2].push({
                                favoriteId:res.data[i].id,
                                id:res.data[i].idleItem.id,
                                imgUrl:pictureList.length > 0 ? pictureList[0] : '',
                                idleName:res.data[i].idleItem.idleName,
                                idleDetails:res.data[i].idleItem.idleDetails,
                                timeStr:res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                                idlePrice:res.data[i].idleItem.idlePrice
                            });
                        }
                    }
                })
            },
            /**
             * 获取我卖出的闲置物品
             */
            getMySoldIdle(){
                this.$api.getMySoldIdle().then(res=>{
                    if (res.status_code === 1){
                        console.log('getMySoldIdle',res.data);
                        for (let i = 0; i < res.data.length; i++) {
                            let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                            this.dataList[3].push({
                                id:res.data[i].id,
                                imgUrl:pictureList.length > 0 ? pictureList[0] : '',
                                idleName:res.data[i].idleItem.idleName,
                                idleDetails:res.data[i].idleItem.idleDetails,
                                timeStr:res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                                idlePrice:res.data[i].orderPrice,
                                orderStatus:res.data[i].orderStatus
                            });
                        }
                    }
                })
            },
            /**
             * 获取我买到的闲置物品
             */
            getMyOrder(){
                this.$api.getMyOrder().then(res=>{
                    if (res.status_code === 1){
                        console.log('getMyOrder',res.data);
                        for (let i = 0; i < res.data.length; i++) {
                            let pictureList = JSON.parse(res.data[i].idleItem.pictureList);
                            this.dataList[4].push({
                                id:res.data[i].id,
                                imgUrl:pictureList.length > 0 ? pictureList[0] : '',
                                idleName:res.data[i].idleItem.idleName,
                                idleDetails:res.data[i].idleItem.idleDetails,
                                timeStr:res.data[i].createTime.substring(0, 10) + " " + res.data[i].createTime.substring(11, 19),
                                idlePrice:res.data[i].orderPrice,
                                orderStatus:res.data[i].orderStatus
                            });
                        }
                    }
                })
            },
            /**
             * 获取用户的闲置物品数据
             * 区分已发布和已下架的物品
             */
            getIdleItemData() {
                this.$api.getAllIdleItem().then(res => {
                    console.log(res);
                    if (res.status_code === 1) {
                        for (let i = 0; i < res.data.length; i++) {
                            // 格式化时间
                            res.data[i].timeStr = res.data[i].releaseTime.substring(0, 10) + " " + res.data[i].releaseTime.substring(11, 19);
                            // 获取第一张图片作为展示图
                            let pictureList = JSON.parse(res.data[i].pictureList);
                            res.data[i].imgUrl = pictureList.length > 0 ? pictureList[0] : '';
                            
                            // 根据物品状态分类
                            if (res.data[i].idleStatus === 1) {
                                this.dataList[0].push(res.data[i]);  // 已发布
                            } else if (res.data[i].idleStatus === 2) {
                                this.dataList[1].push(res.data[i]);  // 已下架
                            }
                        }
                    }
                })
            },
            getAddressData() {
                this.$api.getAddress().then(res => {
                    if (res.status_code === 1) {
                        let data = res.data;
                        for (let i = 0; i < data.length; i++) {
                            data[i].detailAddressText = data[i].provinceName + data[i].cityName + data[i].regionName + data[i].detailAddress;
                            data[i].defaultAddress = data[i].defaultFlag ? '默认地址' : '设为默认';
                        }
                        console.log(data);
                        this.addressData = data;
                    }
                })
            },
            handleClick(tab, event) {
                // console.log(tab, event);
                console.log(this.activeName);
            },
            /**
             * 保存用户昵称
             */
            saveUserNickname() {
                this.notUserNicknameEdit = true;
                this.$api.updateUserPublicInfo({
                    nickname: this.userInfo.nickname
                }).then(res => {
                    console.log(res);
                    // 更新全局用户信息
                    this.$globalData.userInfo.nickname = this.userInfo.nickname;
                })
            },
            /**
             * 保存用户密码
             */
            savePassword() {
                if (!this.userPassword1 || !this.userPassword2) {
                    this.$message.error('密码为空！');
                } else if (this.userPassword2 === this.userPassword3) {
                    this.$api.updatePassword({
                        oldPassword: this.userPassword1,
                        newPassword: this.userPassword2
                    }).then(res => {
                        if (res.status_code === 1) {
                            this.userPasswordEdit = false;
                            this.$message({
                                message: '修改成功！',
                                type: 'success'
                            });
                            // 清空密码输入框
                            this.userPassword1 = '';
                            this.userPassword2 = '';
                            this.userPassword3 = '';
                        } else {
                            this.$message.error('旧密码错误，修改失败！');
                        }
                    })
                } else {
                    this.$message.error('两次输入的密码不一致！');
                }
            },
            finishEdit() {
                this.notUserNicknameEdit = true;
                this.userInfoDialogVisible = false;
                this.userPasswordEdit = false;
            },
            handleAddressChange(value) {
                console.log(value);
                this.addressInfo.provinceName = value[0];
                this.addressInfo.cityName = value[1];
                this.addressInfo.regionName = value[2];
            },
            handleEdit(index, row) {
                console.log(index, row);
                this.addressInfo = JSON.parse(JSON.stringify(row));
                this.selectedOptions = ['', '', ''];
                this.selectedOptions[0] = row.provinceName;
                this.selectedOptions[1] = row.cityName;
                this.selectedOptions[2] = row.regionName;
            },
            handleDelete(index, row) {
                console.log(index, row);
                this.$confirm('是否确定删除该地址?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$api.deleteAddress(row).then(res => {
                        if (res.status_code === 1) {
                            this.$message({
                                message: '删除成功！',
                                type: 'success'
                            });
                            this.addressData.splice(index, 1);
                            if (row.defaultFlag && this.addressData.length > 0) {
                                this.addressData[0].defaultFlag = true;
                                this.addressData[0].defaultAddress = '默认地址';
                                this.update({
                                    id: this.addressData[0].id,
                                    defaultFlag: true
                                });
                            }
                        } else {
                            this.$message.error('系统异常，删除失败！')
                        }
                    }).catch(() => {
                        this.$message.error('网络异常！')
                    });
                }).catch(() => {
                });

            },
            handleSetDefault(index, row) {
                console.log(index, row);
                row.defaultFlag = true;
                this.update(row);
            },
            /**
             * 跳转到详情页
             * @param {string} activeName 当前标签页
             * @param {Object} item 物品或订单对象
             */
            toDetails(activeName, item) {
                if (activeName === '4'||activeName === '5') {
                    // 跳转到订单详情
                    this.$router.push({path: '/order', query: {id: item.id}});
                } else {
                    // 跳转到物品详情
                    this.$router.push({path: '/details', query: {id: item.id}});
                }
            },
            handle(activeName,item,index) {
                console.log(activeName,item,index);
                this.$confirm('是否确认？', '提示', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    if(activeName==='1'){
                        this.$api.updateIdleItem({
                            id:item.id,
                            idleStatus:2
                        }).then(res=>{
                            console.log(res);
                            if(res.status_code===1){
                                this.dataList[0].splice(index,1);
                                item.idleStatus=2;
                                this.dataList[1].unshift(item);
                            }else {
                                this.$message.error(res.msg)
                            }
                        });
                    }else if(activeName==='2'){
                        this.$api.updateIdleItem({
                            id:item.id,
                            idleStatus:0
                        }).then(res=>{
                            console.log(res);
                            if(res.status_code===1){
                                this.dataList[1].splice(index,1);
                            }else {
                                this.$message.error(res.msg)
                            }
                        });
                    }else if(activeName==='3'){
                        this.$api.deleteFavorite({
                            id: item.favoriteId
                        }).then(res=>{
                            console.log(res);
                            if(res.status_code===1){
                                this.$message({
                                    message: '已取消收藏！',
                                    type: 'success'
                                });
                                this.dataList[2].splice(index,1);
                            }else {
                                this.$message.error(res.msg)
                            }
                        }).catch(e=>{
                        })
                    }
                }).catch(() => {
                });

            },
            fileHandleSuccess(response, file, fileList) {
                console.log("file:", response, file, fileList);
                let imgUrl = response.data;
                this.imgFileList = [];
                this.$api.updateUserPublicInfo({
                    avatar: imgUrl
                }).then(res => {
                    console.log(res);
                    this.userInfo.avatar = imgUrl;
                    this.$globalData.userInfo.avatar = imgUrl;
                })
            },
            update(data) {
                this.$api.updateAddress(data).then(res => {
                    if (res.status_code === 1) {
                        this.getAddressData();
                        this.$message({
                            message: '修改成功！',
                            type: 'success'
                        });
                    } else {
                        this.$message.error('系统异常，修改失败！')
                    }
                }).catch(() => {
                    this.$message.error('网络异常！')
                })
            },
            saveAddress() {
                if (this.addressInfo.id) {
                    console.log('update:', this.addressInfo);
                    this.update(this.addressInfo);
                    this.addressInfo = {
                        consigneeName: '',
                        consigneePhone: '',
                        provinceName: '',
                        cityName: '',
                        regionName: '',
                        detailAddress: '',
                        defaultFlag: false
                    };
                    this.selectedOptions = [];
                } else {
                    if (this.addressData.length >= 5) {
                        this.$message.error('已达到最大地址数量！')
                    } else {
                        console.log(this.addressInfo);
                        this.$api.addAddress(this.addressInfo).then(res => {
                            if (res.status_code === 1) {
                                this.getAddressData();
                                this.$message({
                                    message: '新增成功！',
                                    type: 'success'
                                });
                                this.selectedOptions = [];
                                this.addressInfo = {
                                    consigneeName: '',
                                    consigneePhone: '',
                                    provinceName: '',
                                    cityName: '',
                                    regionName: '',
                                    detailAddress: '',
                                    defaultFlag: false
                                };
                            } else {
                                this.$message.error('系统异常，新增失败！')
                            }
                        }).catch(e => {
                            this.$message.error('网络异常！')
                        })
                    }
                }
            }
        }
    }
</script>

<style scoped>

    .user-info-details {
        display: flex;
        height: 140px;
        align-items: center;
        margin: 20px 40px;
    }

    .user-info-details-text {
        margin-left: 20px;
    }

    .user-info-details-text-nickname {
        font-size: 26px;
        font-weight: 600;
        margin: 10px 0;
    }

    .user-info-details-text-time {
        font-size: 14px;
        margin-bottom: 10px;
    }

    .user-info-splace {
        margin-right: 90px;
    }

    .idle-container {
        padding: 0 20px;
    }

    .idle-container-list {
        min-height: 55vh;
    }

    .idle-container-list-item {
        border-bottom: 1px solid #eeeeee;
        cursor: pointer;
    }

    .idle-container-list-item:last-child {
        border-bottom: none;
    }

    .idle-container-list-item-detile {
        height: 120px;
        display: flex;
        align-items: center;
    }

    .idle-container-list-item-text {
        margin-left: 10px;
        height: 100px;
        max-width: 800px;
    }

    .idle-container-list-title {
        font-weight: 600;
        font-size: 18px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .idle-container-list-idle-details {
        font-size: 14px;
        color: #555555;
        padding-top: 5px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .idle-container-list-idle-time {
        font-size: 13px;
        padding-top: 5px;
    }

    .idle-prive {
        font-size: 15px;
        padding-top: 5px;
        color: red;
    }

    .edit-tip {
        font-size: 14px;
        margin: 10px 5px;
    }

    .address-container {
        padding: 10px 20px;
    }

    .address-container-back {
        margin-bottom: 10px;
    }

    .address-container-add-title {
        font-size: 15px;
        color: #409EFF;
        padding: 10px;
    }

    .address-container-add-item {
        margin-bottom: 20px;
    }

    .demonstration {
        color: #666666;
        font-size: 14px;
        padding: 10px;
    }

    .address-container-add {
        padding: 0 200px;
    }

    .address-container-list {
        padding: 30px 100px;
    }

    .idle-item-foot {
        width: 800px;
        display: flex;
        justify-content: space-between;
    }
</style>

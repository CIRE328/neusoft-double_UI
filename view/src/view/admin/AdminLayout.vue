<!-- AdminLayout：管理员后台布局框架 -->
<template>
  <el-container class="layout">
    <el-aside width="220px">
      <el-menu router :default-active="$route.path">
        <el-sub-menu index="customer">
          <template #title><el-icon><User /></el-icon>客户管理</template>
          <el-menu-item index="/admin/checkin">入住登记</el-menu-item>
          <el-menu-item index="/admin/checkout">退住登记</el-menu-item>
          <el-menu-item index="/admin/outward-audit">外出登记</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="bed">
          <template #title><el-icon><Place /></el-icon>床位管理</template>
          <el-menu-item index="/admin/bed-map">床位示意图</el-menu-item>
          <el-menu-item index="/admin/bed-manage">床位管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="nursing">
          <template #title><el-icon><FirstAid /></el-icon>护理管理</template>
          <el-menu-item index="/admin/nursing-level">护理级别</el-menu-item>
          <el-menu-item index="/admin/nursing-item">护理项目</el-menu-item>
          <el-menu-item index="/admin/customer-level">客户护理设置</el-menu-item>
          <el-menu-item index="/admin/nursing-record">护理记录</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="housekeeper">
          <template #title><el-icon><Service /></el-icon>健康管家</template>
          <el-menu-item index="/admin/assign-hk">设置服务对象</el-menu-item>
          <el-menu-item index="/admin/service-watch">服务关注</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/admin/user-manage">
          <el-icon><Setting /></el-icon>用户管理
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span>东软颐养中心管理系统</span>
        <el-button type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
/**
 * 管理员布局组件
 * 提供侧边导航菜单与顶部栏，嵌套渲染各管理子页面
 */
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

/** 退出登录并跳转至登录页 */
const logout = () => {
  store.dispatch('logout')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #ddd;
  padding: 0 20px;
}
</style>

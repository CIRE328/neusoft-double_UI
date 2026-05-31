<!-- HousekeeperLayout：健康管家工作台布局框架 -->
<template>
  <el-container class="layout">
    <el-aside width="200px">
      <el-menu router :default-active="$route.path">
        <el-menu-item index="/housekeeper/daily-nursing"><el-icon><Notebook /></el-icon>日常护理</el-menu-item>
        <el-menu-item index="/housekeeper/nursing-record-query"><el-icon><List /></el-icon>护理记录</el-menu-item>
        <el-menu-item index="/housekeeper/outward-apply"><el-icon><Location /></el-icon>外出申请</el-menu-item>
        <el-menu-item index="/housekeeper/checkout-apply"><el-icon><Close /></el-icon>退住申请</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span>健康管家工作台</span>
        <el-button type="danger" @click="logout">退出</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
/**
 * 健康管家布局组件
 * 提供侧边导航与顶部栏，嵌套渲染管家日常业务子页面
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
.el-header { display: flex; justify-content: space-between; align-items: center; background: #fff; border-bottom: 1px solid #ddd; padding: 0 20px; }
</style>

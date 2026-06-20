<!-- HousekeeperLayout：健康管家工作台布局框架 -->
<template>
  <el-container class="layout">
    <el-aside width="200px">
      <el-menu router :default-active="$route.path">
        <el-menu-item index="/housekeeper/my-customers">
          <el-icon><User /></el-icon>
          <span>我的客户</span>
        </el-menu-item>
        <el-menu-item index="/housekeeper/daily-nursing">
          <el-icon><Notebook /></el-icon>
          <span>日常护理</span>
        </el-menu-item>
        <el-menu-item index="/housekeeper/nursing-record-query">
          <el-icon><List /></el-icon>
          <span>护理记录</span>
        </el-menu-item>
        <el-menu-item index="/housekeeper/outward-apply">
          <el-icon><Location /></el-icon>
          <span>外出申请</span>
        </el-menu-item>
        <el-menu-item index="/housekeeper/checkout-apply">
          <el-icon><Close /></el-icon>
          <span>退住申请</span>
        </el-menu-item>
        <el-menu-item index="/housekeeper/my-applications">
          <el-icon><Document /></el-icon>
          <span>我的申请</span>
        </el-menu-item>
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
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { User, Notebook, List, Location, Close, Document } from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore()

const logout = () => {
  store.dispatch('logout')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.el-header { display: flex; justify-content: space-between; align-items: center; background: #fff; border-bottom: 1px solid #ddd; padding: 0 20px; }
</style>
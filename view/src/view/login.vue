<!-- login：用户登录页 -->
<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>东软颐养中心</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 登录页组件
 * 校验用户名密码，登录成功后按角色跳转至管理员或健康管家工作台
 */
import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const store = useStore()
const router = useRouter()
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }]
}
const formRef = ref(null)
const loading = ref(false)

/** 提交登录表单，成功后按角色跳转 */
const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  loading.value = true
  const success = await store.dispatch('login', form)
  loading.value = false
  if (success) {
    const isAdmin = store.getters.isAdmin
    router.push(isAdmin ? '/admin/checkin' : '/housekeeper/daily-nursing')
  } else {
    ElMessage.error('用户名或密码错误')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-card {
  width: 400px;
  text-align: center;
}
</style>

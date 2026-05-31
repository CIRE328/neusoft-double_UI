<!-- UserManage：用户管理，管理系统用户账号 -->
<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="用户姓名"><el-input v-model="searchName" /></el-form-item>
      <el-button @click="searchUsers">查询</el-button>
      <el-button type="primary" @click="openAddDialog">新增用户</el-button>
    </el-form>
    <el-table :data="users" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phoneNumber" label="手机号" />
      <el-table-column label="角色">
        <template #default="{ row }">{{ row.roleId === 1 ? '系统管理员' : '健康管家' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" @click="resetPwd(row)">重置密码</el-button>
          <el-button size="small" type="danger" @click="deleteUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="新增用户" v-model="addDialog">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phoneNumber" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId">
            <el-option label="系统管理员" :value="1" />
            <el-option label="健康管家" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialog=false">取消</el-button>
        <el-button type="primary" @click="doAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 用户管理组件
 * 查询系统用户，支持新增用户、重置密码与删除用户
 */
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const users = ref([])
const addDialog = ref(false)
const form = ref({ username: '', nickname: '', phoneNumber: '', roleId: 2 })

/** 按姓名查询用户列表 */
const searchUsers = async () => {
  const res = await request.get('/user/list', { params: { name: searchName.value } })
  users.value = res.data
}

/** 打开新增用户对话框 */
const openAddDialog = () => {
  form.value = { username: '', nickname: '', phoneNumber: '', roleId: 2 }
  addDialog.value = true
}

/** 提交新增用户 */
const doAdd = async () => {
  await request.post('/user', form.value)
  ElMessage.success('添加成功')
  addDialog.value = false
  searchUsers()
}

/** 重置用户密码为手机号后6位 */
const resetPwd = (user) => {
  ElMessageBox.confirm(`重置密码为手机号后6位（${user.phoneNumber?.slice(-6)}），确定吗？`, '提示').then(async () => {
    await request.post('/user/reset-password', { id: user.id })
    ElMessage.success('密码已重置')
  })
}

/** 删除用户 */
const deleteUser = (user) => {
  ElMessageBox.confirm('删除用户不可恢复，确定吗？', '提示').then(async () => {
    await request.delete('/user', { params: { id: user.id } })
    ElMessage.success('删除成功')
    searchUsers()
  })
}

onMounted(() => searchUsers())
</script>

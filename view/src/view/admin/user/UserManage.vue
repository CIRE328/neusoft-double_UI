<!-- UserManage：用户管理，管理系统用户账号，管理员账号不可编辑/删除 -->
<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="用户姓名"><el-input v-model="searchName" /></el-form-item>
      <el-button @click="searchUsers">查询</el-button>
      <el-button type="primary" @click="openAddDialog">新增健康管家</el-button>
    </el-form>
    <el-table :data="users" border>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phoneNumber" label="手机号" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="性别">
        <template #default="{ row }">{{ row.sex === 1 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column label="角色">
        <template #default="{ row }">{{ row.roleId === 1 ? '系统管理员' : '健康管家' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button size="small" :disabled="row.roleId === 1" @click="resetPwd(row)">重置密码</el-button>
          <el-button size="small" type="danger" :disabled="row.roleId === 1" @click="deleteUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="新增健康管家" v-model="addDialog">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phoneNumber" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleId" disabled>
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
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const users = ref([])
const addDialog = ref(false)
const form = ref({ username: '', nickname: '', phoneNumber: '', email: '', sex: 1, roleId: 2 })

const searchUsers = async () => {
  const res = await request.get('/user/list', { params: { name: searchName.value } })
  users.value = res.data
}

const openAddDialog = () => {
  form.value = { username: '', nickname: '', phoneNumber: '', email: '', sex: 1, roleId: 2 }
  addDialog.value = true
}

const doAdd = async () => {
  await request.post('/user', form.value)
  ElMessage.success('添加成功')
  addDialog.value = false
  searchUsers()
}

const resetPwd = (user) => {
  ElMessageBox.confirm(`重置密码为手机号后6位（${user.phoneNumber?.slice(-6)}），确定吗？`, '提示').then(async () => {
    await request.post('/user/reset-password', { id: user.id })
    ElMessage.success('密码已重置')
  })
}

const deleteUser = (user) => {
  ElMessageBox.confirm('删除用户不可恢复，确定吗？', '提示').then(async () => {
    await request.delete('/user', { params: { id: user.id } })
    ElMessage.success('删除成功')
    searchUsers()
  })
}

onMounted(() => searchUsers())
</script>
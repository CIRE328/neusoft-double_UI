<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" clearable @input="loadCustomers" />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column prop="preferences" label="饮食喜好" />
      <el-table-column prop="attention" label="注意事项" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="editPreference(row)">编辑</el-button>
          <el-button link type="danger" @click="deletePreference(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="饮食喜好" v-model="dialogVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="饮食喜好">
          <el-input v-model="form.preferences" placeholder="如：少糖、清淡" />
        </el-form-item>
        <el-form-item label="注意事项">
          <el-input v-model="form.attention" placeholder="如：忌辛辣" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePreference">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const customers = ref([])      // 客户列表（含喜好信息）
const dialogVisible = ref(false)
const form = ref({ preferences: '', attention: '' })
const currentCustomer = ref(null)

// 加载客户列表，并附加喜好信息
const loadCustomers = async () => {
  try {
    const res = await request.get('/customer/list', { params: { name: searchName.value } })
    // 为每个客户获取喜好
    for (let c of res.data) {
      const prefRes = await request.get('/preference/list', { params: { customerId: c.id } })
      if (prefRes.data && prefRes.data.length > 0) {
        c.preferences = prefRes.data[0].preferences
        c.attention = prefRes.data[0].attention
        c.preferenceId = prefRes.data[0].id
      } else {
        c.preferences = ''
        c.attention = ''
        c.preferenceId = null
      }
    }
    customers.value = res.data
  } catch (error) {
    ElMessage.error('加载客户列表失败')
  }
}

// 编辑或添加喜好
const editPreference = (row) => {
  currentCustomer.value = row
  form.value = {
    preferences: row.preferences || '',
    attention: row.attention || ''
  }
  dialogVisible.value = true
}

// 保存喜好（新增或更新）
const savePreference = async () => {
  const customerId = currentCustomer.value.id
  try {
    await request.post('/preference', {
      customerId,
      preferences: form.value.preferences,
      attention: form.value.attention
    })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadCustomers()  // 刷新列表
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除喜好
const deletePreference = (row) => {
  if (!row.preferenceId) {
    ElMessage.warning('该客户没有饮食喜好记录')
    return
  }
  ElMessageBox.confirm('确定删除该客户的饮食喜好记录吗？', '提示').then(async () => {
    await request.delete('/preference', { params: { id: row.preferenceId } })
    ElMessage.success('删除成功')
    await loadCustomers()
  }).catch(() => {})
}

onMounted(() => loadCustomers())
</script>
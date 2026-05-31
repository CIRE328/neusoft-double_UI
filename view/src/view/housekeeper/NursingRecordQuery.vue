<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchCustomers" clearable />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="viewRecords(row)">查看护理记录</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="护理记录" v-model="recordDialog" width="800px">
      <el-table :data="records" border>
        <el-table-column prop="nursingContent" label="护理项目" />
        <el-table-column prop="nursingCount" label="数量" />
        <el-table-column prop="nursingTime" label="护理时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="deleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useStore()
const searchName = ref('')
const customers = ref([])
const recordDialog = ref(false)
const records = ref([])
const currentCustomer = ref(null)

const searchCustomers = async () => {
  try {
    const userId = store.state.user?.id
    const res = await request.get('/housekeeper/my-customers', {
      params: { name: searchName.value, userId }
    })
    customers.value = res.data
  } catch (error) {
    ElMessage.error('加载客户列表失败')
  }
}

const viewRecords = async (customer) => {
  currentCustomer.value = customer
  try {
    const res = await request.get('/nurse/record/customer', { params: { customerId: customer.id } })
    records.value = res.data
    recordDialog.value = true
  } catch (error) {
    ElMessage.error('获取护理记录失败')
  }
}

const deleteRecord = (row) => {
  ElMessageBox.confirm('删除记录不可恢复，确定吗？', '提示').then(async () => {
    try {
      await request.delete('/nurse/record', { params: { id: row.id } })
      ElMessage.success('删除成功')
      // 刷新当前客户的记录列表
      await viewRecords(currentCustomer.value)
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => searchCustomers())
</script>
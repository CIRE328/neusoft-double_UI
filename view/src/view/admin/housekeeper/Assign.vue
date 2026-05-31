<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="管家姓名"><el-input v-model="hkName" @input="searchHousekeepers" /></el-form-item>
    </el-form>
    <el-table :data="housekeepers" border>
      <el-table-column prop="nickname" label="管家姓名" />
      <el-table-column label="服务客户">
        <template #default="{ row }">
          <span v-for="c in row.customers" :key="c.id">{{ c.customerName }}; </span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="addCustomer(row)">添加客户</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="添加服务客户" v-model="customerDialog">
      <el-input v-model="customerSearch" placeholder="客户姓名" @input="searchFreeCustomers" />
      <el-table :data="freeCustomers" border>
        <el-table-column prop="customerName" label="客户姓名" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="assign(row)">分配</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const hkName = ref('')
const housekeepers = ref([])
const customerDialog = ref(false)
const customerSearch = ref('')
const freeCustomers = ref([])
const currentHousekeeper = ref(null)

const searchHousekeepers = async () => {
  const res = await request.get('/housekeeper/list', { params: { name: hkName.value } })
  // 获取每个管家的服务客户
  for (let h of res.data) {
    const custRes = await request.get('/housekeeper/customers', { params: { housekeeperId: h.id } })
    h.customers = custRes.data
  }
  housekeepers.value = res.data
}
const addCustomer = (hk) => {
  currentHousekeeper.value = hk
  customerDialog.value = true
  searchFreeCustomers()
}
const searchFreeCustomers = async () => {
  const res = await request.get('/customer/without-housekeeper', { params: { name: customerSearch.value } })
  freeCustomers.value = res.data
}
const assign = async (customer) => {
  await request.post('/housekeeper/assign', { customerId: customer.id, housekeeperId: currentHousekeeper.value.id })
  ElMessage.success('分配成功')
  customerDialog.value = false
  searchHousekeepers()
}
onMounted(() => searchHousekeepers())
</script>
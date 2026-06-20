<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" clearable @input="loadCustomers" />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="姓名" />
      <el-table-column prop="customerAge" label="年龄" />
      <el-table-column prop="customerSex" label="性别" :formatter="(row) => row.customerSex === 1 ? '女' : '男'" />
      <el-table-column prop="contactTel" label="联系电话" />
      <el-table-column prop="roomNo" label="房间号" />
      <el-table-column prop="levelId" label="护理级别" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { getUserId } from '../../utils/auth'

const searchName = ref('')
const customers = ref([])

const loadCustomers = async () => {
  const userId = getUserId()
  const res = await request.get('/housekeeper/my-customers', { params: { name: searchName.value, userId } })
  customers.value = res.data
}

onMounted(() => loadCustomers())
</script>
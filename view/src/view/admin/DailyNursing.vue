<template>
  <div>
    <el-select v-model="selectedCustomerId" placeholder="选择客户" filterable @change="loadItems">
      <el-option v-for="c in customers" :key="c.id" :label="c.customerName" :value="c.id" />
    </el-select>
    <el-table :data="items" border>
      <el-table-column prop="nursingName" label="项目名称" />
      <el-table-column prop="nurseNumber" label="剩余次数" />
      <el-table-column prop="maturityTime" label="到期日期" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="performNursing(row)">执行护理</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useStore()
const housekeeperId = computed(() => store.state.user?.id)

const customers = ref([])
const selectedCustomerId = ref(null)
const items = ref([])

const loadCustomers = async () => {
  const res = await request.get('/housekeeper/my-customers', { params: { userId: housekeeperId.value } })
  customers.value = res.data
}

const loadItems = async () => {
  const res = await request.get('/customer/nurse-items', { params: { customerId: selectedCustomerId.value } })
  items.value = res.data
}

const performNursing = async (item) => {
  const { value: count } = await ElMessageBox.prompt('请输入护理次数', '护理', { inputValue: '1' })
  if (!count) return
  await request.post('/nurse/perform', {
    customerId: selectedCustomerId.value,
    itemId: item.itemId,
    nursingCount: Number(count),
    userId: housekeeperId.value
  })
  ElMessage.success('护理完成')
  loadItems()
}

onMounted(() => loadCustomers())
</script>
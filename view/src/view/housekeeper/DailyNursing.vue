<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="loadCustomers" clearable />
    <el-table :data="customers" border style="margin-top: 10px">
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openNursing(row)">日常护理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="执行护理" v-model="nursingDialog" width="500px">
      <el-form>
        <el-form-item label="护理项目">
          <el-select v-model="selectedItemId" placeholder="请选择护理项目">
            <el-option
                v-for="item in customerItems"
                :key="item.itemId"
                :label="item.nursingName"
                :value="item.itemId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="护理数量">
          <el-input-number v-model="nursingCount" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nursingDialog = false">取消</el-button>
        <el-button type="primary" @click="submitNursing" :loading="submitting">提交护理记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const store = useStore()
const searchName = ref('')
const customers = ref([])
const nursingDialog = ref(false)
const currentCustomer = ref(null)
const customerItems = ref([])
const selectedItemId = ref(null)
const nursingCount = ref(1)
const submitting = ref(false)

const loadCustomers = async () => {
  try {
    const userId = store.state.user?.id
    if (!userId) {
      ElMessage.error('未获取到管家信息，请重新登录')
      return
    }
    const res = await request.get('/housekeeper/my-customers', {
      params: { name: searchName.value, userId }
    })
    customers.value = res.data
  } catch (error) {
    ElMessage.error('加载客户列表失败')
  }
}

const openNursing = async (customer) => {
  currentCustomer.value = customer
  try {
    const res = await request.get('/customer/nurse-items', { params: { customerId: customer.id } })
    customerItems.value = res.data
    if (customerItems.value.length === 0) {
      ElMessage.warning('该客户没有可执行的护理项目')
      nursingDialog.value = false
      return
    }
    selectedItemId.value = customerItems.value[0]?.itemId
    nursingCount.value = 1
    nursingDialog.value = true
  } catch (error) {
    ElMessage.error('获取护理项目失败')
  }
}

const submitNursing = async () => {
  if (!selectedItemId.value) {
    ElMessage.warning('请选择护理项目')
    return
  }
  submitting.value = true
  try {
    await request.post('/nurse/record', {
      customerId: currentCustomer.value.id,
      itemId: selectedItemId.value,
      nursingCount: nursingCount.value,
      userId: store.state.user.id
    })
    ElMessage.success('护理记录已保存')
    nursingDialog.value = false
    // 可选：刷新客户列表（更新剩余次数等信息）
    await loadCustomers()
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => loadCustomers())
</script>
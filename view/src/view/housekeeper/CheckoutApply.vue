<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchCustomers" clearable />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="applyCheckout(row)">退住申请</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="退住申请" v-model="applyDialog">
      <el-form :model="form" label-width="100px">
        <el-form-item label="退住类型">
          <el-select v-model="form.retreattype">
            <el-option label="正常退住" :value="1" />
            <el-option label="死亡退住" :value="2" />
            <el-option label="保留床位" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="退住原因"><el-input v-model="form.retreatmentreason" type="textarea" /></el-form-item>
        <el-form-item label="退住时间"><el-date-picker v-model="form.retreatment" type="date" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialog=false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting">提交</el-button>
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
const applyDialog = ref(false)
const currentCustomer = ref(null)
const submitting = ref(false)
const form = ref({ retreattype: 1, retreatmentreason: '', retreatment: new Date() })

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

const applyCheckout = (customer) => {
  currentCustomer.value = customer
  form.value = { retreattype: 1, retreatmentreason: '', retreatment: new Date() }
  applyDialog.value = true
}

const formatDate = (date) => {
  if (!date) return null
  const d = new Date(date)
  return `${d.getFullYear()}-${(d.getMonth()+1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`
}

const submitApply = async () => {
  if (!form.value.retreatmentreason) {
    ElMessage.warning('请填写退住原因')
    return
  }
  submitting.value = true
  try {
    await request.post('/backdown/apply', {
      customerId: currentCustomer.value.id,
      retreattype: form.value.retreattype,
      retreatmentreason: form.value.retreatmentreason,
      retreatment: formatDate(form.value.retreatment)
    })
    ElMessage.success('退住申请已提交')
    applyDialog.value = false
    await searchCustomers()
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => searchCustomers())
</script>
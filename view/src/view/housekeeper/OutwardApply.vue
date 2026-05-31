<!-- OutwardApply：外出申请，为服务客户提交外出申请 -->
<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchCustomers" clearable />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="applyOutward(row)">外出申请</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="外出申请" v-model="applyDialog">
      <el-form :model="form" label-width="100px">
        <el-form-item label="外出事由"><el-input v-model="form.outgoingreasons" /></el-form-item>
        <el-form-item label="外出时间"><el-date-picker v-model="form.outgoingtime" type="datetime" /></el-form-item>
        <el-form-item label="预计回院时间"><el-date-picker v-model="form.expectedreturntime" type="datetime" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialog=false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 外出申请组件
 * 为当前管家负责的客户填写外出事由与时间，提交外出申请待管理员审核
 */
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
const form = ref({ outgoingreasons: '', outgoingtime: null, expectedreturntime: null })

/** 加载当前管家负责的客户列表 */
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

/** 打开外出申请对话框 */
const applyOutward = (customer) => {
  currentCustomer.value = customer
  form.value = { outgoingreasons: '', outgoingtime: new Date(), expectedreturntime: null }
  applyDialog.value = true
}

/** 将日期格式化为 yyyy-MM-dd HH:mm:ss */
const formatDateTime = (date) => {
  if (!date) return null
  const d = new Date(date)
  const pad = (n) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

/** 提交外出申请 */
const submitApply = async () => {
  if (!form.value.outgoingreasons) {
    ElMessage.warning('请填写外出事由')
    return
  }
  submitting.value = true
  try {
    await request.post('/outward/apply', {
      customerId: currentCustomer.value.id,
      outgoingreasons: form.value.outgoingreasons,
      outgoingtime: formatDateTime(form.value.outgoingtime),
      expectedreturntime: formatDateTime(form.value.expectedreturntime)
    })
    ElMessage.success('申请已提交')
    applyDialog.value = false
    await searchCustomers()  // 刷新列表
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => searchCustomers())
</script>

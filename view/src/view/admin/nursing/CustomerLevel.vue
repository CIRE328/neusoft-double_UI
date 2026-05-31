<!-- CustomerLevel：客户护理设置，为客户配置或移除护理级别 -->
<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchCustomers" clearable />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="姓名" />
      <el-table-column prop="levelName" label="当前护理级别" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button v-if="!row.levelId" type="primary" size="small" @click="openSetLevel(row)">设置护理级别</el-button>
          <el-button v-else type="danger" size="small" @click="removeLevel(row)">移除级别</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="设置护理级别" v-model="levelDialog">
      <el-form>
        <el-form-item label="护理级别">
          <el-select v-model="selectedLevelId" placeholder="请选择">
            <el-option v-for="l in levels" :key="l.id" :label="l.levelName" :value="l.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="levelDialog=false">取消</el-button>
        <el-button type="primary" @click="confirmSetLevel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 客户护理设置组件
 * 查询客户当前护理级别，支持设置或移除护理级别
 */
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const customers = ref([])
const levels = ref([])
const levelDialog = ref(false)
const selectedLevelId = ref(null)
const currentCustomer = ref(null)

/** 查询客户列表并附加护理级别名称 */
const searchCustomers = async () => {
  const res = await request.get('/customer/list', { params: { name: searchName.value } })
  // 附加 levelName
  const levelRes = await request.get('/nurse/level/list')
  const levelMap = new Map(levelRes.data.map(l => [l.id, l.levelName]))
  customers.value = res.data.map(c => ({ ...c, levelName: levelMap.get(c.levelId) || '无' }))
}

/** 加载启用的护理级别列表 */
const loadLevels = async () => {
  const res = await request.get('/nurse/level/list')
  levels.value = res.data.filter(l => l.levelStatus === 1)
}

/** 打开设置护理级别对话框 */
const openSetLevel = (customer) => {
  currentCustomer.value = customer
  selectedLevelId.value = null
  levelDialog.value = true
}

/** 提交为客户设置护理级别 */
const confirmSetLevel = async () => {
  await request.post('/customer/set-level', { customerId: currentCustomer.value.id, levelId: selectedLevelId.value })
  ElMessage.success('设置成功')
  levelDialog.value = false
  searchCustomers()
}

/** 移除客户护理级别（同时移除关联护理项目） */
const removeLevel = (customer) => {
  ElMessageBox.confirm('移除级别将同时移除该客户所有关联的护理项目，确定吗？', '提示').then(async () => {
    await request.post('/customer/remove-level', { customerId: customer.id })
    ElMessage.success('已移除')
    searchCustomers()
  })
}

onMounted(() => { searchCustomers(); loadLevels() })
</script>

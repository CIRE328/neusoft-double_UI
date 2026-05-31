<!-- OutwardAudit：外出登记审核，审核外出申请并登记回院 -->
<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchList" clearable />
    <el-table :data="outwardList" border style="margin-top: 10px">
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column prop="outgoingreasons" label="外出事由" />
      <el-table-column prop="outgoingtime" label="外出时间" />
      <el-table-column prop="expectedreturntime" label="预计回院时间" />
      <el-table-column label="审核状态">
        <template #default="{ row }">{{ ['已提交', '通过', '不通过'][row.auditstatus] || '' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.auditstatus === 0" type="success" size="small" @click="audit(row, true)">通过</el-button>
          <el-button v-if="row.auditstatus === 0" type="danger" size="small" @click="audit(row, false)">不通过</el-button>
          <el-button v-if="row.auditstatus === 1 && !row.actualreturntime" type="primary" size="small" @click="openReturnDialog(row)">登记回院</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="登记回院" v-model="returnDialogVisible">
      <el-form>
        <el-form-item label="实际回院时间">
          <el-date-picker v-model="actualReturnTime" type="datetime" placeholder="选择时间" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReturn">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 外出登记审核组件
 * 审核外出申请，并为已通过且未回院的客户登记实际回院时间
 */
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const searchName = ref('')
const outwardList = ref([])
const returnDialogVisible = ref(false)
const currentOutward = ref(null)
const actualReturnTime = ref(null)

/** 按客户姓名查询外出申请列表 */
const searchList = async () => {
  const res = await request.get('/outward/list', { params: { name: searchName.value } })
  outwardList.value = res.data
}

/** 审核外出申请 */
const audit = async (row, approved) => {
  await request.post('/outward/audit', { id: row.id, approved })
  ElMessage.success('审核完成')
  searchList()
}

/** 打开回院登记对话框 */
const openReturnDialog = (row) => {
  currentOutward.value = row
  actualReturnTime.value = null
  returnDialogVisible.value = true
}

/** 提交实际回院时间 */
const confirmReturn = async () => {
  if (!actualReturnTime.value) {
    ElMessage.warning('请选择回院时间')
    return
  }
  await request.post('/outward/return', { id: currentOutward.value.id, actualReturnTime: actualReturnTime.value })
  ElMessage.success('回院登记成功')
  returnDialogVisible.value = false
  searchList()
}

onMounted(() => searchList())
</script>

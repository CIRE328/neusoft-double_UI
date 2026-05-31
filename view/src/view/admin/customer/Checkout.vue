<!-- Checkout：退住登记审核，审核健康管家提交的退住申请 -->
<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchList" clearable />
    <el-table :data="backdownList" border style="margin-top: 10px">
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column label="退住类型">
        <template #default="{ row }">{{ ['', '正常退住', '死亡退住', '保留床位'][row.retreattype] || '' }}</template>
      </el-table-column>
      <el-table-column prop="retreatmentreason" label="退住原因" />
      <el-table-column label="审核状态">
        <template #default="{ row }">{{ ['已提交', '通过', '不通过'][row.auditstatus] || '' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button v-if="row.auditstatus === 0" type="success" size="small" @click="audit(row, true)">通过</el-button>
          <el-button v-if="row.auditstatus === 0" type="danger" size="small" @click="audit(row, false)">不通过</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
/**
 * 退住登记组件
 * 查询退住申请列表，并对待审核申请进行通过/不通过操作
 */
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const searchName = ref('')
const backdownList = ref([])

/** 按客户姓名查询退住申请列表 */
const searchList = async () => {
  const res = await request.get('/backdown/list', { params: { name: searchName.value } })
  backdownList.value = res.data
}

/** 审核退住申请 */
const audit = async (row, approved) => {
  await request.post('/backdown/audit', { id: row.id, approved })
  ElMessage.success('审核完成')
  searchList()
}

onMounted(() => searchList())
</script>

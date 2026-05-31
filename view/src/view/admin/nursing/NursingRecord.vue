<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchRecords" clearable />
    <el-table :data="records" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column prop="nursingContent" label="护理内容" />
      <el-table-column prop="nursingCount" label="数量" />
      <el-table-column prop="nursingTime" label="护理时间" />
      <el-table-column prop="nickname" label="护理人员" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="deleteRecord(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const records = ref([])

const searchRecords = async () => {
  const res = await request.get('/nurse/record/list', { params: { name: searchName.value } })
  records.value = res.data
}
const deleteRecord = (row) => {
  ElMessageBox.confirm('删除记录不可恢复，确定吗？', '提示').then(async () => {
    await request.delete('/nurse/record', { params: { id: row.id } })
    ElMessage.success('删除成功')
    searchRecords()
  })
}
onMounted(() => searchRecords())
</script>
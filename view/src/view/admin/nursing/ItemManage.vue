<!-- ItemManage：护理项目管理，增删改查护理项目 -->
<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="项目名称"><el-input v-model="searchName" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchStatus">
          <el-option label="全部" value="" />
          <el-option label="启用" value="1" />
          <el-option label="停用" value="2" />
        </el-select>
      </el-form-item>
      <el-button @click="searchItems">查询</el-button>
      <el-button type="primary" @click="openAddDialog">新增项目</el-button>
    </el-form>
    <el-table :data="items" border>
      <el-table-column prop="serialNumber" label="编号" />
      <el-table-column prop="nursingName" label="名称" />
      <el-table-column prop="servicePrice" label="价格" />
      <el-table-column prop="executionCycle" label="执行周期" />
      <el-table-column prop="executionTime" label="执行次数" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">{{ row.status === 1 ? '启用' : '停用' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="editItem(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteItem(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="编辑项目" v-model="dialogVisible">
      <el-form :model="form" label-width="100px">
        <el-form-item label="编号"><el-input v-model="form.serialNumber" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.nursingName" /></el-form-item>
        <el-form-item label="价格"><el-input v-model="form.servicePrice" /></el-form-item>
        <el-form-item label="执行周期"><el-input v-model="form.executionCycle" /></el-form-item>
        <el-form-item label="执行次数"><el-input v-model="form.executionTime" /></el-form-item>
        <el-form-item label="描述"><el-input type="textarea" v-model="form.message" /></el-form-item>
        <el-form-item label="状态">
          <el-radio v-model="form.status" :label="1">启用</el-radio>
          <el-radio v-model="form.status" :label="2">停用</el-radio>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 护理项目管理组件
 * 支持按名称和状态查询护理项目，并提供新增、编辑、删除功能
 */
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const searchStatus = ref('')
const items = ref([])
const dialogVisible = ref(false)
const form = ref({})
const isEdit = ref(false)

/** 按条件查询护理项目列表 */
const searchItems = async () => {
  const res = await request.get('/nurse/item/list', { params: { name: searchName.value, status: searchStatus.value } })
  items.value = res.data
}

/** 打开新增项目对话框 */
const openAddDialog = () => {
  isEdit.value = false
  form.value = { status: 1 }
  dialogVisible.value = true
}

/** 打开编辑项目对话框 */
const editItem = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

/** 保存护理项目（新增或更新） */
const saveItem = async () => {
  if (isEdit.value) {
    await request.put('/nurse/item', form.value)
  } else {
    await request.post('/nurse/item', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  searchItems()
}

/** 删除护理项目 */
const deleteItem = (row) => {
  ElMessageBox.confirm('确定删除该项目吗？', '提示').then(async () => {
    await request.delete('/nurse/item', { params: { id: row.id } })
    ElMessage.success('删除成功')
    searchItems()
  })
}

onMounted(() => searchItems())
</script>

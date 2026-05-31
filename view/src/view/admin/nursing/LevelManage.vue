<template>
  <div>
    <el-button type="primary" @click="addLevel">新增级别</el-button>
    <el-table :data="levels" border style="margin-top:10px">
      <el-table-column prop="levelName" label="级别名称" />
      <el-table-column label="状态">
        <template #default="{ row }">{{ row.levelStatus === 1 ? '启用' : '停用' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-switch v-model="row.levelStatus" :active-value="1" :inactive-value="2" @change="updateStatus(row)" />
          <el-button type="primary" size="small" @click="configItems(row)">配置项目</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="配置护理项目" v-model="configDialog">
      <div>已选项目：<el-tag v-for="item in selectedItems" :key="item.id" closable @close="removeItem(item)" style="margin:2px">{{ item.nursingName }}</el-tag></div>
      <el-select v-model="addItemId" placeholder="添加项目" clearable>
        <el-option v-for="item in allItems" :key="item.id" :label="item.nursingName" :value="item.id" />
      </el-select>
      <el-button @click="addToLevel">添加</el-button>
    </el-dialog>
    <el-dialog title="新增级别" v-model="addDialog">
      <el-input v-model="newLevelName" placeholder="级别名称" />
      <template #footer>
        <el-button @click="addDialog=false">取消</el-button>
        <el-button type="primary" @click="doAddLevel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const levels = ref([])
const configDialog = ref(false)
const currentLevelId = ref(null)
const selectedItems = ref([])
const allItems = ref([])
const addItemId = ref(null)
const addDialog = ref(false)
const newLevelName = ref('')

const loadLevels = async () => { const res = await request.get('/nurse/level/list'); levels.value = res.data }
const addLevel = () => { addDialog.value = true }
const doAddLevel = async () => {
  await request.post('/nurse/level', { levelName: newLevelName.value, levelStatus: 1 })
  ElMessage.success('添加成功')
  addDialog.value = false
  loadLevels()
}
const updateStatus = async (row) => {
  await request.put('/nurse/level', row)
  ElMessage.success('更新成功')
}
const configItems = async (row) => {
  currentLevelId.value = row.id
  const res = await request.get('/nurse/level/items', { params: { levelId: row.id } })
  selectedItems.value = res.data
  const all = await request.get('/nurse/item/list')
  allItems.value = all.data.filter(i => i.status === 1)
  configDialog.value = true
}
const removeItem = async (item) => {
  await request.delete('/nurse/level/item', { params: { levelId: currentLevelId.value, itemId: item.id } })
  selectedItems.value = selectedItems.value.filter(i => i.id !== item.id)
}
const addToLevel = async () => {
  if (!addItemId.value) return
  await request.post('/nurse/level/item', { levelId: currentLevelId.value, itemId: addItemId.value })
  const added = allItems.value.find(i => i.id === addItemId.value)
  selectedItems.value.push(added)
  addItemId.value = null
}
onMounted(() => loadLevels())
</script>
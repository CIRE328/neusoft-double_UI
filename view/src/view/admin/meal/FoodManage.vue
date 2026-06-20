<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="食品名称">
        <el-input v-model="searchName" placeholder="模糊查询" clearable />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="searchType" clearable>
          <el-option v-for="t in foodTypes" :key="t" :label="t" :value="t" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="loadFoods">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增食品</el-button>
    </el-form>

    <el-table :data="foods" border>
      <el-table-column prop="foodName" label="食品名称" />
      <el-table-column prop="foodType" label="类型" />
      <el-table-column prop="price" label="价格" />
      <el-table-column label="是否清真">
        <template #default="{ row }">{{ row.isHalal === 1 ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button link type="primary" @click="editFood(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteFood(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" v-model="dialogVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="食品名称"><el-input v-model="form.foodName" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="form.foodType" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" /></el-form-item>
        <el-form-item label="是否清真">
          <el-radio-group v-model="form.isHalal">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFood">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const searchType = ref('')
const foods = ref([])
const foodTypes = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({})
const isEdit = ref(false)

const loadFoods = async () => {
  const res = await request.get('/food/list', { params: { name: searchName.value, type: searchType.value } })
  foods.value = res.data
  // 提取所有类型用于下拉
  const types = new Set(foods.value.map(f => f.foodType).filter(Boolean))
  foodTypes.value = Array.from(types)
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { foodName: '', foodType: '', price: 0, isHalal: 0 }
  dialogTitle.value = '新增食品'
  dialogVisible.value = true
}

const editFood = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogTitle.value = '编辑食品'
  dialogVisible.value = true
}

const saveFood = async () => {
  if (isEdit.value) {
    await request.put('/food', form.value)
  } else {
    await request.post('/food', form.value)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadFoods()
}

const deleteFood = (row) => {
  ElMessageBox.confirm('确定删除该食品吗？', '提示').then(async () => {
    await request.delete('/food', { params: { id: row.id } })
    ElMessage.success('删除成功')
    loadFoods()
  })
}

onMounted(() => loadFoods())
</script>
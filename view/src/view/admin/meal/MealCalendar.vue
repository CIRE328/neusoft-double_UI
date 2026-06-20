<template>
  <div>
    <el-form :inline="true">
      <el-form-item label="星期">
        <el-select v-model="weekDay">
          <el-option v-for="(w, i) in weeks" :key="i" :label="w" :value="w" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="loadCalendar">查询</el-button>
      <el-button type="success" @click="openAddDialog">新增安排</el-button>
    </el-form>

    <el-table :data="meals" border>
      <el-table-column label="餐次">
        <template #default="{ row }">{{ ['早餐', '午餐', '晚餐'][row.mealType-1] }}</template>
      </el-table-column>
      <el-table-column prop="foodName" label="食品名称" />
      <el-table-column prop="taste" label="口味" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button link type="primary" @click="editMeal(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteMeal(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" v-model="dialogVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="餐次">
          <el-select v-model="form.mealType">
            <el-option label="早餐" :value="1" />
            <el-option label="午餐" :value="2" />
            <el-option label="晚餐" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="食品">
          <el-select v-model="form.foodId" filterable>
            <el-option v-for="f in foods" :key="f.id" :label="f.foodName" :value="f.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="口味">
          <el-input v-model="form.taste" placeholder="如多糖、少盐" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMeal">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'   // 推荐使用别名
import { ElMessage, ElMessageBox } from 'element-plus'

const weeks = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const weekDay = ref('周一')
const meals = ref([])
const foods = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({})
const isEdit = ref(false)

const loadCalendar = async () => {
  try {
    const res = await request.get('/meal/calendar', { params: { weekDay: weekDay.value } })
    meals.value = res.data
    if (meals.value.length === 0) {
      ElMessage.info('当前星期暂无膳食安排，请点击“新增安排”添加')
    }
  } catch (error) {
    ElMessage.error('加载失败：' + (error.response?.data?.message || error.message))
  }
}

const loadFoods = async () => {
  try {
    const res = await request.get('/food/list')
    foods.value = res.data
  } catch (error) {
    ElMessage.error('加载食品列表失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  form.value = { mealType: 1, foodId: null, taste: '' }
  dialogTitle.value = '新增餐次安排'
  dialogVisible.value = true
}

const editMeal = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogTitle.value = '编辑餐次安排'
  dialogVisible.value = true
}

const saveMeal = async () => {
  const data = { ...form.value, weekDay: weekDay.value }
  try {
    if (isEdit.value) {
      await request.put('/meal', data)
    } else {
      await request.post('/meal', data)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    await loadCalendar()
  } catch (error) {
    ElMessage.error('保存失败：' + (error.response?.data?.message || error.message))
  }
}

const deleteMeal = (row) => {
  ElMessageBox.confirm('确定删除该餐次安排吗？', '提示').then(async () => {
    try {
      await request.delete('/meal', { params: { id: row.id } })
      ElMessage.success('删除成功')
      await loadCalendar()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadFoods()
  loadCalendar()
})
</script>
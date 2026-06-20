<template>
  <el-tabs v-model="activeTab">
    <el-tab-pane label="外出申请" name="outward">
      <el-table :data="outwardList" border>
        <el-table-column prop="customerName" label="客户" />
        <el-table-column prop="outgoingReason" label="事由" />
        <el-table-column prop="outgoingTime" label="外出时间" />
        <el-table-column prop="expectedReturnTime" label="预计返回" />
        <el-table-column label="状态">
          <template #default="{ row }">{{ ['待审核','已通过','已拒绝'][row.auditStatus] }}</template>
        </el-table-column>
      </el-table>
    </el-tab-pane>
    <el-tab-pane label="退住申请" name="backdown">
      <el-table :data="backdownList" border>
        <el-table-column prop="customerName" label="客户" />
        <el-table-column label="退住类型">
          <template #default="{ row }">{{ ['正常','死亡','保留'][row.retreatType] }}</template>
        </el-table-column>
        <el-table-column prop="retreatReason" label="原因" />
        <el-table-column prop="retreatTime" label="退住时间" />
        <el-table-column label="状态">
          <template #default="{ row }">{{ ['待审核','已通过','已拒绝'][row.auditStatus] }}</template>
        </el-table-column>
      </el-table>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { getUserId } from '../../utils/auth'

const activeTab = ref('outward')
const outwardList = ref([])
const backdownList = ref([])

const loadOutward = async () => {
  const userId = getUserId()
  if (!userId) {
    console.warn('未获取到 userId')
    return
  }
  try {
    const res = await request.get('/outward/my', { params: { userId } })
    outwardList.value = res.data
  } catch (error) {
    console.error('加载外出申请失败', error)
  }
}

const loadBackdown = async () => {
  const userId = getUserId()
  if (!userId) return
  try {
    const res = await request.get('/backdown/my', { params: { userId } })
    backdownList.value = res.data
  } catch (error) {
    console.error('加载退住申请失败', error)
  }
}

onMounted(() => {
  loadOutward()
  loadBackdown()
})
</script>
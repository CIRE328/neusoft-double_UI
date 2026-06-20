<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>床位统计</template>
          <div>总床位：{{ bedStats.total }}</div>
          <div>空闲：{{ bedStats.free }}</div>
          <div>有人：{{ bedStats.occupied }}</div>
          <div>外出：{{ bedStats.outward }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>客户统计</template>
          <div>总客户：{{ customerStats.total }}</div>
          <div>自理老人：{{ customerStats.selfCare }}</div>
          <div>护理老人：{{ customerStats.nursingCare }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>护理记录</template>
          <div>总记录数：{{ recordCount }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-divider />
    <h3>最近10条护理记录</h3>
    <el-table :data="recentRecords" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column prop="nursingName" label="护理项目" />
      <el-table-column prop="nursingCount" label="次数" />
      <el-table-column prop="nursingTime" label="护理时间" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'

const bedStats = ref({ total: 0, free: 0, occupied: 0, outward: 0 })
const customerStats = ref({ total: 0, selfCare: 0, nursingCare: 0 })
const recordCount = ref(0)
const recentRecords = ref([])

const loadStatistics = async () => {
  const bed = await request.get('/statistics/bed')
  bedStats.value = bed.data
  const cust = await request.get('/statistics/customer')
  customerStats.value = cust.data
  const rec = await request.get('/statistics/record/count')
  recordCount.value = rec.data
  const records = await request.get('/statistics/record/recent', { params: { limit: 10 } })
  recentRecords.value = records.data
}

onMounted(() => loadStatistics())
</script>
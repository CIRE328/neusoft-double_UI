<template>
  <div>
    <div>总床位数: {{ stats.total }}  空闲: {{ stats.free }}  有人: {{ stats.occupied }}  外出: {{ stats.outward }}</div>
    <el-tabs v-model="activeFloor" @tab-click="loadFloorData">
      <el-tab-pane label="1层" name="1"></el-tab-pane>
      <el-tab-pane label="2层" name="2"></el-tab-pane>
    </el-tabs>
    <div v-for="room in roomsWithBeds" :key="room.roomNo" class="room-card">
      <h3>{{ room.roomNo }}号房间</h3>
      <div class="beds">
        <div v-for="bed in room.beds" :class="['bed', statusClass(bed.bedStatus)]">
          {{ bed.bedNo }} - {{ statusText(bed.bedStatus) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'

const stats = ref({ total: 0, free: 0, occupied: 0, outward: 0 })
const activeFloor = ref('1')
const roomsWithBeds = ref([])

const loadStats = async () => { const res = await request.get('/bed/statistics'); stats.value = res.data }
const loadFloorData = async () => {
  const res = await request.get('/bed/rooms', { params: { floor: activeFloor.value } })
  roomsWithBeds.value = res.data
}
const statusClass = (s) => ({ 1: 'free', 2: 'occupied', 3: 'outward' }[s])
const statusText = (s) => ({ 1: '空闲', 2: '有人', 3: '外出' }[s])
onMounted(() => { loadStats(); loadFloorData() })
</script>

<style scoped>
.room-card { border: 1px solid #eee; margin: 10px; padding: 10px; }
.beds { display: flex; gap: 10px; }
.bed { width: 80px; text-align: center; padding: 5px; border-radius: 5px; }
.free { background: #c8e6c9; }
.occupied { background: #ffcdd2; }
.outward { background: #fff9c4; }
</style>
<template>
  <div>
    <!-- 查询表单 -->
    <el-form :inline="true">
      <el-form-item label="客户姓名">
        <el-input v-model="searchName" placeholder="模糊查询" clearable />
      </el-form-item>
      <el-form-item label="入住日期">
        <el-date-picker v-model="checkinDate" type="date" placeholder="选择日期" />
      </el-form-item>
      <el-form-item label="使用状态">
        <el-select v-model="usageStatus" placeholder="请选择">
          <el-option label="正在使用" value="当前使用" />
          <el-option label="使用历史" value="历史使用" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="searchDetails">查询</el-button>
    </el-form>

    <!-- 床位使用详情列表 -->
    <el-table :data="detailsList" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column prop="bedNo" label="床位号" />
      <el-table-column prop="startDate" label="入住日期" />
      <el-table-column prop="endDate" label="结束日期" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="warning" size="small" @click="openChangeBed(row)">调换床位</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 调换床位对话框 -->
    <el-dialog title="床位调换" v-model="changeDialog" width="400px">
      <el-form>
        <el-form-item label="房间号">
          <el-select v-model="newRoomNo" placeholder="请选择房间" @change="loadFreeBeds">
            <el-option
                v-for="room in rooms"
                :key="room.roomNo"
                :label="room.roomNo"
                :value="room.roomNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号">
          <el-select v-model="newBedId" placeholder="请选择床位">
            <el-option
                v-for="bed in freeBeds"
                :key="bed.id"
                :label="bed.bedNo"
                :value="bed.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialog = false">取消</el-button>
        <el-button type="primary" @click="doChangeBed">确认调换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage } from 'element-plus'

const searchName = ref('')
const checkinDate = ref(null)
const usageStatus = ref('')
const detailsList = ref([])

const changeDialog = ref(false)
const currentDetail = ref(null)
const rooms = ref([])
const freeBeds = ref([])
const newRoomNo = ref('')
const newBedId = ref(null)

// 查询床位使用详情
const searchDetails = async () => {
  try {
    const res = await request.get('/bed/details', {
      params: {
        name: searchName.value,
        checkinDate: checkinDate.value,
        status: usageStatus.value
      }
    })
    detailsList.value = res.data
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

// 打开调换对话框
const openChangeBed = async (row) => {
  currentDetail.value = row
  changeDialog.value = true
  await loadRooms()
}

// 加载所有房间
const loadRooms = async () => {
  const res = await request.get('/room/list')
  rooms.value = res.data
}

// 根据房间号加载空闲床位
const loadFreeBeds = async () => {
  if (!newRoomNo.value) return
  const res = await request.get('/bed/free', { params: { roomNo: newRoomNo.value } })
  freeBeds.value = res.data
}

// 执行调换
const doChangeBed = async () => {
  if (!newBedId.value) {
    ElMessage.warning('请选择床位')
    return
  }
  try {
    await request.post('/bed/change', {
      customerId: currentDetail.value.customerId,
      newBedId: newBedId.value
    })
    ElMessage.success('床位调换成功')
    changeDialog.value = false
    searchDetails() // 刷新列表
  } catch (error) {
    ElMessage.error('调换失败')
  }
}

onMounted(() => {
  searchDetails()
  loadRooms()
})
</script>
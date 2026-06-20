<!-- Checkin：客户入住登记，新增客户并办理入住，支持编辑和删除客户 -->
<template>
  <div>
    <!-- 查询表单 -->
    <el-form :inline="true">
      <el-form-item label="客户姓名">
        <el-input v-model="searchName" placeholder="模糊查询" clearable />
      </el-form-item>
      <el-form-item label="老人类型">
        <el-select v-model="customerType">
          <el-option label="自理老人" value="自理老人" />
          <el-option label="护理老人" value="护理老人" />
        </el-select>
      </el-form-item>
      <el-button type="primary" @click="searchCustomers">查询</el-button>
      <el-button type="success" @click="openAddCustomerDialog">新增客户</el-button>
    </el-form>

    <!-- 客户列表 -->
    <el-table :data="customerList" border>
      <el-table-column prop="customerName" label="姓名" />
      <el-table-column prop="customerAge" label="年龄" />
      <el-table-column prop="idcard" label="身份证号" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-button type="success" size="small" @click="openCheckinDialog(row)">入住登记</el-button>
          <el-button type="danger" size="small" @click="deleteCustomer(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增客户对话框 -->
    <el-dialog title="新增客户" v-model="addCustomerDialogVisible" width="500px">
      <el-form :model="newCustomer" label-width="100px">
        <el-form-item label="姓名" required>
          <el-input v-model="newCustomer.customerName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="newCustomer.customerSex" :label="1">男</el-radio>
          <el-radio v-model="newCustomer.customerSex" :label="0">女</el-radio>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="newCustomer.idcard" />
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker v-model="newCustomer.birthday" type="date" @change="calcAgeForNew" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="newCustomer.customerAge" disabled />
        </el-form-item>
        <el-form-item label="血型">
          <el-input v-model="newCustomer.bloodType" />
        </el-form-item>
        <el-form-item label="家属">
          <el-input v-model="newCustomer.familyMember" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="newCustomer.contactTel" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCustomerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addCustomer">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑客户对话框 -->
    <el-dialog title="编辑客户" v-model="editDialogVisible" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="姓名"><el-input v-model="editForm.customerName" /></el-form-item>
        <el-form-item label="性别">
          <el-radio v-model="editForm.customerSex" :label="1">男</el-radio>
          <el-radio v-model="editForm.customerSex" :label="0">女</el-radio>
        </el-form-item>
        <el-form-item label="身份证号"><el-input v-model="editForm.idcard" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="editForm.contactTel" /></el-form-item>
        <el-form-item label="家属"><el-input v-model="editForm.familyMember" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 入住登记对话框 -->
    <el-dialog title="入住登记" v-model="checkinDialogVisible" width="600px">
      <el-form :model="checkinForm" label-width="100px">
        <el-divider content-position="left">客户信息</el-divider>
        <el-form-item label="姓名">{{ checkinForm.customerName }}</el-form-item>
        <el-form-item label="性别">{{ checkinForm.customerSex === 1 ? '男' : '女' }}</el-form-item>
        <el-form-item label="身份证号">{{ checkinForm.idcard }}</el-form-item>
        <el-form-item label="年龄">{{ checkinForm.customerAge }}</el-form-item>
        <el-form-item label="血型">{{ checkinForm.bloodType }}</el-form-item>
        <el-form-item label="家属">{{ checkinForm.familyMember }}</el-form-item>
        <el-form-item label="联系电话">{{ checkinForm.contactTel }}</el-form-item>

        <el-divider content-position="left">入住信息</el-divider>
        <el-form-item label="楼栋">
          <el-select v-model="checkinForm.buildingNo" disabled>
            <el-option label="606" value="606" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-select v-model="selectedRoomNo" placeholder="请选择房间" @change="onRoomChange">
            <el-option v-for="room in rooms" :key="room.roomNo" :label="room.roomNo" :value="room.roomNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位号">
          <el-select v-model="selectedBedId" placeholder="请选择床位">
            <el-option v-for="bed in freeBeds" :key="bed.id" :label="bed.bedNo" :value="bed.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入住时间">
          <el-date-picker v-model="checkinForm.checkinDate" type="date" />
        </el-form-item>
        <el-form-item label="合同到期时间">
          <el-date-picker v-model="checkinForm.expirationDate" type="date" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkinDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckin">提交入住</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询相关
const searchName = ref('')
const customerType = ref('')
const customerList = ref([])

// 新增客户相关
const addCustomerDialogVisible = ref(false)
const newCustomer = ref({
  customerName: '',
  customerSex: 1,
  idcard: '',
  birthday: null,
  customerAge: 0,
  bloodType: '',
  familyMember: '',
  contactTel: ''
})

// 编辑客户相关
const editDialogVisible = ref(false)
const editForm = ref({})

// 入住登记相关
const checkinDialogVisible = ref(false)
const checkinForm = ref({
  id: null,
  customerName: '',
  customerSex: 1,
  idcard: '',
  customerAge: 0,
  bloodType: '',
  familyMember: '',
  contactTel: '',
  buildingNo: '606',
  checkinDate: new Date(),
  expirationDate: null,
  bedId: null
})
const rooms = ref([])
const freeBeds = ref([])
const selectedRoomNo = ref('')
const selectedBedId = ref(null)

// 查询客户列表
const searchCustomers = async () => {
  try {
    const res = await request.get('/customer/list', {
      params: { name: searchName.value, type: customerType.value }
    })
    customerList.value = res.data
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

// 打开新增客户对话框
const openAddCustomerDialog = () => {
  newCustomer.value = {
    customerName: '',
    customerSex: 1,
    idcard: '',
    birthday: null,
    customerAge: 0,
    bloodType: '',
    familyMember: '',
    contactTel: ''
  }
  addCustomerDialogVisible.value = true
}

// 新增客户
const addCustomer = async () => {
  if (!newCustomer.value.customerName) {
    ElMessage.warning('请填写客户姓名')
    return
  }
  try {
    const res = await request.post('/customer', newCustomer.value)
    if (res.code === 200) {
      ElMessage.success('客户添加成功')
      addCustomerDialogVisible.value = false
      await searchCustomers()
      // 自动打开该客户的入住登记对话框
      openCheckinDialog(res.data)
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加客户失败')
  }
}

// 打开编辑客户对话框
const openEditDialog = (row) => {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

// 保存编辑
const saveEdit = async () => {
  await request.put('/customer', editForm.value)
  ElMessage.success('修改成功')
  editDialogVisible.value = false
  searchCustomers()
}

// 删除客户
const deleteCustomer = (row) => {
  ElMessageBox.confirm('确定删除该客户吗？', '提示').then(async () => {
    await request.delete('/customer', { params: { id: row.id } })
    ElMessage.success('删除成功')
    searchCustomers()
  })
}

// 打开入住登记对话框
const openCheckinDialog = async (customer) => {
  checkinForm.value = {
    id: customer.id,
    customerName: customer.customerName,
    customerSex: customer.customerSex,
    idcard: customer.idcard,
    customerAge: customer.customerAge,
    bloodType: customer.bloodType,
    familyMember: customer.familyMember,
    contactTel: customer.contactTel,
    buildingNo: '606',
    checkinDate: new Date(),
    expirationDate: null,
    bedId: null
  }
  selectedRoomNo.value = ''
  selectedBedId.value = null
  await loadRooms()
  checkinDialogVisible.value = true
}

// 加载房间列表
const loadRooms = async () => {
  const res = await request.get('/room/list')
  rooms.value = res.data
}

// 根据房间号加载空闲床位
const onRoomChange = async (roomNo) => {
  if (!roomNo) return
  const res = await request.get('/bed/free', { params: { roomNo } })
  freeBeds.value = res.data
  selectedBedId.value = null
}

// 提交入住登记
const submitCheckin = async () => {
  if (!selectedBedId.value) {
    ElMessage.warning('请选择床位')
    return
  }
  if (!checkinForm.value.checkinDate || !checkinForm.value.expirationDate) {
    ElMessage.warning('请填写入住时间和合同到期时间')
    return
  }
  if (new Date(checkinForm.value.expirationDate) < new Date(checkinForm.value.checkinDate)) {
    ElMessage.warning('合同到期时间不能小于入住时间')
    return
  }

  const submitData = {
    ...checkinForm.value,
    bedId: selectedBedId.value
  }
  try {
    const res = await request.post('/customer/checkin', submitData)
    if (res.code === 200) {
      ElMessage.success('入住登记成功')
      checkinDialogVisible.value = false
      await searchCustomers()
    } else {
      ElMessage.error(res.message || '入住失败')
    }
  } catch (error) {
    ElMessage.error('入住登记失败')
  }
}

// 年龄计算
const calcAgeForNew = () => {
  if (newCustomer.value.birthday) {
    const birth = new Date(newCustomer.value.birthday)
    const today = new Date()
    let age = today.getFullYear() - birth.getFullYear()
    if (today.getMonth() < birth.getMonth() ||
        (today.getMonth() === birth.getMonth() && today.getDate() < birth.getDate())) {
      age--
    }
    newCustomer.value.customerAge = age
  }
}

onMounted(() => {
  searchCustomers()
  loadRooms()
})
</script>
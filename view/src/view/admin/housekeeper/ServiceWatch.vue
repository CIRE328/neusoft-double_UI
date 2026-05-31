<!-- ServiceWatch：服务关注，管理客户已购护理服务 -->
<template>
  <div>
    <el-input v-model="searchName" placeholder="客户姓名" style="width:200px" @input="searchCustomers" clearable />
    <el-table :data="customers" border>
      <el-table-column prop="customerName" label="客户姓名" />
      <el-table-column label="已购服务" width="400">
        <template #default="{ row }">
          <el-tag v-for="item in row.items" :key="item.id" style="margin:2px" :type="item.statusType">{{ item.nursingName }} ({{ item.nurseNumber }}) {{ item.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="manageService(row)">管理服务</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="服务管理" v-model="serviceDialog" width="600px">
      <div v-if="currentCustomer">
        <h4>现有服务</h4>
        <el-table :data="currentCustomer.items" border>
          <el-table-column prop="nursingName" label="项目名称" />
          <el-table-column prop="nurseNumber" label="剩余次数" />
          <el-table-column prop="maturityTime" label="到期时间" />
          <el-table-column label="操作">
            <template #default="{ row: item }">
              <el-button size="small" @click="renewItem(item)">续费</el-button>
              <el-button size="small" type="danger" @click="removeItem(item)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <h4>添加新服务</h4>
        <el-select v-model="addItemId" placeholder="选择护理项目" @change="onAddItemSelect">
          <el-option v-for="item in availableItems" :key="item.id" :label="item.nursingName" :value="item.id" />
        </el-select>
        <el-button type="primary" @click="addService">添加</el-button>
      </div>
    </el-dialog>
    <el-dialog title="续费" v-model="renewDialog">
      <el-form>
        <el-form-item label="增加次数"><el-input-number v-model="renewCount" :min="1" /></el-form-item>
        <el-form-item label="新的到期时间"><el-date-picker v-model="newMaturity" type="date" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewDialog=false">取消</el-button>
        <el-button type="primary" @click="doRenew">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 服务关注组件
 * 查看客户已购护理服务状态，支持添加、续费与移除服务
 */
import { ref, onMounted } from 'vue'
import request from '../../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const searchName = ref('')
const customers = ref([])
const serviceDialog = ref(false)
const currentCustomer = ref(null)
const addItemId = ref(null)
const availableItems = ref([])
const renewDialog = ref(false)
const renewItemData = ref(null)
const renewCount = ref(1)
const newMaturity = ref(null)

/** 查询客户及其已购服务，并计算服务状态标签 */
const searchCustomers = async () => {
  const res = await request.get('/customer/list', { params: { name: searchName.value } })
  for (let c of res.data) {
    const itemsRes = await request.get('/customer/nurse-items', { params: { customerId: c.id } })
    c.items = itemsRes.data.map(item => {
      const now = new Date()
      const maturity = new Date(item.maturityTime)
      let statusType = 'success'
      let statusText = '正常'
      if (item.nurseNumber <= 0) { statusType = 'danger'; statusText = '欠费' }
      else if (maturity < now) { statusType = 'warning'; statusText = '到期' }
      else { statusType = 'success'; statusText = '未到期' }
      return { ...item, statusType, statusText }
    })
  }
  customers.value = res.data
}

/** 打开服务管理对话框 */
const manageService = (customer) => {
  currentCustomer.value = customer
  serviceDialog.value = true
  loadAvailableItems(customer.id)
}

/** 加载客户尚未拥有的可用护理项目 */
const loadAvailableItems = async (customerId) => {
  const all = await request.get('/nurse/item/list', { params: { status: '1' } })
  const ownedIds = currentCustomer.value.items.map(i => i.itemId)
  availableItems.value = all.data.filter(i => !ownedIds.includes(i.id))
}

const onAddItemSelect = () => {}

/** 为客户添加新护理服务 */
const addService = async () => {
  await request.post('/customer/purchase-item', {
    customerId: currentCustomer.value.id,
    itemId: addItemId.value,
    quantity: 1,
    maturityTime: new Date(new Date().setMonth(new Date().getMonth() + 3))
  })
  ElMessage.success('添加成功')
  serviceDialog.value = false
  searchCustomers()
}

/** 打开续费对话框 */
const renewItem = (item) => {
  renewItemData.value = item
  renewCount.value = 1
  newMaturity.value = null
  renewDialog.value = true
}

/** 提交服务续费 */
const doRenew = async () => {
  await request.post('/customer/renew-item', {
    customerNurseItemId: renewItemData.value.id,
    additionalQuantity: renewCount.value,
    newMaturityTime: newMaturity.value
  })
  ElMessage.success('续费成功')
  renewDialog.value = false
  serviceDialog.value = false
  searchCustomers()
}

/** 移除客户某项护理服务 */
const removeItem = (item) => {
  ElMessageBox.confirm('确定移除该服务吗？', '提示').then(async () => {
    await request.delete('/customer/item', { params: { id: item.id } })
    ElMessage.success('移除成功')
    serviceDialog.value = false
    searchCustomers()
  })
}

onMounted(() => searchCustomers())
</script>

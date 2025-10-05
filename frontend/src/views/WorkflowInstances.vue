<template>
  <div class="workflow-instances-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>工作流实例</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="业务标题">
          <el-input v-model="searchForm.businessTitle" placeholder="请输入业务标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="运行中" value="RUNNING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="错误" value="ERROR" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="instanceList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="实例ID" width="80" />
        <el-table-column prop="workflowName" label="工作流名称" width="150" />
        <el-table-column prop="businessTitle" label="业务标题" min-width="200" />
        <el-table-column prop="businessType" label="业务类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.businessType === 'CONTENT'" type="primary">内容审批</el-tag>
            <el-tag v-else-if="row.businessType === 'USER'" type="success">用户审批</el-tag>
            <el-tag v-else type="info">{{ row.businessType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'RUNNING'" type="primary">运行中</el-tag>
            <el-tag v-else-if="row.status === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
            <el-tag v-else-if="row.status === 'CANCELLED'" type="info">已取消</el-tag>
            <el-tag v-else-if="row.status === 'ERROR'" type="danger">错误</el-tag>
            <el-tag v-else type="warning">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="initiatedAt" label="发起时间" width="180" />
        <el-table-column prop="completedAt" label="完成时间" width="180">
          <template #default="{ row }">
            {{ row.completedAt || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="info" link @click="handleViewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 'RUNNING'" 
              type="danger" 
              link 
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="pagination.page + 1"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="实例详情" width="700px">
      <el-descriptions :column="2" border v-if="currentInstance">
        <el-descriptions-item label="实例ID">{{ currentInstance.id }}</el-descriptions-item>
        <el-descriptions-item label="工作流名称">{{ currentInstance.workflowName }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ currentInstance.businessType }}</el-descriptions-item>
        <el-descriptions-item label="业务ID">{{ currentInstance.businessId }}</el-descriptions-item>
        <el-descriptions-item label="业务标题" :span="2">{{ currentInstance.businessTitle }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentInstance.status === 'RUNNING'" type="primary">运行中</el-tag>
          <el-tag v-else-if="currentInstance.status === 'APPROVED'" type="success">已通过</el-tag>
          <el-tag v-else-if="currentInstance.status === 'REJECTED'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="currentInstance.status === 'CANCELLED'" type="info">已取消</el-tag>
          <el-tag v-else type="warning">{{ currentInstance.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="当前节点ID">{{ currentInstance.currentNodeId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发起人ID">{{ currentInstance.initiatorId }}</el-descriptions-item>
        <el-descriptions-item label="发起时间">{{ currentInstance.initiatedAt }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" :span="2">{{ currentInstance.completedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成备注" :span="2">{{ currentInstance.completionNote || '-' }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getInstancesApi, cancelInstanceApi } from '@/api/workflow'

// 搜索表单
const searchForm = reactive({
  businessTitle: '',
  status: ''
})

// 实例列表
const instanceList = ref<any[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 0,  // 从0开始，匹配后端分页
  size: 10,
  total: 0
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentInstance = ref<any>(null)

// 加载实例列表
const loadInstances = async () => {
  loading.value = true
  console.log('🔍 开始加载工作流实例，参数:', {
    page: pagination.page,
    size: pagination.size,
    ...searchForm
  })

  try {
    const response = await getInstancesApi({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    console.log('✅ 工作流实例响应:', response)

    if (response.code === 200 && response.data) {
      instanceList.value = Array.isArray(response.data.content) ? response.data.content : []
      pagination.total = response.data.totalElements || 0
      console.log(`✅ 加载成功: ${instanceList.value.length}条数据，总数: ${pagination.total}`)
    } else {
      instanceList.value = []
      pagination.total = 0
      ElMessage.error(response.message || '加载实例列表失败')
    }
  } catch (error: any) {
    console.error('❌ 加载实例列表失败:', error)
    instanceList.value = []
    pagination.total = 0
    ElMessage.error(error.message || '加载实例列表失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  pagination.page = page - 1  // Element Plus从1开始，转换为从0开始
  loadInstances()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 0  // 改变每页大小时重置到第一页
  loadInstances()
}

// 搜索
const handleSearch = () => {
  pagination.page = 0  // 搜索时重置到第一页
  loadInstances()
}

// 重置
const handleReset = () => {
  searchForm.businessTitle = ''
  searchForm.status = ''
  pagination.page = 0  // 重置时回到第一页
  loadInstances()
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentInstance.value = row
  detailDialogVisible.value = true
}

// 取消实例
const handleCancel = (row: any) => {
  ElMessageBox.confirm('确定要取消该工作流实例吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelInstanceApi(row.id)
      ElMessage.success('取消成功')
      loadInstances()
    } catch (error: any) {
      console.error('取消失败:', error)
      ElMessage.error(error.message || '取消失败')
    }
  }).catch(() => {
    // 取消操作
  })
}

// 初始化
onMounted(() => {
  loadInstances()
})
</script>

<style scoped>
.workflow-instances-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>


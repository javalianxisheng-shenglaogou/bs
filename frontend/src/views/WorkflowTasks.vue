<template>
  <div class="workflow-tasks-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ activeTab === 'pending' ? '我的待办' : '我的已办' }}</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待办任务" name="pending">
          <el-table :data="pendingTasks" v-loading="loading" border stripe>
            <el-table-column prop="id" label="任务ID" width="80" />
            <el-table-column prop="taskName" label="任务名称" width="150" />
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
                <el-tag v-if="row.status === 'PENDING'" type="warning">待处理</el-tag>
                <el-tag v-else-if="row.status === 'IN_PROGRESS'" type="primary">处理中</el-tag>
                <el-tag v-else type="info">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleApprove(row)">通过</el-button>
                <el-button type="danger" link @click="handleReject(row)">拒绝</el-button>
                <el-button type="info" link @click="handleViewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            :current-page="pendingPage + 1"
            v-model:page-size="pendingPageSize"
            :total="pendingTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handlePendingSizeChange"
            @current-change="handlePendingPageChange"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>

        <el-tab-pane label="已办任务" name="completed">
          <el-table :data="completedTasks" v-loading="loading" border stripe>
            <el-table-column prop="id" label="任务ID" width="80" />
            <el-table-column prop="taskName" label="任务名称" width="150" />
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
                <el-tag v-if="row.status === 'APPROVED'" type="success">已通过</el-tag>
                <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已拒绝</el-tag>
                <el-tag v-else-if="row.status === 'CANCELLED'" type="info">已取消</el-tag>
                <el-tag v-else type="warning">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="action" label="操作" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.action === 'APPROVE'" type="success">通过</el-tag>
                <el-tag v-else-if="row.action === 'REJECT'" type="danger">拒绝</el-tag>
                <el-tag v-else type="info">{{ row.action || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="processedAt" label="处理时间" width="180" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="info" link @click="handleViewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            :current-page="completedPage + 1"
            v-model:page-size="completedPageSize"
            :total="completedTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleCompletedSizeChange"
            @current-change="handleCompletedPageChange"
            style="margin-top: 20px; justify-content: flex-end"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      :title="approveAction === 'approve' ? '审批通过' : '审批拒绝'"
      width="500px"
    >
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="任务名称">
          <span>{{ currentTask?.taskName }}</span>
        </el-form-item>
        <el-form-item label="业务标题">
          <span>{{ currentTask?.businessTitle }}</span>
        </el-form-item>
        <el-form-item label="审批意见" required>
          <el-input
            v-model="approveForm.comment"
            type="textarea"
            :rows="4"
            :placeholder="approveAction === 'approve' ? '请输入审批意见（可选）' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmApprove"
          :loading="submitting"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="任务详情" width="600px">
      <el-descriptions :column="1" border v-if="currentTask">
        <el-descriptions-item label="任务ID">{{ currentTask.id }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ currentTask.businessType }}</el-descriptions-item>
        <el-descriptions-item label="业务标题">{{ currentTask.businessTitle }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentTask.status === 'PENDING'" type="warning">待处理</el-tag>
          <el-tag v-else-if="currentTask.status === 'APPROVED'" type="success">已通过</el-tag>
          <el-tag v-else-if="currentTask.status === 'REJECTED'" type="danger">已拒绝</el-tag>
          <el-tag v-else type="info">{{ currentTask.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentTask.assigneeName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ currentTask.processedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见">{{ currentTask.comment || '-' }}</el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyPendingTasksApi, getMyCompletedTasksApi, approveTaskApi, rejectTaskApi } from '@/api/workflow'

// 标签页
const activeTab = ref('pending')

// 待办任务
const pendingTasks = ref<any[]>([])
const pendingPage = ref(0)  // 从0开始，匹配后端分页
const pendingPageSize = ref(10)
const pendingTotal = ref(0)

// 已办任务
const completedTasks = ref<any[]>([])
const completedPage = ref(0)  // 从0开始，匹配后端分页
const completedPageSize = ref(10)
const completedTotal = ref(0)

// 加载状态
const loading = ref(false)

// 审批对话框
const approveDialogVisible = ref(false)
const approveAction = ref<'approve' | 'reject'>('approve')
const approveForm = ref({
  comment: ''
})
const currentTask = ref<any>(null)
const submitting = ref(false)

// 详情对话框
const detailDialogVisible = ref(false)

// 加载待办任务
const loadPendingTasks = async () => {
  loading.value = true
  console.log('🔍 开始加载待办任务，参数:', {
    page: pendingPage.value,
    size: pendingPageSize.value
  })

  try {
    const response = await getMyPendingTasksApi({
      page: pendingPage.value,
      size: pendingPageSize.value
    })
    console.log('✅ 待办任务响应:', response)

    if (response.code === 200 && response.data) {
      pendingTasks.value = Array.isArray(response.data.content) ? response.data.content : []
      pendingTotal.value = response.data.totalElements || 0
      console.log('✅ 待办任务加载成功:', {
        total: pendingTotal.value,
        count: pendingTasks.value.length
      })
    } else {
      pendingTasks.value = []
      pendingTotal.value = 0
      ElMessage.error(response.message || '加载待办任务失败')
    }
  } catch (error: any) {
    console.error('❌ 加载待办任务失败:', error)
    pendingTasks.value = []
    pendingTotal.value = 0
    ElMessage.error(error.message || '加载待办任务失败')
  } finally {
    loading.value = false
  }
}

// 加载已办任务
const loadCompletedTasks = async () => {
  loading.value = true
  console.log('🔍 开始加载已办任务，参数:', {
    page: completedPage.value,
    size: completedPageSize.value
  })

  try {
    const response = await getMyCompletedTasksApi({
      page: completedPage.value,
      size: completedPageSize.value
    })
    console.log('✅ 已办任务响应:', response)

    if (response.code === 200 && response.data) {
      completedTasks.value = Array.isArray(response.data.content) ? response.data.content : []
      completedTotal.value = response.data.totalElements || 0
      console.log('✅ 已办任务加载成功:', {
        total: completedTotal.value,
        count: completedTasks.value.length
      })
    } else {
      completedTasks.value = []
      completedTotal.value = 0
      ElMessage.error(response.message || '加载已办任务失败')
    }
  } catch (error: any) {
    console.error('❌ 加载已办任务失败:', error)
    completedTasks.value = []
    completedTotal.value = 0
    ElMessage.error(error.message || '加载已办任务失败')
  } finally {
    loading.value = false
  }
}

// 待办任务分页处理
const handlePendingPageChange = (page: number) => {
  pendingPage.value = page - 1  // Element Plus从1开始，转换为从0开始
  loadPendingTasks()
}

const handlePendingSizeChange = (size: number) => {
  pendingPageSize.value = size
  pendingPage.value = 0  // 改变每页大小时重置到第一页
  loadPendingTasks()
}

// 已办任务分页处理
const handleCompletedPageChange = (page: number) => {
  completedPage.value = page - 1  // Element Plus从1开始，转换为从0开始
  loadCompletedTasks()
}

const handleCompletedSizeChange = (size: number) => {
  completedPageSize.value = size
  completedPage.value = 0  // 改变每页大小时重置到第一页
  loadCompletedTasks()
}

// 标签页切换
const handleTabChange = (tab: string) => {
  if (tab === 'pending') {
    loadPendingTasks()
  } else {
    loadCompletedTasks()
  }
}

// 通过
const handleApprove = (row: any) => {
  currentTask.value = row
  approveAction.value = 'approve'
  approveForm.value.comment = ''
  approveDialogVisible.value = true
}

// 拒绝
const handleReject = (row: any) => {
  currentTask.value = row
  approveAction.value = 'reject'
  approveForm.value.comment = ''
  approveDialogVisible.value = true
}

// 确认审批
const confirmApprove = async () => {
  if (approveAction.value === 'reject' && !approveForm.value.comment) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  submitting.value = true
  try {
    if (approveAction.value === 'approve') {
      await approveTaskApi(currentTask.value.id, approveForm.value)
      ElMessage.success('审批通过成功')
    } else {
      await rejectTaskApi(currentTask.value.id, approveForm.value)
      ElMessage.success('审批拒绝成功')
    }
    approveDialogVisible.value = false
    loadPendingTasks()
  } catch (error: any) {
    console.error('审批失败:', error)
    ElMessage.error(error.message || '审批失败')
  } finally {
    submitting.value = false
  }
}

// 查看详情
const handleViewDetail = (row: any) => {
  currentTask.value = row
  detailDialogVisible.value = true
}

// 初始化
onMounted(() => {
  loadPendingTasks()
})
</script>

<style scoped>
.workflow-tasks-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>


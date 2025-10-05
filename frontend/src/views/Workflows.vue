<template>
  <div class="workflows-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>工作流管理</span>
          <el-button type="primary" @click="handleCreate">新建工作流</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="工作流名称">
          <el-input v-model="searchForm.name" placeholder="请输入工作流名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="激活" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="workflowList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="工作流名称" min-width="150" />
        <el-table-column prop="code" label="工作流代码" width="150" />
        <el-table-column prop="workflowType" label="类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.workflowType === 'CONTENT_APPROVAL'" type="primary">内容审批</el-tag>
            <el-tag v-else-if="row.workflowType === 'USER_APPROVAL'" type="success">用户审批</el-tag>
            <el-tag v-else type="info">自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">激活</el-tag>
            <el-tag v-else-if="row.status === 'INACTIVE'" type="warning">停用</el-tag>
            <el-tag v-else type="info">草稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleDesign(row)">设计</el-button>
            <el-button 
              v-if="row.status === 'ACTIVE'" 
              type="warning" 
              link 
              @click="handleToggleStatus(row, 'INACTIVE')"
            >
              停用
            </el-button>
            <el-button 
              v-else 
              type="success" 
              link 
              @click="handleToggleStatus(row, 'ACTIVE')"
            >
              激活
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="工作流名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入工作流名称" />
        </el-form-item>
        <el-form-item label="工作流代码" prop="code">
          <el-input v-model="form.code" placeholder="请输入工作流代码（唯一）" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="工作流类型" prop="workflowType">
          <el-select v-model="form.workflowType" placeholder="请选择工作流类型">
            <el-option label="内容审批" value="CONTENT_APPROVAL" />
            <el-option label="用户审批" value="USER_APPROVAL" />
            <el-option label="自定义" value="CUSTOM" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发事件" prop="triggerEvent">
          <el-input v-model="form.triggerEvent" placeholder="请输入触发事件" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="激活" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入工作流描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getWorkflowsApi,
  createWorkflowApi,
  updateWorkflowApi,
  deleteWorkflowApi,
  updateWorkflowStatusApi
} from '@/api/workflow'

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 工作流列表
const workflowList = ref<any[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 0,  // 从0开始，匹配后端分页
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新建工作流')
const isEdit = ref(false)
const submitting = ref(false)

// 表单
const formRef = ref<FormInstance>()
const form = reactive({
  id: null as number | null,
  name: '',
  code: '',
  workflowType: '',
  triggerEvent: '',
  status: 'DRAFT',
  description: ''
})

// 表单验证规则
const rules: FormRules = {
  name: [{ required: true, message: '请输入工作流名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入工作流代码', trigger: 'blur' }],
  workflowType: [{ required: true, message: '请选择工作流类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 加载工作流列表
const loadWorkflows = async () => {
  loading.value = true
  console.log('🔍 开始加载工作流列表，参数:', {
    page: pagination.page,
    size: pagination.size,
    ...searchForm
  })

  try {
    const response = await getWorkflowsApi({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    console.log('✅ 工作流列表响应:', response)

    if (response.code === 200 && response.data) {
      workflowList.value = Array.isArray(response.data.content) ? response.data.content : []
      pagination.total = response.data.totalElements || 0
    } else {
      workflowList.value = []
      pagination.total = 0
      ElMessage.error(response.message || '加载工作流列表失败')
    }
  } catch (error: any) {
    console.error('❌ 加载工作流列表失败:', error)
    workflowList.value = []
    pagination.total = 0
    ElMessage.error(error.message || '加载工作流列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 0  // 重置到第一页（从0开始）
  loadWorkflows()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.page = 0  // 重置到第一页（从0开始）
  loadWorkflows()
}

// 分页改变
const handlePageChange = (page: number) => {
  pagination.page = page - 1  // Element Plus从1开始，转换为从0开始
  loadWorkflows()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 0  // 重置到第一页
  loadWorkflows()
}

// 新建
const handleCreate = () => {
  dialogTitle.value = '新建工作流'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑工作流'
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name,
    code: row.code,
    workflowType: row.workflowType,
    triggerEvent: row.triggerEvent,
    status: row.status,
    description: row.description
  })
  dialogVisible.value = true
}

// 设计工作流
const handleDesign = (row: any) => {
  router.push(`/workflows/${row.id}/design`)
}

// 切换状态
const handleToggleStatus = async (row: any, status: string) => {
  try {
    await updateWorkflowStatusApi(row.id, status)
    ElMessage.success('状态更新成功')
    loadWorkflows()
  } catch (error: any) {
    console.error('更新状态失败:', error)
    ElMessage.error(error.message || '更新状态失败')
  }
}

// 删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该工作流吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteWorkflowApi(row.id)
      ElMessage.success('删除成功')
      loadWorkflows()
    } catch (error: any) {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      if (isEdit.value && form.id) {
        await updateWorkflowApi(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createWorkflowApi(form)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadWorkflows()
    } catch (error: any) {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '提交失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.name = ''
  form.code = ''
  form.workflowType = ''
  form.triggerEvent = ''
  form.status = 'DRAFT'
  form.description = ''
  formRef.value?.clearValidate()
}

// 对话框关闭
const handleDialogClose = () => {
  resetForm()
}

// 初始化
onMounted(() => {
  loadWorkflows()
})
</script>

<style scoped>
.workflows-container {
  padding: 20px;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.search-form {
  margin-bottom: 20px;
}

/* 表格优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

:deep(.el-table__header th) {
  background: transparent;
  color: white;
  font-weight: 600;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
  transform: scale(1.01);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 标签优化 */
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

/* 分页优化 */
:deep(.el-pagination) {
  display: flex;
  justify-content: flex-end;
}
</style>


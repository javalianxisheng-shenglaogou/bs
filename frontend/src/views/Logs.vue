<template>
  <div class="logs">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统日志</span>
          <div class="header-actions">
            <el-button type="danger" @click="handleClear" :disabled="selectedIds.length === 0">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="warning" @click="showClearDialog = true">
              <el-icon><Delete /></el-icon>
              清空日志
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="模块">
            <el-select v-model="searchForm.module" placeholder="请选择模块" clearable style="width: 120px">
              <el-option label="认证" value="auth" />
              <el-option label="站点" value="site" />
              <el-option label="内容" value="content" />
              <el-option label="分类" value="category" />
              <el-option label="用户" value="user" />
              <el-option label="文件" value="file" />
            </el-select>
          </el-form-item>
          <el-form-item label="级别">
            <el-select v-model="searchForm.level" placeholder="请选择级别" clearable style="width: 120px">
              <el-option label="INFO" value="INFO" />
              <el-option label="WARN" value="WARN" />
              <el-option label="ERROR" value="ERROR" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.isSuccess" placeholder="请选择状态" clearable style="width: 120px">
              <el-option label="成功" :value="true" />
              <el-option label="失败" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 360px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 日志表格 -->
      <el-table
        :data="logList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag :type="getModuleType(row.module)" size="small">{{ getModuleName(row.module) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="executionTime" label="执行时间" width="100">
          <template #default="{ row }">
            {{ row.executionTime }}ms
          </template>
        </el-table-column>
        <el-table-column prop="isSuccess" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isSuccess ? 'success' : 'danger'" size="small">
              {{ row.isSuccess ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="日志详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ getModuleName(currentLog.module) }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ currentLog.action }}</el-descriptions-item>
        <el-descriptions-item label="级别">
          <el-tag :type="getLevelType(currentLog.level)" size="small">{{ currentLog.level }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentLog.isSuccess ? 'success' : 'danger'" size="small">
            {{ currentLog.isSuccess ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentLog.description }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ currentLog.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="执行时间">{{ currentLog.executionTime }}ms</el-descriptions-item>
        <el-descriptions-item label="浏览器" :span="2">{{ currentLog.browser }}</el-descriptions-item>
        <el-descriptions-item label="操作系统" :span="2">{{ currentLog.operatingSystem }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre>{{ currentLog.requestParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <pre>{{ currentLog.responseResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="currentLog.errorMessage">
          <pre class="error-message">{{ currentLog.errorMessage }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ currentLog.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 清空日志对话框 -->
    <el-dialog v-model="showClearDialog" title="清空日志" width="500px">
      <el-form :model="clearForm" label-width="100px">
        <el-form-item label="日志级别">
          <el-select v-model="clearForm.level" placeholder="请选择级别(可选)" clearable>
            <el-option label="INFO" value="INFO" />
            <el-option label="WARN" value="WARN" />
            <el-option label="ERROR" value="ERROR" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="clearDateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-alert
          title="警告：清空操作不可恢复，请谨慎操作！"
          type="warning"
          :closable="false"
          show-icon
        />
      </el-form>
      <template #footer>
        <el-button @click="showClearDialog = false">取消</el-button>
        <el-button type="danger" @click="handleConfirmClear">确定清空</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { getLogsApi, deleteLogsApi, clearLogsApi, type SystemLog } from '@/api/log'

// 响应式数据
const loading = ref(false)
const logList = ref<SystemLog[]>([])
const selectedIds = ref<number[]>([])
const detailDialogVisible = ref(false)
const showClearDialog = ref(false)
const currentLog = ref<SystemLog>({} as SystemLog)
const dateRange = ref<string[]>([])
const clearDateRange = ref<string[]>([])

// 搜索表单
const searchForm = reactive({
  username: '',
  module: '',
  level: '',
  isSuccess: undefined as boolean | undefined
})

// 清空表单
const clearForm = reactive({
  level: '',
  startTime: '',
  endTime: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 加载日志列表
const loadLogs = async () => {
  loading.value = true
  try {
    const params: any = {
      ...searchForm,
      page: pagination.page,
      size: pagination.size
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }

    const data = await getLogsApi(params)
    logList.value = data.content
    pagination.total = data.total
  } catch (error: any) {
    console.error('加载日志列表失败:', error)
    ElMessage.error(error.message || '加载日志列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadLogs()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.module = ''
  searchForm.level = ''
  searchForm.isSuccess = undefined
  dateRange.value = []
  pagination.page = 1
  loadLogs()
}

// 查看详情
const handleView = (log: SystemLog) => {
  currentLog.value = log
  detailDialogVisible.value = true
}

// 选择变化
const handleSelectionChange = (selection: SystemLog[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(id => id !== undefined)
}

// 批量删除
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的日志吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteLogsApi(selectedIds.value)
    ElMessage.success('删除成功')
    await loadLogs()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除日志失败:', error)
      ElMessage.error(error.message || '删除日志失败')
    }
  }
}

// 确认清空
const handleConfirmClear = async () => {
  try {
    const params: any = {
      level: clearForm.level
    }

    if (clearDateRange.value && clearDateRange.value.length === 2) {
      params.startTime = clearDateRange.value[0]
      params.endTime = clearDateRange.value[1]
    }

    await clearLogsApi(params)
    ElMessage.success('清空成功')
    showClearDialog.value = false
    await loadLogs()
  } catch (error: any) {
    console.error('清空日志失败:', error)
    ElMessage.error(error.message || '清空日志失败')
  }
}

// 分页变化
const handlePageChange = (page: number) => {
  pagination.page = page
  loadLogs()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  loadLogs()
}

// 获取模块名称
const getModuleName = (module: string) => {
  const moduleMap: Record<string, string> = {
    auth: '认证',
    site: '站点',
    content: '内容',
    category: '分类',
    user: '用户',
    file: '文件'
  }
  return moduleMap[module] || module
}

// 获取模块类型
const getModuleType = (module: string) => {
  const typeMap: Record<string, string> = {
    auth: 'success',
    site: 'primary',
    content: 'warning',
    category: 'info',
    user: 'danger',
    file: ''
  }
  return typeMap[module] || ''
}

// 获取级别类型
const getLevelType = (level: string) => {
  const typeMap: Record<string, string> = {
    INFO: 'info',
    WARN: 'warning',
    ERROR: 'danger'
  }
  return typeMap[level] || 'info'
}

// 组件挂载时加载数据
onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
  font-size: 12px;
  line-height: 1.5;
}

.error-message {
  color: #f56c6c;
  background-color: #fef0f0;
}
</style>


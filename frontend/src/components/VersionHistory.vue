<template>
  <el-drawer
    v-model="visible"
    :title="t('content.version.title')"
    direction="rtl"
    size="80%"
    :before-close="handleClose"
    class="version-drawer"
  >
    <div class="version-history-container">
      <!-- 内容信息 -->
      <el-card class="content-info-card" shadow="never">
        <div class="content-info">
          <h3>{{ contentInfo?.title }}</h3>
          <div class="content-meta">
            <el-tag type="info">ID: {{ contentInfo?.id }}</el-tag>
            <el-tag :type="getStatusType(contentInfo?.status)">
              {{ t(`content.status.${contentInfo?.status?.toLowerCase()}`) }}
            </el-tag>
            <span class="author">{{ t('content.fields.author') }}: {{ contentInfo?.authorName }}</span>
          </div>
        </div>
      </el-card>

      <!-- 版本列表 -->
      <el-card class="version-list-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>{{ t('content.version.history') }}</span>
            <div class="header-actions">
              <el-button
                type="primary"
                :disabled="selectedVersions.length !== 2"
                @click="handleCompareVersions"
                size="small"
              >
                <el-icon><DocumentCopy /></el-icon>
                {{ t('content.version.actions.compareSelected') }}
              </el-button>
              <el-button @click="loadVersions" size="small">
                <el-icon><Refresh /></el-icon>
                {{ t('common.button.refresh') }}
              </el-button>
            </div>
          </div>
        </template>

        <el-table 
          :data="versions" 
          v-loading="loading"
          @selection-change="handleSelectionChange"
          class="version-table"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="versionNumber" :label="t('content.version.fields.versionNumber')" width="100">
            <template #default="{ row }">
              <el-tag type="primary" size="small">v{{ row.versionNumber }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="changeType" :label="t('content.version.fields.changeType')" width="120">
            <template #default="{ row }">
              <el-tag :type="getChangeTypeColor(row.changeType)" size="small">
                {{ t(`content.version.changeTypes.${row.changeType}`) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="changeSummary" :label="t('content.version.fields.changeSummary')" min-width="200" show-overflow-tooltip />
          <el-table-column prop="createdBy" :label="t('content.version.fields.createdBy')" width="120" />
          <el-table-column prop="createdAt" :label="t('content.version.fields.createdAt')" width="180" />
          <el-table-column prop="tagName" :label="t('content.version.fields.tagName')" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.tagName" type="warning" size="small">{{ row.tagName }}</el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column :label="t('common.button.actions')" width="320" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" size="small" @click="viewVersion(row)" class="action-btn">
                  <el-icon><View /></el-icon>
                  {{ t('content.version.actions.view') }}
                </el-button>
                <el-button type="success" size="small" @click="restoreVersion(row)" class="action-btn">
                  <el-icon><RefreshRight /></el-icon>
                  {{ t('content.version.actions.restore') }}
                </el-button>
                <el-button type="warning" size="small" @click="tagVersion(row)" class="action-btn">
                  <el-icon><PriceTag /></el-icon>
                  {{ t('content.version.actions.tag') }}
                </el-button>
                <el-button
                  v-if="userStore.isAdmin()"
                  type="danger"
                  size="small"
                  @click="deleteVersion(row)"
                  class="action-btn"
                >
                  <el-icon><Delete /></el-icon>
                  {{ t('content.version.actions.delete') }}
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 版本详情对话框 -->
    <el-dialog
      v-model="versionDetailVisible"
      :title="t('content.version.dialog.versionDetail')"
      width="60%"
      class="version-detail-dialog"
    >
      <div v-if="currentVersion" class="version-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="t('content.version.fields.versionNumber')">
            <el-tag type="primary">v{{ currentVersion.versionNumber }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="t('content.version.fields.changeType')">
            <el-tag :type="getChangeTypeColor(currentVersion.changeType)">
              {{ t(`content.version.changeTypes.${currentVersion.changeType}`) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item :label="t('content.version.fields.createdBy')">
            {{ currentVersion.createdBy }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('content.version.fields.createdAt')">
            {{ currentVersion.createdAt }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('content.version.fields.changeSummary')" :span="2">
            {{ currentVersion.changeSummary }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="version-content">
          <h4>{{ t('content.fields.content') }}</h4>
          <div class="content-preview" v-html="currentVersion.content"></div>
        </div>
      </div>
    </el-dialog>

    <!-- 版本对比对话框 -->
    <el-dialog
      v-model="compareDialogVisible"
      :title="t('content.version.dialog.versionCompare')"
      width="90%"
      class="version-compare-dialog"
    >
      <div v-if="compareVersions.length === 2" class="version-compare">
        <div class="compare-header">
          <div class="version-info">
            <h4>{{ t('content.version.dialog.versionA', { version: compareVersions[0].versionNumber }) }}</h4>
            <p>{{ compareVersions[0].createdAt }} - {{ compareVersions[0].createdBy }}</p>
          </div>
          <div class="version-info">
            <h4>{{ t('content.version.dialog.versionB', { version: compareVersions[1].versionNumber }) }}</h4>
            <p>{{ compareVersions[1].createdAt }} - {{ compareVersions[1].createdBy }}</p>
          </div>
        </div>
        
        <div class="compare-content">
          <div class="version-content-side">
            <div class="content-preview" v-html="compareVersions[0].content"></div>
          </div>
          <div class="version-content-side">
            <div class="content-preview" v-html="compareVersions[1].content"></div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 版本标记对话框 -->
    <el-dialog
      v-model="tagDialogVisible"
      :title="t('content.version.dialog.tagVersion')"
      width="400px"
    >
      <el-form :model="tagForm" label-width="80px">
        <el-form-item :label="t('content.version.fields.tagName')">
          <el-input 
            v-model="tagForm.tagName" 
            :placeholder="t('content.version.dialog.tagPlaceholder')"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="tagDialogVisible = false">{{ t('common.button.cancel') }}</el-button>
          <el-button type="primary" @click="confirmTag">{{ t('common.button.confirm') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DocumentCopy,
  Refresh,
  View,
  RefreshRight,
  PriceTag,
  Delete
} from '@element-plus/icons-vue'
import {
  getVersionListApi,
  getVersionApi,
  restoreVersionApi,
  compareVersionsApi,
  tagVersionApi,
  deleteVersionApi
} from '@/api/content'

// 接口定义
interface Content {
  id: number
  title: string
  status: string
  authorName: string
}

interface Version {
  id: number
  versionNumber: number
  changeType: string
  changeSummary: string
  createdBy: string
  createdAt: string
  tagName?: string
  content: string
}

// Props
interface Props {
  modelValue: boolean
  content: Content | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

// Composables
const { t } = useI18n()
const userStore = useUserStore()

// 响应式数据
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const contentInfo = computed(() => props.content)
const loading = ref(false)
const versions = ref<Version[]>([])
const selectedVersions = ref<Version[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框状态
const versionDetailVisible = ref(false)
const compareDialogVisible = ref(false)
const tagDialogVisible = ref(false)

// 当前操作的版本
const currentVersion = ref<Version | null>(null)
const compareVersions = ref<Version[]>([])
const tagForm = ref({ tagName: '' })
const currentTagVersion = ref<Version | null>(null)

// 监听内容变化，重新加载版本
watch(() => props.content, (newContent) => {
  if (newContent && visible.value) {
    loadVersions()
  }
})

// 监听抽屉显示状态
watch(visible, (newVisible) => {
  if (newVisible && props.content) {
    loadVersions()
  }
})

// 方法
const handleClose = () => {
  visible.value = false
  selectedVersions.value = []
  currentVersion.value = null
  compareVersions.value = []
}

const loadVersions = async () => {
  if (!props.content) return

  loading.value = true
  try {
    // 调用真实API获取版本列表
    const response = await getVersionListApi(props.content.id, currentPage.value - 1, pageSize.value)

    if (response.code === 200 && response.data) {
      versions.value = response.data.content || []
      total.value = response.data.totalElements || 0
      console.log('✅ 版本列表加载成功:', versions.value.length, '个版本')
    } else {
      versions.value = []
      total.value = 0
      ElMessage.error(response.message || t('content.version.messages.loadFailed'))
    }
  } catch (error: any) {
    console.error('❌ 加载版本列表失败:', error)
    versions.value = []
    total.value = 0
    ElMessage.error(error.message || t('content.version.messages.loadFailed'))
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection: Version[]) => {
  selectedVersions.value = selection
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadVersions()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadVersions()
}

const viewVersion = (version: Version) => {
  currentVersion.value = version
  versionDetailVisible.value = true
}

const handleCompareVersions = () => {
  if (selectedVersions.value.length !== 2) {
    ElMessage.warning(t('content.version.messages.selectTwoVersions'))
    return
  }

  compareVersions.value = [...selectedVersions.value]
  compareDialogVisible.value = true
}

const restoreVersion = async (version: Version) => {
  if (!props.content) return

  try {
    await ElMessageBox.confirm(
      t('content.version.messages.restoreConfirm', { version: version.versionNumber }),
      t('common.message.confirm'),
      {
        confirmButtonText: t('common.button.confirm'),
        cancelButtonText: t('common.button.cancel'),
        type: 'warning',
      }
    )

    // 调用真实API恢复版本
    const response = await restoreVersionApi(props.content.id, version.versionNumber)

    if (response.code === 200) {
      ElMessage.success(t('content.version.messages.restoreSuccess'))
      loadVersions()
      // 通知父组件刷新内容
      emit('update:modelValue', false)
    } else {
      ElMessage.error(response.message || t('content.version.messages.restoreFailed'))
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('❌ 版本恢复失败:', error)
      ElMessage.error(error.message || t('content.version.messages.restoreFailed'))
    }
  }
}

const tagVersion = (version: Version) => {
  currentTagVersion.value = version
  tagForm.value.tagName = version.tagName || ''
  tagDialogVisible.value = true
}

const confirmTag = async () => {
  if (!currentTagVersion.value || !props.content) return

  try {
    // 调用真实API打标签
    const response = await tagVersionApi(
      props.content.id,
      currentTagVersion.value.versionNumber,
      tagForm.value.tagName
    )

    if (response.code === 200) {
      ElMessage.success(t('content.version.messages.tagSuccess'))
      tagDialogVisible.value = false
      currentTagVersion.value = null
      tagForm.value.tagName = ''
      loadVersions() // 重新加载版本列表
    } else {
      ElMessage.error(response.message || t('content.version.messages.tagFailed'))
    }
  } catch (error: any) {
    console.error('❌ 版本标记失败:', error)
    ElMessage.error(t('content.version.messages.tagFailed'))
  }
}

const deleteVersion = async (version: Version) => {
  if (!props.content) return

  try {
    await ElMessageBox.confirm(
      t('content.version.messages.deleteConfirm', { version: version.versionNumber }),
      t('common.message.confirm'),
      {
        confirmButtonText: t('common.button.confirm'),
        cancelButtonText: t('common.button.cancel'),
        type: 'error',
      }
    )

    // 调用真实API删除版本
    const response = await deleteVersionApi(props.content.id, version.versionNumber)

    if (response.code === 200) {
      ElMessage.success(t('content.version.messages.deleteSuccess'))
      loadVersions() // 重新加载版本列表
    } else {
      ElMessage.error(response.message || t('content.version.messages.deleteFailed'))
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('❌ 版本删除失败:', error)
      ElMessage.error(error.message || t('content.version.messages.deleteFailed'))
    }
  }
}

// 辅助方法
const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'PUBLISHED': 'success',
    'DRAFT': 'info',
    'PENDING': 'warning',
    'ARCHIVED': 'info'
  }
  return statusMap[status] || 'info'
}

const getChangeTypeColor = (changeType: string) => {
  const colorMap: Record<string, string> = {
    'CREATE': 'success',
    'UPDATE': 'primary',
    'PUBLISH': 'warning',
    'UNPUBLISH': 'info',
    'RESTORE': 'danger',
    'STATUS_CHANGE': 'warning'
  }
  return colorMap[changeType] || 'info'
}
</script>

<style scoped>
.version-history-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100%;
}

.content-info-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.content-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.content-meta .author {
  color: #606266;
  font-size: 14px;
}

.version-list-card {
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.version-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.version-detail {
  padding: 20px 0;
}

.version-content {
  margin-top: 20px;
}

.version-content h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 8px;
}

.content-preview {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.6;
}

.version-compare {
  padding: 20px 0;
}

.compare-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e4e7ed;
}

.version-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.version-info p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.compare-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.version-content-side {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.version-content-side .content-preview {
  border: none;
  border-radius: 0;
  margin: 0;
  max-height: 500px;
}

/* 抽屉样式优化 */
:deep(.el-drawer) {
  border-radius: 12px 0 0 12px;
  overflow: hidden;
}

:deep(.el-drawer__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 20px;
  margin-bottom: 0;
}

:deep(.el-drawer__title) {
  color: #fff;
  font-weight: 600;
  font-size: 18px;
}

:deep(.el-drawer__close-btn) {
  color: #fff;
  font-size: 20px;
}

:deep(.el-drawer__body) {
  padding: 0;
  background: #f5f7fa;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  transition: all 0.3s;
}

:deep(.el-table__row:hover) {
  background: #f5f7fa;
}

/* 按钮样式优化 */
:deep(.el-button) {
  transition: all 0.3s;
  border-radius: 6px;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

:deep(.el-button:active) {
  transform: translateY(0);
}

/* 标签样式优化 */
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: #fff;
  font-weight: 600;
}

:deep(.el-dialog__close) {
  color: #fff;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

/* 描述列表样式优化 */
:deep(.el-descriptions) {
  margin-bottom: 20px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  color: #303133;
}

/* 分页样式优化 */
:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-pagination button:hover) {
  color: #409EFF;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .version-history-container {
    padding: 10px;
  }

  .compare-header,
  .compare-content {
    grid-template-columns: 1fr;
  }

  .content-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .header-actions {
    flex-direction: column;
    gap: 4px;
  }
}

/* 动画效果 */
.version-history-container {
  animation: fadeIn 0.3s ease-in;
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

/* 内容预览样式 */
.content-preview :deep(h1),
.content-preview :deep(h2),
.content-preview :deep(h3),
.content-preview :deep(h4),
.content-preview :deep(h5),
.content-preview :deep(h6) {
  color: #303133;
  margin: 16px 0 12px 0;
  font-weight: 600;
}

.content-preview :deep(p) {
  margin: 12px 0;
  color: #606266;
  line-height: 1.8;
}

.content-preview :deep(ul),
.content-preview :deep(ol) {
  margin: 12px 0;
  padding-left: 24px;
}

.content-preview :deep(li) {
  margin: 6px 0;
  color: #606266;
}

.content-preview :deep(blockquote) {
  border-left: 4px solid #409EFF;
  padding-left: 16px;
  margin: 16px 0;
  color: #909399;
  font-style: italic;
}

.content-preview :deep(code) {
  background: #f1f2f3;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  color: #e83e8c;
}

.content-preview :deep(pre) {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 16px;
  overflow-x: auto;
  margin: 16px 0;
}

.content-preview :deep(pre code) {
  background: none;
  padding: 0;
  color: #495057;
}

/* 操作按钮样式优化 */
.action-buttons {
  display: flex;
  flex-wrap: nowrap;
  gap: 4px;
  align-items: center;
  justify-content: flex-start;
}

.action-btn {
  flex-shrink: 0;
  min-width: auto;
  padding: 4px 8px;
  font-size: 12px;
}

.action-btn .el-icon {
  margin-right: 2px;
}

/* 确保按钮文本不换行 */
.action-btn span {
  white-space: nowrap;
}

/* 响应式处理 */
@media (max-width: 1200px) {
  .action-buttons {
    flex-direction: column;
    gap: 2px;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>

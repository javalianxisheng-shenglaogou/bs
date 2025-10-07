<template>
  <div class="contents">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ t('content.list') }}</span>
          <div>
            <el-select v-model="selectedSite" :placeholder="t('site.placeholder.selectSite')" clearable style="width: 150px; margin-right: 10px">
              <el-option
                v-for="site in siteList"
                :key="site.id"
                :label="site.name"
                :value="site.id || 0"
              />
            </el-select>
            <el-select v-model="selectedStatus" :placeholder="t('content.placeholder.selectStatus')" clearable style="width: 120px; margin-right: 10px">
              <el-option :label="t('content.status.draft')" value="DRAFT" />
              <el-option :label="t('content.status.pending')" value="PENDING" />
              <el-option :label="t('content.status.published')" value="PUBLISHED" />
              <el-option :label="t('content.status.archived')" value="ARCHIVED" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              {{ t('content.add') }}
            </el-button>
          </div>
        </div>
      </template>

      <!-- 内容表格 -->
      <el-table :data="contentList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" :label="t('content.fields.title')" min-width="200" show-overflow-tooltip />
        <el-table-column :label="t('content.fields.contentType')" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.contentType === 'ARTICLE'" type="primary">{{ t('content.contentTypes.article') }}</el-tag>
            <el-tag v-else-if="row.contentType === 'PAGE'" type="success">{{ t('content.contentTypes.page') }}</el-tag>
            <el-tag v-else-if="row.contentType === 'NEWS'" type="warning">{{ t('content.contentTypes.news') }}</el-tag>
            <el-tag v-else>{{ t('content.contentTypes.other') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('content.fields.status')" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PUBLISHED'" type="success">{{ t('content.status.published') }}</el-tag>
            <el-tag v-else-if="row.status === 'DRAFT'" type="info">{{ t('content.status.draft') }}</el-tag>
            <el-tag v-else-if="row.status === 'PENDING'" type="warning">{{ t('content.status.pending') }}</el-tag>
            <el-tag v-else type="info">{{ t('content.status.archived') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('content.fields.approvalStatus')" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.approvalStatus === 'APPROVED'" type="success">{{ t('content.approvalStatus.approved') }}</el-tag>
            <el-tag v-else-if="row.approvalStatus === 'PENDING'" type="warning">{{ t('content.approvalStatus.pending') }}</el-tag>
            <el-tag v-else-if="row.approvalStatus === 'REJECTED'" type="danger">{{ t('content.approvalStatus.rejected') }}</el-tag>
            <el-tag v-else-if="row.status === 'DRAFT' && (!row.approvalStatus || row.approvalStatus === 'NONE')" type="info">{{ t('content.approvalStatus.none') }}</el-tag>
            <el-tag v-else-if="row.status === 'PUBLISHED' && row.approvalStatus === 'APPROVED'" type="success">{{ t('content.approvalStatus.approved') }}</el-tag>
            <el-tag v-else type="info">{{ t('content.approvalStatus.none') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" :label="t('content.fields.authorName')" width="100" />
        <el-table-column prop="viewCount" :label="t('content.fields.viewCount')" width="100" />
        <el-table-column :label="t('content.fields.isTop') + '/' + t('content.fields.isFeatured')" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small">{{ t('content.fields.isTop') }}</el-tag>
            <el-tag v-if="row.isFeatured" type="warning" size="small">{{ t('content.fields.isFeatured') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('content.fields.createdAt')" width="180" />
        <el-table-column :label="t('common.button.actions')" width="450" fixed="right">
          <template #default="{ row }">
            <!-- 编辑按钮 - 只有非发布状态才能编辑 -->
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
              :disabled="row.status === 'PUBLISHED'"
              :title="row.status === 'PUBLISHED' ? t('content.message.cannotEditPublished') : t('content.edit')"
            >
              {{ t('common.button.edit') }}
            </el-button>

            <!-- 管理员：直接发布按钮 - 草稿状态 -->
            <el-button
              v-if="userStore.isAdmin() && row.status === 'DRAFT'"
              type="success"
              size="small"
              @click="handlePublish(row)"
            >
              {{ t('common.button.publish') }}
            </el-button>

            <!-- 非管理员：提交审批按钮 - 草稿状态且未提交审批 -->
            <el-button
              v-if="!userStore.isAdmin() && row.status === 'DRAFT' && (!row.approvalStatus || row.approvalStatus === 'NONE')"
              type="success"
              size="small"
              @click="handleSubmitApproval(row)"
            >
              {{ t('workflow.submitApproval') }}
            </el-button>

            <!-- 非管理员：撤回审批按钮 - 审批中 -->
            <el-button
              v-if="!userStore.isAdmin() && row.approvalStatus === 'PENDING'"
              type="warning"
              size="small"
              @click="handleWithdrawApproval(row)"
            >
              {{ t('workflow.withdrawApproval') }}
            </el-button>

            <!-- 非管理员：发布按钮 - 草稿状态且审批通过 -->
            <el-button
              v-if="!userStore.isAdmin() && row.status === 'DRAFT' && row.approvalStatus === 'APPROVED'"
              type="success"
              size="small"
              @click="handlePublish(row)"
            >
              {{ t('common.button.publish') }}
            </el-button>

            <!-- 审批员：审批按钮 - 审批中状态 -->
            <el-button
              v-if="userStore.hasPermission('content:review') && row.approvalStatus === 'PENDING'"
              type="primary"
              size="small"
              @click="handleApprove(row)"
            >
              {{ t('workflow.approve') }}
            </el-button>

            <!-- 下线按钮 - 已发布状态 -->
            <el-button
              v-if="row.status === 'PUBLISHED'"
              type="warning"
              size="small"
              @click="handleUnpublish(row)"
            >
              {{ t('common.button.unpublish') }}
            </el-button>

            <!-- 删除按钮 - 非发布状态才能删除 -->
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
              :disabled="row.status === 'PUBLISHED'"
              :title="row.status === 'PUBLISHED' ? t('content.message.cannotEditPublished') : t('content.delete')"
            >
              {{ t('common.button.delete') }}
            </el-button>
            
            <!-- 版本历史按钮：打开当前内容的版本抽屉 -->
            <el-button
              type="info"
              size="small"
              @click="openVersions(row)"
              :title="t('content.version.actions.viewHistory')"
            >
              {{ t('content.version.history') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          :current-page="currentPage + 1"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item :label="t('site.fields.name')" prop="siteId">
          <el-select v-model="formData.siteId" :placeholder="t('site.placeholder.selectSite')" :disabled="isEdit">
            <el-option
              v-for="site in siteList"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('content.fields.title')" prop="title">
          <el-input v-model="formData.title" :placeholder="t('content.placeholder.title')" />
        </el-form-item>

        <el-form-item :label="t('content.fields.slug')" prop="slug">
          <el-input
            v-model="formData.slug"
            :placeholder="t('content.placeholder.slug')"
          />
        </el-form-item>

        <el-form-item :label="t('content.fields.excerpt')">
          <el-input
            v-model="formData.summary"
            type="textarea"
            :rows="3"
            :placeholder="t('content.placeholder.excerpt')"
          />
        </el-form-item>

        <el-form-item :label="t('content.fields.content')">
          <RichTextEditor v-model="formData.content" height="400px" :placeholder="t('content.placeholder.content')" />
        </el-form-item>

        <el-form-item :label="t('content.fields.contentType')">
          <el-select v-model="formData.contentType" :placeholder="t('content.placeholder.selectContentType')">
            <el-option :label="t('content.contentTypes.article')" value="ARTICLE" />
            <el-option :label="t('content.contentTypes.page')" value="PAGE" />
            <el-option :label="t('content.contentTypes.news')" value="NEWS" />
            <el-option :label="t('content.contentTypes.product')" value="PRODUCT" />
            <el-option :label="t('content.contentTypes.custom')" value="CUSTOM" />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('content.fields.coverImage')">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
          >
            <img v-if="formData.coverImage" :src="formData.coverImage" class="cover-image" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">{{ t('content.uploadTip') }}</div>
        </el-form-item>

        <el-form-item :label="t('content.fields.status')">
          <el-radio-group v-model="formData.status">
            <el-radio label="DRAFT">{{ t('content.status.draft') }}</el-radio>
            <el-radio label="PENDING">{{ t('content.status.pending') }}</el-radio>
            <el-radio label="PUBLISHED">{{ t('content.status.published') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="t('content.fields.attributes')">
          <el-checkbox v-model="formData.isTop">{{ t('content.fields.isTop') }}</el-checkbox>
          <el-checkbox v-model="formData.isFeatured">{{ t('content.fields.isFeatured') }}</el-checkbox>
          <el-checkbox v-model="formData.isOriginal">{{ t('content.fields.isOriginal') }}</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.button.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.button.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 提交审批对话框 -->
    <SubmitApprovalDialog
      v-model:visible="submitApprovalDialogVisible"
      :content-id="currentContent?.id || 0"
      :content-title="currentContent?.title || ''"
      @success="handleSubmitApprovalSuccess"
    />

    <!-- 审批对话框 -->
    <ApproveContentDialog
      v-model:visible="approveDialogVisible"
      :content-id="currentContent?.id || 0"
      :content-title="currentContent?.title || ''"
      @success="handleApproveSuccess"
    />

    <!-- 版本历史组件 -->
    <VersionHistory
      v-model="versionsDrawerVisible"
      :content="currentContent"
    />



    <!-- 版本详情对话框：展示单个版本的关键字段 -->
    <el-dialog v-model="versionDetailVisible" :title="t('content.version.dialog.versionDetail')" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item :label="t('content.version.fields.versionNumber')">{{ versionDetail?.versionNumber }}</el-descriptions-item>
        <el-descriptions-item :label="t('content.fields.title')">{{ versionDetail?.title }}</el-descriptions-item>
        <el-descriptions-item :label="t('content.version.fields.changeType')">{{ versionDetail?.changeType || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('content.version.fields.changeSummary')" :span="2">{{ versionDetail?.changeSummary || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('content.version.fields.createdBy')">{{ versionDetail?.createdByName || versionDetail?.createdBy || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('content.version.fields.createdAt')">{{ versionDetail?.createdAt || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="t('content.version.fields.tagName')" :span="2">{{ versionDetail?.isTagged ? (versionDetail?.tagName || t('content.version.tagged')) : '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt-12">
        <div class="sub-title">{{ t('content.fields.content') }}{{ t('common.message.preview') }}</div>
        <el-input type="textarea" :rows="10" v-model="versionDetailContentPreview" readonly />
      </div>
      <template #footer>
        <el-button @click="versionDetailVisible = false">{{ t('common.button.close') }}</el-button>
      </template>
    </el-dialog>

    <!-- 版本对比对话框：展示两版本的差异与关键字段 -->
    <el-dialog v-model="compareDialogVisible" :title="t('content.version.dialog.versionCompare')" width="800px">
      <div class="compare-meta">
        <div class="compare-block">
          <div class="sub-title">{{ t('content.version.dialog.versionA', { version: compareVersionA?.versionNumber }) }}</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="t('content.fields.title')">{{ compareVersionA?.title }}</el-descriptions-item>
            <el-descriptions-item :label="t('content.version.fields.changeSummary')">{{ compareVersionA?.changeSummary || '-' }}</el-descriptions-item>
            <el-descriptions-item :label="t('content.version.fields.createdBy')">{{ compareVersionA?.createdByName || '-' }}</el-descriptions-item>
            <el-descriptions-item :label="t('content.version.fields.createdAt')">{{ compareVersionA?.createdAt || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <div class="compare-block">
          <div class="sub-title">{{ t('content.version.dialog.versionB', { version: compareVersionB?.versionNumber }) }}</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item :label="t('content.fields.title')">{{ compareVersionB?.title }}</el-descriptions-item>
            <el-descriptions-item :label="t('content.version.fields.changeSummary')">{{ compareVersionB?.changeSummary || '-' }}</el-descriptions-item>
            <el-descriptions-item :label="t('content.version.fields.createdBy')">{{ compareVersionB?.createdByName || '-' }}</el-descriptions-item>
            <el-descriptions-item :label="t('content.version.fields.createdAt')">{{ compareVersionB?.createdAt || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <div class="mt-12">
        <div class="sub-title">{{ t('content.version.diffFields') }}</div>
        <div v-if="compareDifferences && Object.keys(compareDifferences).length" class="diff-list">
          <el-tag
            v-for="(changed, field) in compareDifferences"
            :key="field"
            :type="changed ? 'danger' : 'info'"
            class="mr-8 mb-8"
          >
            {{ field }}：{{ changed ? t('content.version.changed') : t('content.version.unchanged') }}
          </el-tag>
        </div>
        <div v-else class="text-muted">{{ t('content.version.noDifferences') }}</div>
      </div>
      <template #footer>
        <el-button @click="compareDialogVisible = false">{{ t('common.button.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import {
  getContentsApi,
  createContentApi,
  updateContentApi,
  deleteContentApi,
  updateContentStatusApi,
  submitApprovalApi,
  withdrawApprovalApi,
  type Content,
  // 版本相关API与类型
  getVersionListApi,
  getVersionApi,
  restoreVersionApi,
  compareVersionsApi,
  getVersionStatisticsApi,
  tagVersionApi,
  deleteVersionApi,
  type ContentVersionDTO
} from '@/api/content'
import { getAllSitesApi, type Site } from '@/api/site'
import { useUserStore } from '@/store/user'
import RichTextEditor from '@/components/RichTextEditor.vue'
import SubmitApprovalDialog from '@/components/SubmitApprovalDialog.vue'
import ApproveContentDialog from '@/components/ApproveContentDialog.vue'
import VersionHistory from '@/components/VersionHistory.vue'

const userStore = useUserStore()
const { t } = useI18n()

// 上传配置
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/files/upload'
const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

// 响应式数据
const loading = ref(false)
const selectedSite = ref<number | undefined>(undefined)
const selectedStatus = ref('')
const contentList = ref<Content[]>([])
const currentPage = ref(0)  // 从0开始，匹配后端分页
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增内容')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const siteList = ref<Site[]>([])
const submitApprovalDialogVisible = ref(false)
const approveDialogVisible = ref(false)
const currentContent = ref<Content | null>(null)

// 版本控制相关
const versionsDrawerVisible = ref(false)
const versionDetailVisible = ref(false)
const compareDialogVisible = ref(false)

// 表单数据
const formData = reactive<Content>({
  siteId: 0,
  title: '',
  slug: '',
  summary: '',
  content: '',
  contentType: 'ARTICLE',
  status: 'DRAFT',
  isTop: false,
  isFeatured: false,
  isOriginal: true
})

// 表单验证规则
const formRules: FormRules = {
  siteId: [
    { required: true, message: t('site.validation.siteRequired'), trigger: 'change' }
  ],
  title: [
    { required: true, message: t('content.validation.titleRequired'), trigger: 'blur' },
    { max: 200, message: t('content.validation.titleLength'), trigger: 'blur' }
  ],
  slug: [
    { required: true, message: t('content.validation.slugRequired'), trigger: 'blur' },
    { max: 200, message: t('content.validation.slugLength'), trigger: 'blur' },
    { pattern: /^[a-z0-9-]+$/, message: t('content.validation.slugPattern'), trigger: 'blur' }
  ]
}

// 加载站点列表
const loadSites = async () => {
  try {
    const response = await getAllSitesApi()
    console.log('✅ 站点列表响应:', response)
    // response是 {code, message, data} 格式
    if (response.code === 200 && response.data) {
      siteList.value = (Array.isArray(response.data) ? response.data : []).filter(site => site && site.id)
    } else {
      siteList.value = []
    }
  } catch (error: any) {
    console.error('❌ 加载站点列表失败:', error)
    siteList.value = []
  }
}

// 加载内容列表
const loadContents = async () => {
  loading.value = true
  console.log('🔍 开始加载内容列表，参数:', {
    siteId: selectedSite.value,
    status: selectedStatus.value,
    page: currentPage.value,
    size: pageSize.value
  })

  try {
    const response = await getContentsApi({
      siteId: selectedSite.value,
      status: selectedStatus.value || undefined,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: 'createdAt',
      sortDir: 'desc'
    })
    console.log('✅ 内容列表响应:', response)

    // response是 {code, message, data} 格式，data里面有content和totalElements
    if (response.code === 200 && response.data) {
      contentList.value = Array.isArray(response.data.content) ? response.data.content : []
      total.value = response.data.totalElements || 0
      console.log('✅ 内容列表加载成功:', {
        total: total.value,
        count: contentList.value.length,
        firstItem: contentList.value[0]
      })
    } else {
      contentList.value = []
      total.value = 0
      ElMessage.error(response.message || '加载内容列表失败')
    }
  } catch (error: any) {
    console.error('❌ 加载内容列表失败:', error)
    contentList.value = []
    total.value = 0
    ElMessage.error(error.message || '加载内容列表失败')
  } finally {
    loading.value = false
  }
}

// 监听筛选条件变化
watch([selectedSite, selectedStatus], () => {
  currentPage.value = 0  // 重置到第一页（从0开始）
  loadContents()
})

// 重置表单
const resetForm = () => {
  formData.siteId = 0
  formData.title = ''
  formData.slug = ''
  formData.summary = ''
  formData.content = ''
  formData.contentType = 'ARTICLE'
  formData.status = 'DRAFT'
  formData.coverImage = ''
  formData.isTop = false
  formData.isFeatured = false
  formData.isOriginal = true
  formRef.value?.clearValidate()
}

// 封面上传成功
const handleCoverSuccess = (response: any) => {
  if (response.code === 200) {
    formData.coverImage = response.data.url
    ElMessage.success(t('content.message.uploadSuccess'))
  } else {
    ElMessage.error(response.message || t('content.message.uploadFailed'))
  }
}

// 封面上传前验证
const beforeCoverUpload = (file: File) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error(t('content.message.uploadFormatError'))
    return false
  }
  if (!isLt2M) {
    ElMessage.error(t('content.message.uploadSizeError'))
    return false
  }
  return true
}

// 新增内容
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = t('content.add')
  resetForm()
  // 设置当前用户为作者
  if (userStore.userInfo) {
    formData.authorId = userStore.userInfo.userId
    formData.authorName = userStore.userInfo.nickname || userStore.userInfo.username
  }
  dialogVisible.value = true
}

// 编辑内容
const handleEdit = (row: Content) => {
  isEdit.value = true
  dialogTitle.value = t('content.edit')
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 发布内容
const handlePublish = async (row: Content) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布内容 "${row.title}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
      }
    )

    await updateContentStatusApi(row.id!, 'PUBLISHED')
    ElMessage.success('发布成功')
    await loadContents()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('发布内容失败:', error)
      ElMessage.error(error.message || '发布内容失败')
    }
  }
}

// 下线内容
const handleUnpublish = async (row: Content) => {
  try {
    await ElMessageBox.confirm(
      `确定要下线内容 "${row.title}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await updateContentStatusApi(row.id!, 'DRAFT')
    ElMessage.success('下线成功')
    await loadContents()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('下线内容失败:', error)
      ElMessage.error(error.message || '下线内容失败')
    }
  }
}

// 提交审批
const handleSubmitApproval = (row: Content) => {
  currentContent.value = row
  submitApprovalDialogVisible.value = true
}

// 提交审批成功回调
const handleSubmitApprovalSuccess = async () => {
  await loadContents()
}

// 审批内容
const handleApprove = (row: Content) => {
  currentContent.value = row
  approveDialogVisible.value = true
}

// 审批成功回调
const handleApproveSuccess = async () => {
  await loadContents()
}

// 撤回审批
const handleWithdrawApproval = async (row: Content) => {
  try {
    await ElMessageBox.confirm(
      `确定要撤回内容 "${row.title}" 的审批吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await withdrawApprovalApi(row.id!)
    ElMessage.success('撤回审批成功')
    await loadContents()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('撤回审批失败:', error)
      ElMessage.error(error.message || '撤回审批失败')
    }
  }
}

// 删除内容
const handleDelete = async (row: Content) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除内容 "${row.title}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error',
      }
    )

    await deleteContentApi(row.id!)
    ElMessage.success('删除成功')
    await loadContents()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除内容失败:', error)
      ElMessage.error(error.message || '删除内容失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value && formData.id) {
          await updateContentApi(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createContentApi(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadContents()
      } catch (error: any) {
        console.error('保存内容失败:', error)
        ElMessage.error(error.message || '保存内容失败')
      }
    }
  })
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadContents()
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val - 1  // Element Plus从1开始，转换为从0开始
  loadContents()
}

// 版本控制相关方法
const openVersions = (row: Content) => {
  console.log('打开版本历史:', row)
  currentContent.value = row
  versionsDrawerVisible.value = true
}

const openVersionDetail = (version: any) => {
  console.log('查看版本详情:', version)
  versionDetailVisible.value = true
}

const openCompareDialog = (version1: any, version2: any) => {
  console.log('比较版本:', version1, version2)
  compareDialogVisible.value = true
}

// 组件挂载时加载数据
onMounted(() => {
  loadSites()
  loadContents()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.cover-image {
  width: 178px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* 页面动画优化 */
.contents {
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

/* 卡片头部优化 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

/* 表格优化 */
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

/* 按钮优化 */
:deep(.el-button) {
  transition: all 0.3s;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

:deep(.el-button:active) {
  transform: translateY(0);
}

/* 标签优化 */
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

/* 对话框优化 */
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

/* 分页优化 */
:deep(.el-pagination) {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination button:hover) {
  color: #409EFF;
}
</style>


<template>
  <div class="contents">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>内容列表</span>
          <div>
            <el-select v-model="selectedSite" placeholder="选择站点" clearable style="width: 150px; margin-right: 10px">
              <el-option
                v-for="site in siteList"
                :key="site.id"
                :label="site.name"
                :value="site.id || 0"
              />
            </el-select>
            <el-select v-model="selectedStatus" placeholder="状态" clearable style="width: 120px; margin-right: 10px">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="已归档" value="ARCHIVED" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增内容
            </el-button>
          </div>
        </div>
      </template>

      <!-- 内容表格 -->
      <el-table :data="contentList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="内容类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.contentType === 'ARTICLE'" type="primary">文章</el-tag>
            <el-tag v-else-if="row.contentType === 'PAGE'" type="success">页面</el-tag>
            <el-tag v-else-if="row.contentType === 'NEWS'" type="warning">新闻</el-tag>
            <el-tag v-else>其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PUBLISHED'" type="success">已发布</el-tag>
            <el-tag v-else-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 'PENDING'" type="warning">待审核</el-tag>
            <el-tag v-else type="info">已归档</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.approvalStatus === 'APPROVED'" type="success">已通过</el-tag>
            <el-tag v-else-if="row.approvalStatus === 'PENDING'" type="warning">审批中</el-tag>
            <el-tag v-else-if="row.approvalStatus === 'REJECTED'" type="danger">已拒绝</el-tag>
            <el-tag v-else type="info">无需审批</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="置顶/推荐" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="row.isFeatured" type="warning" size="small">推荐</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="400" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>

            <!-- 提交审批按钮 - 草稿状态且未提交审批 -->
            <el-button
              v-if="row.status === 'DRAFT' && (!row.approvalStatus || row.approvalStatus === 'NONE')"
              type="success"
              size="small"
              @click="handleSubmitApproval(row)"
            >
              提交审批
            </el-button>

            <!-- 撤回审批按钮 - 审批中 -->
            <el-button
              v-if="row.approvalStatus === 'PENDING'"
              type="warning"
              size="small"
              @click="handleWithdrawApproval(row)"
            >
              撤回审批
            </el-button>

            <!-- 直接发布按钮 - 管理员或审批通过 -->
            <el-button
              v-if="row.status !== 'PUBLISHED' && row.approvalStatus !== 'PENDING'"
              type="success"
              size="small"
              @click="handlePublish(row)"
            >
              发布
            </el-button>

            <!-- 下线按钮 -->
            <el-button
              v-if="row.status === 'PUBLISHED'"
              type="warning"
              size="small"
              @click="handleUnpublish(row)"
            >
              下线
            </el-button>

            <!-- 删除按钮 -->
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item label="所属站点" prop="siteId">
          <el-select v-model="formData.siteId" placeholder="请选择站点" :disabled="isEdit">
            <el-option
              v-for="site in siteList"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>

        <el-form-item label="URL别名" prop="slug">
          <el-input
            v-model="formData.slug"
            placeholder="请输入URL别名(小写字母、数字、连字符)"
          />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input
            v-model="formData.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入摘要"
          />
        </el-form-item>

        <el-form-item label="内容">
          <RichTextEditor v-model="formData.content" height="400px" placeholder="请输入内容..." />
        </el-form-item>

        <el-form-item label="内容类型">
          <el-select v-model="formData.contentType" placeholder="请选择内容类型">
            <el-option label="文章" value="ARTICLE" />
            <el-option label="页面" value="PAGE" />
            <el-option label="新闻" value="NEWS" />
            <el-option label="产品" value="PRODUCT" />
            <el-option label="自定义" value="CUSTOM" />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图">
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
          <div class="upload-tip">建议尺寸: 800x450px, 支持jpg/png格式, 大小不超过2MB</div>
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio label="DRAFT">草稿</el-radio>
            <el-radio label="PENDING">待审核</el-radio>
            <el-radio label="PUBLISHED">已发布</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="内容属性">
          <el-checkbox v-model="formData.isTop">置顶</el-checkbox>
          <el-checkbox v-model="formData.isFeatured">推荐</el-checkbox>
          <el-checkbox v-model="formData.isOriginal">原创</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 提交审批对话框 -->
    <SubmitApprovalDialog
      v-model:visible="submitApprovalDialogVisible"
      :content-id="currentContent?.id || 0"
      :content-title="currentContent?.title || ''"
      @success="handleSubmitApprovalSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getContentsApi,
  createContentApi,
  updateContentApi,
  deleteContentApi,
  updateContentStatusApi,
  submitApprovalApi,
  withdrawApprovalApi,
  type Content
} from '@/api/content'
import { getAllSitesApi, type Site } from '@/api/site'
import { useUserStore } from '@/store/user'
import RichTextEditor from '@/components/RichTextEditor.vue'
import SubmitApprovalDialog from '@/components/SubmitApprovalDialog.vue'

const userStore = useUserStore()

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
const currentContent = ref<Content | null>(null)

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
    { required: true, message: '请选择站点', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过200', trigger: 'blur' }
  ],
  slug: [
    { required: true, message: '请输入URL别名', trigger: 'blur' },
    { max: 200, message: 'URL别名长度不能超过200', trigger: 'blur' },
    { pattern: /^[a-z0-9-]+$/, message: 'URL别名只能包含小写字母、数字和连字符', trigger: 'blur' }
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
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(response.message || '封面上传失败')
  }
}

// 封面上传前验证
const beforeCoverUpload = (file: File) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('封面图只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面图大小不能超过 2MB!')
    return false
  }
  return true
}

// 新增内容
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增内容'
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
  dialogTitle.value = '编辑内容'
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


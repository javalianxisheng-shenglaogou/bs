<template>
  <div class="content-edit-page">
    <div class="page-header">
      <h1>编辑内容</h1>
      <div class="header-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button @click="saveDraft" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="publish" :loading="publishing">
          {{ contentForm.status === 'PUBLISHED' ? '更新' : '发布' }}
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="content-form-container">
      <el-form
        ref="contentFormRef"
        :model="contentForm"
        :rules="contentRules"
        label-width="100px"
        size="large"
      >
        <el-row :gutter="24">
          <!-- 左侧主要内容 -->
          <el-col :span="16">
            <el-card class="main-content-card">
              <el-form-item label="标题" prop="title">
                <el-input
                  v-model="contentForm.title"
                  placeholder="请输入内容标题"
                  maxlength="200"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="摘要" prop="summary">
                <el-input
                  v-model="contentForm.summary"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入内容摘要"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="内容" prop="content">
                <div class="editor-container">
                  <el-input
                    v-model="contentForm.content"
                    type="textarea"
                    :rows="20"
                    placeholder="请输入内容正文"
                    class="content-editor"
                  />
                  <div class="editor-toolbar">
                    <el-button size="small" @click="insertImage">插入图片</el-button>
                    <el-button size="small" @click="insertLink">插入链接</el-button>
                    <span class="word-count">{{ contentForm.content.length }} 字符</span>
                  </div>
                </div>
              </el-form-item>
            </el-card>
          </el-col>

          <!-- 右侧设置面板 -->
          <el-col :span="8">
            <!-- 发布设置 -->
            <el-card class="settings-card">
              <template #header>
                <span>发布设置</span>
              </template>

              <el-form-item label="内容类型" prop="contentType">
                <el-select v-model="contentForm.contentType" placeholder="选择内容类型">
                  <el-option label="文章" value="ARTICLE" />
                  <el-option label="页面" value="PAGE" />
                </el-select>
              </el-form-item>

              <el-form-item label="状态">
                <el-tag :type="getStatusType(contentForm.status)">
                  {{ getStatusText(contentForm.status) }}
                </el-tag>
              </el-form-item>

              <el-form-item label="分类" prop="categoryId">
                <el-select v-model="contentForm.categoryId" placeholder="选择分类" clearable>
                  <el-option
                    v-for="category in categories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="发布时间">
                <el-date-picker
                  v-model="contentForm.publishAt"
                  type="datetime"
                  placeholder="选择发布时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>

              <el-form-item>
                <el-checkbox v-model="contentForm.allowComment">允许评论</el-checkbox>
              </el-form-item>

              <el-form-item>
                <el-checkbox v-model="contentForm.isTop">置顶显示</el-checkbox>
              </el-form-item>
            </el-card>

            <!-- 特色图片 -->
            <el-card class="featured-image-card">
              <template #header>
                <span>特色图片</span>
              </template>

              <div class="featured-image-upload">
                <el-upload
                  v-if="!contentForm.featuredImage"
                  :show-file-list="false"
                  :before-upload="beforeImageUpload"
                  :http-request="uploadFeaturedImage"
                  accept="image/*"
                  drag
                >
                  <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                  <div class="el-upload__text">
                    将图片拖到此处，或<em>点击上传</em>
                  </div>
                  <template #tip>
                    <div class="el-upload__tip">
                      只能上传jpg/png文件，且不超过2MB
                    </div>
                  </template>
                </el-upload>

                <div v-else class="featured-image-preview">
                  <img :src="contentForm.featuredImage" alt="特色图片" />
                  <div class="image-actions">
                    <el-button size="small" @click="changeFeaturedImage">更换</el-button>
                    <el-button size="small" type="danger" @click="removeFeaturedImage">移除</el-button>
                  </div>
                </div>
              </div>
            </el-card>

            <!-- SEO设置 -->
            <el-card class="seo-card">
              <template #header>
                <span>SEO设置</span>
              </template>

              <el-form-item label="SEO标题">
                <el-input
                  v-model="contentForm.seoTitle"
                  placeholder="SEO标题"
                  maxlength="60"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="SEO描述">
                <el-input
                  v-model="contentForm.seoDescription"
                  type="textarea"
                  :rows="3"
                  placeholder="SEO描述"
                  maxlength="160"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="关键词">
                <el-input
                  v-model="contentForm.seoKeywords"
                  placeholder="关键词，用逗号分隔"
                />
              </el-form-item>

              <el-form-item label="URL别名">
                <el-input
                  v-model="contentForm.slug"
                  placeholder="URL别名"
                />
              </el-form-item>
            </el-card>

            <!-- 内容信息 -->
            <el-card class="info-card">
              <template #header>
                <span>内容信息</span>
              </template>

              <div class="info-item">
                <label>创建时间</label>
                <span>{{ formatDate(contentForm.createdAt) }}</span>
              </div>

              <div class="info-item">
                <label>更新时间</label>
                <span>{{ formatDate(contentForm.updatedAt) }}</span>
              </div>

              <div class="info-item">
                <label>浏览量</label>
                <span>{{ contentForm.viewCount || 0 }}</span>
              </div>

              <div class="info-item">
                <label>点赞数</label>
                <span>{{ contentForm.likeCount || 0 }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules, type UploadRequestOptions } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { useAppStore } from '../stores/app'
import { contentApi } from '../api/content'
import { categoriesApi } from '../api/categories'
import { uploadFile } from '../api/users'
import type { ContentCategory, Content } from '../api'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const publishing = ref(false)
const categories = ref<ContentCategory[]>([])

const contentFormRef = ref<FormInstance>()

// 表单数据
const contentForm = reactive({
  id: 0,
  title: '',
  summary: '',
  content: '',
  contentType: 'ARTICLE',
  status: 'DRAFT',
  categoryId: undefined as number | undefined,
  publishAt: '',
  allowComment: true,
  isTop: false,
  featuredImage: '',
  seoTitle: '',
  seoDescription: '',
  seoKeywords: '',
  slug: '',
  createdAt: '',
  updatedAt: '',
  viewCount: 0,
  likeCount: 0
})

// 表单验证规则
const contentRules: FormRules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 200, message: '标题长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  summary: [
    { max: 500, message: '摘要长度不能超过 500 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
  contentType: [
    { required: true, message: '请选择内容类型', trigger: 'change' }
  ]
}

// 方法
const fetchContent = async () => {
  const contentId = Number(route.params.id)
  if (!contentId) return

  loading.value = true
  try {
    const response = await contentApi.getContentById(contentId)
    const content = response.data

    // 填充表单数据
    Object.assign(contentForm, {
      id: content.id,
      title: content.title,
      summary: content.summary || '',
      content: content.content,
      contentType: content.contentType,
      status: content.status,
      categoryId: content.categoryId,
      publishAt: content.publishAt || '',
      allowComment: content.allowComment,
      isTop: content.isTop,
      featuredImage: content.featuredImage || '',
      seoTitle: content.seoTitle || '',
      seoDescription: content.seoDescription || '',
      seoKeywords: content.seoKeywords || '',
      slug: content.slug || '',
      createdAt: content.createdAt,
      updatedAt: content.updatedAt,
      viewCount: content.viewCount,
      likeCount: content.likeCount
    })
  } catch (error: any) {
    ElMessage.error(error.message || '获取内容失败')
    router.push('/content')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  const currentSite = appStore.currentSite
  if (!currentSite) return

  try {
    const response = await categoriesApi.getCategoryTree(currentSite.id)
    categories.value = response.data
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const goBack = () => {
  router.push('/content')
}

const saveDraft = async () => {
  if (!contentFormRef.value) return

  try {
    await contentFormRef.value.validate()
    saving.value = true

    const data = {
      ...contentForm,
      status: 'DRAFT'
    }

    await contentApi.updateContent(contentForm.id, data)
    ElMessage.success('草稿保存成功')
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const publish = async () => {
  if (!contentFormRef.value) return

  try {
    await contentFormRef.value.validate()
    publishing.value = true

    const data = {
      ...contentForm,
      status: 'PUBLISHED'
    }

    await contentApi.updateContent(contentForm.id, data)
    ElMessage.success(contentForm.status === 'PUBLISHED' ? '内容更新成功' : '内容发布成功')

    // 更新本地状态
    contentForm.status = 'PUBLISHED'
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    publishing.value = false
  }
}

const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadFeaturedImage = async (options: UploadRequestOptions) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)

    const response = await uploadFile(formData)
    contentForm.featuredImage = response.data.url

    ElMessage.success('图片上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '图片上传失败')
  }
}

const changeFeaturedImage = () => {
  // 触发文件选择
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = (e.target as HTMLInputElement).files?.[0]
    if (file && beforeImageUpload(file)) {
      uploadFeaturedImage({ file } as UploadRequestOptions)
    }
  }
  input.click()
}

const removeFeaturedImage = () => {
  contentForm.featuredImage = ''
  ElMessage.success('特色图片已移除')
}

const insertImage = () => {
  ElMessage.info('图片插入功能开发中...')
}

const insertLink = () => {
  ElMessage.info('链接插入功能开发中...')
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    ARCHIVED: 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已下线'
  }
  return statusMap[status] || status
}

const formatDate = (dateString?: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  appStore.setBreadcrumbs([
    { title: '内容管理', path: '/content' },
    { title: '编辑内容' }
  ])

  fetchContent()
  fetchCategories()
})
</script>

<style scoped>
.content-edit-page {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.content-form-container {
  max-width: 1200px;
}

.main-content-card,
.settings-card,
.featured-image-card,
.seo-card,
.info-card {
  margin-bottom: 24px;
}

.editor-container {
  position: relative;
}

.content-editor {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding: 8px 0;
  border-top: 1px solid #e4e7ed;
}

.word-count {
  font-size: 12px;
  color: #909399;
}

.featured-image-upload {
  text-align: center;
}

.featured-image-preview {
  position: relative;
}

.featured-image-preview img {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.image-actions {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  font-weight: 500;
  color: #606266;
}

@media (max-width: 1200px) {
  .content-form-container {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .content-edit-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  :deep(.el-col) {
    margin-bottom: 16px;
  }
}
</style>

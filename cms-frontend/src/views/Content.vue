<template>
  <div class="content-page">
    <div class="page-header">
      <h1>内容管理</h1>
      <el-button type="primary" @click="createContent" :disabled="!currentSite">
        <el-icon><Plus /></el-icon>
        创建内容
      </el-button>
    </div>

    <!-- 站点选择提示 -->
    <el-alert
      v-if="!currentSite"
      title="请先选择一个站点"
      description="内容管理需要在特定站点下进行，请在顶部选择一个站点。"
      type="warning"
      :closable="false"
      show-icon
      class="site-alert"
    />

    <!-- 搜索和筛选 -->
    <el-card v-if="currentSite" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索标题或内容"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已下线" value="ARCHIVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 内容列表 -->
    <el-card v-if="currentSite" class="content-card">
      <div v-loading="loading">
        <el-table :data="contentList" empty-text="暂无内容数据">
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <div class="content-title">
                <el-link type="primary" @click="viewContent(row)">
                  {{ row.title }}
                </el-link>
                <el-tag v-if="row.featured" type="warning" size="small">推荐</el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="categoryName" label="分类" width="120">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.categoryName || '-' }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag
                :type="getStatusType(row.status)"
                size="small"
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="viewCount" label="浏览量" width="100" align="center">
            <template #default="{ row }">
              {{ row.viewCount || 0 }}
            </template>
          </el-table-column>

          <el-table-column prop="publishedAt" label="发布时间" width="180">
            <template #default="{ row }">
              {{ row.publishedAt ? formatDate(row.publishedAt) : '-' }}
            </template>
          </el-table-column>

          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="editContent(row)">
                编辑
              </el-button>
              <el-button text type="primary" @click="viewContent(row)">
                查看
              </el-button>
              <el-button text type="danger" @click="deleteContent(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useAppStore } from '../stores/app'
import { contentApi } from '../api/content'
import { categoriesApi } from '../api/categories'
import type { Content, ContentCategory } from '../api'

const router = useRouter()
const appStore = useAppStore()

// 响应式数据
const loading = ref(false)
const contentList = ref<Content[]>([])
const categories = ref<ContentCategory[]>([])

const searchForm = reactive({
  keyword: '',
  status: '',
  categoryId: undefined as number | undefined
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 计算属性
const currentSite = computed(() => appStore.currentSite)

// 方法
const fetchContent = async () => {
  if (!currentSite.value) return

  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      siteId: currentSite.value.id,
      ...searchForm
    }

    const response = await contentApi.getContents(params)
    contentList.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch content:', error)
    ElMessage.error('获取内容列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  if (!currentSite.value) return

  try {
    const response = await categoriesApi.getCategoryTree(currentSite.value.id)
    categories.value = response.data
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
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

const createContent = () => {
  router.push('/content/create')
}

const editContent = (content: Content) => {
  router.push(`/content/${content.id}/edit`)
}

const viewContent = (content: Content) => {
  // 这里可以实现内容预览功能
  ElMessage.info('内容预览功能开发中...')
}

const deleteContent = async (content: Content) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除内容"${content.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await contentApi.deleteContent(content.id)
    ElMessage.success('删除成功')
    await fetchContent()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to delete content:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchContent()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    categoryId: undefined
  })
  pagination.page = 1
  fetchContent()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchContent()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchContent()
}

// 监听站点变化
watch(currentSite, (newSite) => {
  if (newSite) {
    fetchContent()
    fetchCategories()
  } else {
    contentList.value = []
    categories.value = []
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  appStore.setBreadcrumbs([{ title: '内容管理' }])
})
</script>

<style scoped>
.content-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

.site-alert {
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.content-card {
  margin-bottom: 20px;
}

.content-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.el-table .el-table__row) {
  cursor: pointer;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .content-page {
    padding: 16px;
  }

  :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-right: 0;
    margin-bottom: 16px;
  }
}
</style>

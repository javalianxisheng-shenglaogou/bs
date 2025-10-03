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
                :value="site.id"
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
        <el-table-column prop="authorName" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column label="置顶/推荐" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
            <el-tag v-if="row.isFeatured" type="warning" size="small">推荐</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handlePublish(row)" v-if="row.status !== 'PUBLISHED'">
              发布
            </el-button>
            <el-button type="warning" size="small" @click="handleUnpublish(row)" v-else>
              下线
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="10"
            placeholder="请输入内容"
          />
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
          <el-input v-model="formData.coverImage" placeholder="请输入封面图URL" />
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

        <el-form-item label="SEO标题">
          <el-input v-model="formData.seoTitle" placeholder="请输入SEO标题" />
        </el-form-item>

        <el-form-item label="SEO关键词">
          <el-input v-model="formData.seoKeywords" placeholder="请输入SEO关键词" />
        </el-form-item>

        <el-form-item label="SEO描述">
          <el-input
            v-model="formData.seoDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入SEO描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
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
  type Content
} from '@/api/content'
import { getAllSitesApi, type Site } from '@/api/site'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const selectedSite = ref<number | undefined>(undefined)
const selectedStatus = ref('')
const contentList = ref<Content[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增内容')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const siteList = ref<Site[]>([])

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
    const data = await getAllSitesApi()
    siteList.value = data
  } catch (error: any) {
    console.error('加载站点列表失败:', error)
  }
}

// 加载内容列表
const loadContents = async () => {
  loading.value = true
  try {
    const data = await getContentsApi({
      siteId: selectedSite.value,
      status: selectedStatus.value || undefined,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: 'createdAt',
      sortDir: 'desc'
    })
    contentList.value = data.content
    total.value = data.total
  } catch (error: any) {
    console.error('加载内容列表失败:', error)
    ElMessage.error(error.message || '加载内容列表失败')
  } finally {
    loading.value = false
  }
}

// 监听筛选条件变化
watch([selectedSite, selectedStatus], () => {
  currentPage.value = 1
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
  formData.seoTitle = ''
  formData.seoKeywords = ''
  formData.seoDescription = ''
  formRef.value?.clearValidate()
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
  currentPage.value = val
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
</style>


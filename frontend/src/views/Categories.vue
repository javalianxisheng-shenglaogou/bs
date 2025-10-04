<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item label="站点">
          <el-select v-model="selectedSite" placeholder="请选择站点" clearable style="width: 200px">
            <el-option
              v-for="site in siteList"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCategoryTree">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 分类树 -->
      <el-table
        v-loading="loading"
        :data="categoryTree"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" width="250">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-image
                v-if="row.iconUrl"
                :src="row.iconUrl"
                style="width: 24px; height: 24px; border-radius: 4px;"
                fit="cover"
              />
              <el-icon v-else style="font-size: 24px; color: #909399;">
                <Folder />
              </el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="分类编码" width="150" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="level" label="层级" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">L{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isVisible" label="可见性" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isVisible ? 'success' : 'danger'" size="small">
              {{ row.isVisible ? '可见' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleAddChild(row)" link>
              <el-icon><Plus /></el-icon> 子分类
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)" link>
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button
              :type="row.isVisible ? 'warning' : 'success'"
              size="small"
              @click="handleToggleVisibility(row)"
              link
            >
              <el-icon><View v-if="!row.isVisible" /><Hide v-else /></el-icon>
              {{ row.isVisible ? '隐藏' : '显示' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" link>
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <el-form-item label="站点" prop="siteId">
              <el-select v-model="formData.siteId" placeholder="请选择站点" :disabled="isEdit" style="width: 100%;">
                <el-option
                  v-for="site in siteList"
                  :key="site.id"
                  :label="site.name"
                  :value="site.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="父分类" prop="parentId">
              <el-tree-select
                v-model="formData.parentId"
                :data="categoryTreeOptions"
                :props="{ label: 'name', value: 'id' }"
                placeholder="请选择父分类(不选则为顶级分类)"
                clearable
                check-strictly
                :disabled="isEdit"
                style="width: 100%;"
              />
            </el-form-item>

            <el-form-item label="分类名称" prop="name">
              <el-input v-model="formData.name" placeholder="请输入分类名称" />
            </el-form-item>

            <el-form-item label="分类编码" prop="code">
              <el-input v-model="formData.code" placeholder="请输入分类编码(英文字母、数字、下划线)" />
            </el-form-item>

            <el-form-item label="描述">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入分类描述"
              />
            </el-form-item>

            <el-form-item label="排序">
              <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">数字越小越靠前</span>
            </el-form-item>

            <el-form-item label="是否可见">
              <el-switch v-model="formData.isVisible" />
            </el-form-item>
          </el-tab-pane>

          <!-- 图片设置 -->
          <el-tab-pane label="图片设置" name="images">
            <el-form-item label="分类图标">
              <el-upload
                class="icon-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleIconSuccess"
                :before-upload="beforeUpload"
              >
                <img v-if="formData.iconUrl" :src="formData.iconUrl" class="icon-image" />
                <el-icon v-else class="icon-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div style="color: #909399; font-size: 12px; margin-top: 8px;">
                建议尺寸: 64x64px, 支持jpg/png格式, 大小不超过2MB
              </div>
              <el-button v-if="formData.iconUrl" size="small" type="danger" @click="formData.iconUrl = ''" style="margin-top: 8px;">
                删除图标
              </el-button>
            </el-form-item>

            <el-form-item label="封面图">
              <el-upload
                class="cover-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :before-upload="beforeUpload"
              >
                <img v-if="formData.coverUrl" :src="formData.coverUrl" class="cover-image" />
                <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div style="color: #909399; font-size: 12px; margin-top: 8px;">
                建议尺寸: 800x450px, 支持jpg/png格式, 大小不超过2MB
              </div>
              <el-button v-if="formData.coverUrl" size="small" type="danger" @click="formData.coverUrl = ''" style="margin-top: 8px;">
                删除封面
              </el-button>
            </el-form-item>
          </el-tab-pane>

          <!-- SEO设置 -->
          <el-tab-pane label="SEO设置" name="seo">
            <el-form-item label="SEO标题">
              <el-input v-model="formData.seoTitle" placeholder="请输入SEO标题(不填则使用分类名称)" maxlength="100" show-word-limit />
            </el-form-item>

            <el-form-item label="SEO关键词">
              <el-input v-model="formData.seoKeywords" placeholder="请输入SEO关键词,多个关键词用逗号分隔" maxlength="200" show-word-limit />
            </el-form-item>

            <el-form-item label="SEO描述">
              <el-input
                v-model="formData.seoDescription"
                type="textarea"
                :rows="4"
                placeholder="请输入SEO描述"
                maxlength="300"
                show-word-limit
              />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules, UploadProps } from 'element-plus'
import { Plus, Edit, Delete, View, Hide, Folder } from '@element-plus/icons-vue'
import {
  getCategoryTreeApi,
  createCategoryApi,
  updateCategoryApi,
  deleteCategoryApi,
  updateCategoryVisibilityApi,
  type Category
} from '@/api/category'
import { getAllSitesApi, type Site } from '@/api/site'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const selectedSite = ref<number | undefined>(undefined)
const categoryTree = ref<Category[]>([])
const categoryTreeOptions = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const siteList = ref<Site[]>([])
const activeTab = ref('basic')

// 上传配置
const uploadUrl = computed(() => `${import.meta.env.VITE_API_BASE_URL}/files/upload/category`)
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

// 表单数据
const formData = reactive<Category>({
  siteId: 0,
  name: '',
  code: '',
  description: '',
  iconUrl: '',
  coverUrl: '',
  sortOrder: 0,
  isVisible: true,
  seoTitle: '',
  seoKeywords: '',
  seoDescription: ''
})

// 表单验证规则
const formRules: FormRules = {
  siteId: [
    { required: true, message: '请选择站点', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 100, message: '分类名称长度不能超过100', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入分类编码', trigger: 'blur' },
    { max: 50, message: '分类编码长度不能超过50', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '分类编码只能包含字母、数字和下划线', trigger: 'blur' }
  ]
}

// 加载站点列表
const loadSites = async () => {
  try {
    const response = await getAllSitesApi()
    if (response.code === 200) {
      siteList.value = response.data
      if (siteList.value.length > 0 && !selectedSite.value) {
        selectedSite.value = siteList.value[0].id
      }
    }
  } catch (error: any) {
    console.error('加载站点列表失败:', error)
    ElMessage.error(error.message || '加载站点列表失败')
  }
}

// 加载分类树
const loadCategoryTree = async () => {
  if (!selectedSite.value) {
    ElMessage.warning('请先选择站点')
    return
  }

  loading.value = true
  try {
    const response = await getCategoryTreeApi(selectedSite.value)
    if (response.code === 200) {
      categoryTree.value = response.data
      categoryTreeOptions.value = response.data
    }
  } catch (error: any) {
    console.error('加载分类树失败:', error)
    ElMessage.error(error.message || '加载分类树失败')
  } finally {
    loading.value = false
  }
}

// 监听站点变化
watch(selectedSite, () => {
  if (selectedSite.value) {
    loadCategoryTree()
  }
})

// 格式化日期
const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 上传前验证
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 图标上传成功
const handleIconSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    formData.iconUrl = response.data.url
    ElMessage.success('图标上传成功')
  } else {
    ElMessage.error(response.message || '图标上传失败')
  }
}

// 封面上传成功
const handleCoverSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    formData.coverUrl = response.data.url
    ElMessage.success('封面上传成功')
  } else {
    ElMessage.error(response.message || '封面上传失败')
  }
}

// 重置表单
const resetForm = () => {
  formData.siteId = selectedSite.value || 0
  formData.parentId = undefined
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.iconUrl = ''
  formData.coverUrl = ''
  formData.sortOrder = 0
  formData.isVisible = true
  formData.seoTitle = ''
  formData.seoKeywords = ''
  formData.seoDescription = ''
  activeTab.value = 'basic'
  formRef.value?.clearValidate()
}

// 新增分类
const handleAdd = () => {
  if (!selectedSite.value) {
    ElMessage.warning('请先选择站点')
    return
  }
  isEdit.value = false
  dialogTitle.value = '新增分类'
  resetForm()
  dialogVisible.value = true
}

// 添加子分类
const handleAddChild = (row: Category) => {
  isEdit.value = false
  dialogTitle.value = '新增子分类'
  resetForm()
  formData.parentId = row.id
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row: Category) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 切换可见性
const handleToggleVisibility = async (row: Category) => {
  try {
    const newVisibility = !row.isVisible
    await updateCategoryVisibilityApi(row.id!, newVisibility)
    ElMessage.success('更新成功')
    loadCategoryTree()
  } catch (error: any) {
    console.error('更新可见性失败:', error)
    ElMessage.error(error.message || '更新可见性失败')
  }
}

// 删除分类
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${row.name}"吗?`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteCategoryApi(row.id!)
    ElMessage.success('删除成功')
    loadCategoryTree()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
      ElMessage.error(error.message || '删除分类失败')
    }
  }
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateCategoryApi(formData.id!, formData)
          ElMessage.success('更新成功')
        } else {
          await createCategoryApi(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadCategoryTree()
      } catch (error: any) {
        console.error('保存分类失败:', error)
        ElMessage.error(error.message || '保存分类失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadSites()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}

/* 图标上传样式 */
.icon-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.icon-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.icon-image {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

/* 封面上传样式 */
.cover-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
  width: 300px;
  height: 169px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.cover-image {
  width: 300px;
  height: 169px;
  display: block;
  object-fit: cover;
}
</style>


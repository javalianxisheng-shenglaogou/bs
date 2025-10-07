<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ t('category.title') }}</span>
          <el-button type="primary" @click="handleAdd">{{ t('category.add') }}</el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item :label="t('site.title')">
          <el-select v-model="selectedSite" :placeholder="t('category.placeholder.selectSite')" clearable style="width: 200px">
            <el-option
              v-for="site in siteList"
              :key="site.id"
              :label="site.name"
              :value="site.id || 0"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCategoryTree">{{ t('category.query') }}</el-button>
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
        <el-table-column prop="name" :label="t('category.fields.name')" width="250">
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
        <el-table-column prop="code" :label="t('category.fields.code')" width="150" />
        <el-table-column prop="sortOrder" :label="t('category.fields.sortOrder')" width="80" align="center" />
        <el-table-column prop="level" :label="t('category.fields.level')" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">L{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isVisible" :label="t('category.fields.isVisible')" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isVisible ? 'success' : 'danger'" size="small">
              {{ row.isVisible ? t('category.status.visible') : t('category.status.hidden') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="t('category.fields.description')" min-width="150" show-overflow-tooltip />
        <el-table-column :label="t('category.fields.createdAt')" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" width="320" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleAddChild(row)" link>
              <el-icon><Plus /></el-icon> {{ t('category.actions.addChild') }}
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)" link>
              <el-icon><Edit /></el-icon> {{ t('category.actions.edit') }}
            </el-button>
            <el-button
              :type="row.isVisible ? 'warning' : 'success'"
              size="small"
              @click="handleToggleVisibility(row)"
              link
            >
              <el-icon><View v-if="!row.isVisible" /><Hide v-else /></el-icon>
              {{ row.isVisible ? t('category.actions.hide') : t('category.actions.show') }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" link>
              <el-icon><Delete /></el-icon> {{ t('category.actions.delete') }}
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
          <el-tab-pane :label="t('category.tabs.basic')" name="basic">
            <el-form-item :label="t('site.title')" prop="siteId">
              <el-select v-model="formData.siteId" :placeholder="t('category.placeholder.selectSite')" :disabled="isEdit" style="width: 100%;">
                <el-option
                  v-for="site in siteList"
                  :key="site.id"
                  :label="site.name"
                  :value="site.id || 0"
                />
              </el-select>
            </el-form-item>

            <el-form-item :label="t('category.fields.parentId')" prop="parentId">
              <el-tree-select
                v-model="formData.parentId"
                :data="categoryTreeOptions"
                :props="{ label: 'name', value: 'id' }"
                :placeholder="t('category.placeholder.selectParent')"
                clearable
                check-strictly
                :disabled="isEdit"
                style="width: 100%;"
              />
            </el-form-item>

            <el-form-item :label="t('category.fields.name')" prop="name">
              <el-input v-model="formData.name" :placeholder="t('category.placeholder.name')" />
            </el-form-item>

            <el-form-item :label="t('category.fields.code')" prop="code">
              <el-input v-model="formData.code" :placeholder="t('category.placeholder.code')" />
            </el-form-item>

            <el-form-item :label="t('category.fields.description')">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                :placeholder="t('category.placeholder.description')"
              />
            </el-form-item>

            <el-form-item :label="t('category.fields.sortOrder')">
              <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">{{ t('category.tips.sortOrder') }}</span>
            </el-form-item>

            <el-form-item :label="t('category.fields.isVisible')">
              <el-switch v-model="formData.isVisible" />
            </el-form-item>
          </el-tab-pane>

          <!-- 图片设置 -->
          <el-tab-pane :label="t('category.tabs.images')" name="images">
            <el-form-item :label="t('category.fields.iconUrl')">
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
                {{ t('category.tips.iconSize') }}
              </div>
              <el-button v-if="formData.iconUrl" size="small" type="danger" @click="formData.iconUrl = ''" style="margin-top: 8px;">
                {{ t('category.tips.deleteIcon') }}
              </el-button>
            </el-form-item>

            <el-form-item :label="t('category.fields.coverUrl')">
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
                {{ t('category.tips.coverSize') }}
              </div>
              <el-button v-if="formData.coverUrl" size="small" type="danger" @click="formData.coverUrl = ''" style="margin-top: 8px;">
                {{ t('category.tips.deleteCover') }}
              </el-button>
            </el-form-item>
          </el-tab-pane>

          <!-- SEO设置 -->
          <el-tab-pane :label="t('category.tabs.seo')" name="seo">
            <el-form-item :label="t('category.fields.seoTitle')">
              <el-input v-model="formData.seoTitle" :placeholder="t('category.placeholder.seoTitle')" maxlength="100" show-word-limit />
            </el-form-item>

            <el-form-item :label="t('category.fields.seoKeywords')">
              <el-input v-model="formData.seoKeywords" :placeholder="t('category.placeholder.seoKeywords')" maxlength="200" show-word-limit />
            </el-form-item>

            <el-form-item :label="t('category.fields.seoDescription')">
              <el-input
                v-model="formData.seoDescription"
                type="textarea"
                :rows="4"
                :placeholder="t('category.placeholder.seoDescription')"
                maxlength="300"
                show-word-limit
              />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.button.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">{{ t('common.button.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules, UploadProps } from 'element-plus'
import { Plus, Edit, Delete, View, Hide, Folder } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import {
  getCategoryTreeApi,
  createCategoryApi,
  updateCategoryApi,
  deleteCategoryApi,
  updateCategoryVisibilityApi,
  type Category
} from '@/api/category'
import { getAllSitesApi, type Site } from '@/api/site'
import { useUserStore } from '@/store/user'

const { t } = useI18n()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const selectedSite = ref<number | undefined>(undefined)
const categoryTree = ref<Category[]>([])
const categoryTreeOptions = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = computed(() => isEdit.value ? t('category.edit') : t('category.add'))
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const siteList = ref<Site[]>([])
const activeTab = ref('basic')

// 上传配置
const uploadUrl = computed(() => '/api/files/upload/category')
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
const formRules = computed<FormRules>(() => ({
  siteId: [
    { required: true, message: t('category.validation.siteRequired'), trigger: 'change' }
  ],
  name: [
    { required: true, message: t('category.validation.nameRequired'), trigger: 'blur' },
    { max: 100, message: t('category.validation.nameMaxLength'), trigger: 'blur' }
  ],
  code: [
    { required: true, message: t('category.validation.codeRequired'), trigger: 'blur' },
    { max: 50, message: t('category.validation.codeMaxLength'), trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: t('category.validation.codePattern'), trigger: 'blur' }
  ]
}))

// 加载站点列表
const loadSites = async () => {
  try {
    const response = await getAllSitesApi()
    console.log('✅ 站点列表响应:', response)
    if (response.code === 200 && response.data) {
      siteList.value = Array.isArray(response.data) ? response.data : []
      if (siteList.value.length > 0 && !selectedSite.value) {
        selectedSite.value = siteList.value[0].id
      }
    } else {
      siteList.value = []
      ElMessage.error(response.message || t('category.message.loadSitesFailed'))
    }
  } catch (error: any) {
    console.error('❌ 加载站点列表失败:', error)
    siteList.value = []
    ElMessage.error(error.message || t('category.message.loadSitesFailed'))
  }
}

// 加载分类树
const loadCategoryTree = async () => {
  if (!selectedSite.value) {
    ElMessage.warning(t('category.message.selectSiteFirst'))
    return
  }

  loading.value = true
  console.log('🔍 开始加载分类树，站点ID:', selectedSite.value)

  try {
    const response = await getCategoryTreeApi(selectedSite.value)
    console.log('✅ 分类树响应:', response)

    if (response.code === 200 && response.data) {
      categoryTree.value = Array.isArray(response.data) ? response.data : []
      categoryTreeOptions.value = Array.isArray(response.data) ? response.data : []
      console.log('✅ 分类树加载成功，数量:', categoryTree.value.length)
    } else {
      categoryTree.value = []
      categoryTreeOptions.value = []
      ElMessage.error(response.message || t('category.message.loadTreeFailed'))
    }
  } catch (error: any) {
    console.error('❌ 加载分类树失败:', error)
    categoryTree.value = []
    categoryTreeOptions.value = []
    ElMessage.error(error.message || t('category.message.loadTreeFailed'))
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
    ElMessage.error(t('category.message.onlyJpgPng'))
    return false
  }
  if (!isLt2M) {
    ElMessage.error(t('category.message.sizeTooLarge'))
    return false
  }
  return true
}

// 图标上传成功
const handleIconSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    formData.iconUrl = response.data.url
    ElMessage.success(t('category.message.iconUploadSuccess'))
  } else {
    ElMessage.error(response.message || t('category.message.uploadFailed'))
  }
}

// 封面上传成功
const handleCoverSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    formData.coverUrl = response.data.url
    ElMessage.success(t('category.message.coverUploadSuccess'))
  } else {
    ElMessage.error(response.message || t('category.message.uploadFailed'))
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
    ElMessage.warning(t('category.message.selectSiteFirst'))
    return
  }
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 添加子分类
const handleAddChild = (row: Category) => {
  isEdit.value = false
  resetForm()
  formData.parentId = row.id
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row: Category) => {
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 切换可见性
const handleToggleVisibility = async (row: Category) => {
  try {
    const newVisibility = !row.isVisible
    await updateCategoryVisibilityApi(row.id!, newVisibility)
    ElMessage.success(t('category.message.updateSuccess'))
    loadCategoryTree()
  } catch (error: any) {
    console.error('更新可见性失败:', error)
    ElMessage.error(error.message || t('category.message.saveFailed'))
  }
}

// 删除分类
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm(
      t('category.message.deleteConfirm', { name: row.name }),
      t('common.warning'),
      {
        confirmButtonText: t('common.button.confirm'),
        cancelButtonText: t('common.button.cancel'),
        type: 'warning'
      }
    )

    await deleteCategoryApi(row.id!)
    ElMessage.success(t('category.message.deleteSuccess'))
    loadCategoryTree()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
      ElMessage.error(error.message || t('category.message.deleteFailed'))
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
          ElMessage.success(t('category.message.updateSuccess'))
        } else {
          await createCategoryApi(formData)
          ElMessage.success(t('category.message.createSuccess'))
        }
        dialogVisible.value = false
        loadCategoryTree()
      } catch (error: any) {
        console.error('保存分类失败:', error)
        ElMessage.error(error.message || t('category.message.saveFailed'))
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
/* 页面动画 */
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

:deep(.el-card) {
  animation: fadeIn 0.5s ease-in;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.filter-form {
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


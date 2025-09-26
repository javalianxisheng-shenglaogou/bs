<template>
  <div class="categories-page">
    <div class="page-header">
      <h1>分类管理</h1>
      <el-button type="primary" @click="showCreateDialog = true" :disabled="!currentSite">
        <el-icon><Plus /></el-icon>
        创建分类
      </el-button>
    </div>

    <!-- 站点选择提示 -->
    <el-alert
      v-if="!currentSite"
      title="请先选择一个站点"
      description="分类管理需要在特定站点下进行，请在顶部选择一个站点。"
      type="warning"
      :closable="false"
      show-icon
      class="site-alert"
    />

    <!-- 分类列表 -->
    <el-card v-if="currentSite" class="categories-card">
      <template #header>
        <div class="card-header">
          <span>{{ currentSite.name }} - 分类列表</span>
          <el-button text @click="fetchCategories">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-table
          :data="categories"
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          empty-text="暂无分类数据"
        >
          <el-table-column prop="name" label="分类名称" min-width="200">
            <template #default="{ row }">
              <div class="category-name">
                <el-icon v-if="row.children && row.children.length > 0"><Folder /></el-icon>
                <el-icon v-else><Document /></el-icon>
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="code" label="分类代码" width="150" />

          <el-table-column prop="description" label="描述" min-width="200">
            <template #default="{ row }">
              <span>{{ row.description || '-' }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="sortOrder" label="排序" width="80" align="center" />

          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="editCategory(row)">
                编辑
              </el-button>
              <el-button text type="primary" @click="addChildCategory(row)">
                添加子分类
              </el-button>
              <el-button text type="danger" @click="deleteCategory(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 创建/编辑分类对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingCategory ? '编辑分类' : '创建分类'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="categoryForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>

        <el-form-item label="分类代码" prop="code">
          <el-input v-model="categoryForm.code" placeholder="请输入分类代码（英文）" />
        </el-form-item>

        <el-form-item label="父分类" prop="parentCategoryId">
          <el-tree-select
            v-model="categoryForm.parentCategoryId"
            :data="categoryTreeOptions"
            :props="{ label: 'name', value: 'id' }"
            placeholder="选择父分类（可选）"
            clearable
            check-strictly
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
          />
        </el-form-item>

        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="categoryForm.sortOrder"
            :min="0"
            :max="999"
            placeholder="排序值"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editingCategory ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Refresh, Folder, Document } from '@element-plus/icons-vue'
import { useAppStore } from '../stores/app'
import { categoriesApi, type CategoryCreateRequest, type CategoryUpdateRequest } from '../api/categories'
import type { ContentCategory } from '../api'

const appStore = useAppStore()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const showCreateDialog = ref(false)
const categories = ref<ContentCategory[]>([])
const editingCategory = ref<ContentCategory | null>(null)

// 表单相关
const formRef = ref<FormInstance>()
const categoryForm = reactive<CategoryCreateRequest>({
  name: '',
  code: '',
  description: '',
  sortOrder: 0,
  parentCategoryId: undefined,
  siteId: 0
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入分类代码', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9_-]*$/, message: '分类代码必须以字母开头，只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ]
}

// 计算属性
const currentSite = computed(() => appStore.currentSite)

const categoryTreeOptions = computed(() => {
  const buildTree = (items: ContentCategory[], excludeId?: number): any[] => {
    return items
      .filter(item => item.id !== excludeId)
      .map(item => ({
        id: item.id,
        name: item.name,
        children: item.children ? buildTree(item.children, excludeId) : []
      }))
  }
  return buildTree(categories.value, editingCategory.value?.id)
})

// 方法
const fetchCategories = async () => {
  if (!currentSite.value) return

  loading.value = true
  try {
    const response = await categoriesApi.getCategoryTree(currentSite.value.id)
    categories.value = response.data
  } catch (error) {
    console.error('Failed to fetch categories:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const editCategory = (category: ContentCategory) => {
  editingCategory.value = category
  categoryForm.name = category.name
  categoryForm.code = category.code
  categoryForm.description = category.description || ''
  categoryForm.sortOrder = category.sortOrder || 0
  categoryForm.parentCategoryId = category.parentCategoryId
  categoryForm.siteId = currentSite.value!.id
  showCreateDialog.value = true
}

const addChildCategory = (parentCategory: ContentCategory) => {
  resetForm()
  categoryForm.parentCategoryId = parentCategory.id
  categoryForm.siteId = currentSite.value!.id
  showCreateDialog.value = true
}

const deleteCategory = async (category: ContentCategory) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await categoriesApi.deleteCategory(category.id)
    ElMessage.success('删除成功')
    await fetchCategories()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to delete category:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value || !currentSite.value) return

  try {
    await formRef.value.validate()

    submitting.value = true
    categoryForm.siteId = currentSite.value.id

    if (editingCategory.value) {
      // 更新分类
      const { siteId, ...updateData } = categoryForm
      await categoriesApi.updateCategory(editingCategory.value.id, updateData)
      ElMessage.success('更新成功')
    } else {
      // 创建分类
      await categoriesApi.createCategory(categoryForm)
      ElMessage.success('创建成功')
    }

    showCreateDialog.value = false
    await fetchCategories()
  } catch (error) {
    console.error('Failed to submit category:', error)
    ElMessage.error(editingCategory.value ? '更新失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  editingCategory.value = null
  Object.assign(categoryForm, {
    name: '',
    code: '',
    description: '',
    sortOrder: 0,
    parentCategoryId: undefined,
    siteId: 0
  })
  formRef.value?.resetFields()
}

// 监听站点变化
watch(currentSite, (newSite) => {
  if (newSite) {
    fetchCategories()
  } else {
    categories.value = []
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  appStore.setBreadcrumbs([{ title: '分类管理' }])
})
</script>

<style scoped>
.categories-page {
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

.categories-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-name .el-icon {
  color: #409eff;
}

:deep(.el-table .el-table__row) {
  cursor: pointer;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-tree-select) {
  width: 100%;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .categories-page {
    padding: 16px;
  }
}
</style>

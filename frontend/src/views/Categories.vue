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
        :data="categoryTree"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" width="300" />
        <el-table-column prop="code" label="分类编码" width="150" />
        <el-table-column prop="sortOrder" label="排序" width="100" align="center" />
        <el-table-column prop="level" label="层级" width="100" align="center" />
        <el-table-column prop="isVisible" label="可见性" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isVisible ? 'success' : 'danger'">
              {{ row.isVisible ? '可见' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleAddChild(row)">添加子分类</el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.isVisible ? 'warning' : 'success'"
              size="small"
              @click="handleToggleVisibility(row)"
            >
              {{ row.isVisible ? '隐藏' : '显示' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="站点" prop="siteId">
          <el-select v-model="formData.siteId" placeholder="请选择站点" :disabled="isEdit">
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
        </el-form-item>

        <el-form-item label="是否可见">
          <el-switch v-model="formData.isVisible" />
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
import {
  getCategoryTreeApi,
  createCategoryApi,
  updateCategoryApi,
  deleteCategoryApi,
  updateCategoryVisibilityApi,
  type Category
} from '@/api/category'
import { getAllSitesApi, type Site } from '@/api/site'

// 响应式数据
const loading = ref(false)
const selectedSite = ref<number | undefined>(undefined)
const categoryTree = ref<Category[]>([])
const categoryTreeOptions = ref<Category[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const siteList = ref<Site[]>([])

// 表单数据
const formData = reactive<Category>({
  siteId: 0,
  name: '',
  code: '',
  description: '',
  sortOrder: 0,
  isVisible: true
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

// 重置表单
const resetForm = () => {
  formData.siteId = selectedSite.value || 0
  formData.parentId = undefined
  formData.name = ''
  formData.code = ''
  formData.description = ''
  formData.sortOrder = 0
  formData.isVisible = true
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
</style>


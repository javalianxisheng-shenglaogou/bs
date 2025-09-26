<template>
  <div class="sites-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1>站点管理</h1>
          <p class="header-desc">管理您的所有站点，创建和配置多站点内容管理系统</p>
        </div>
        <el-button type="primary" size="large" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          创建站点
        </el-button>
      </div>
    </div>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Search /></el-icon>
          <span>搜索筛选</span>
        </div>
      </template>
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索站点名称或域名"
            clearable
            prefix-icon="Search"
            @keyup.enter="handleSearch"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable style="width: 140px">
            <el-option label="活跃" value="ACTIVE">
              <div class="status-option">
                <el-tag type="success" size="small">活跃</el-tag>
              </div>
            </el-option>
            <el-option label="维护中" value="MAINTENANCE">
              <div class="status-option">
                <el-tag type="warning" size="small">维护中</el-tag>
              </div>
            </el-option>
            <el-option label="禁用" value="INACTIVE">
              <div class="status-option">
                <el-tag type="danger" size="small">禁用</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 站点列表 -->
    <el-card class="sites-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><OfficeBuilding /></el-icon>
            <span>站点列表</span>
            <el-tag type="info" size="small">{{ pagination.total }} 个站点</el-tag>
          </div>
          <el-button text @click="fetchSites">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="sites"
        style="width: 100%"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'childSites', hasChildren: 'hasChildren' }"
        class="sites-table"
        :header-cell-style="{ background: '#fafafa', color: '#606266' }"
        empty-text="暂无站点数据"
      >
        <el-table-column prop="name" label="站点信息" min-width="280">
          <template #default="{ row }">
            <div class="site-info">
              <div class="site-main">
                <div class="site-name">
                  <el-icon class="site-icon"><OfficeBuilding /></el-icon>
                  <span class="name-text">{{ row.name }}</span>
                  <el-tag v-if="row.parentSite" size="small" type="warning" class="site-tag">
                    子站点 ({{ row.parentSite.name }})
                  </el-tag>
                </div>
                <div class="site-meta">
                  <el-tag size="small" type="info" effect="plain">{{ row.code }}</el-tag>
                  <span class="site-domain" v-if="row.domain">
                    <el-icon><Link /></el-icon>
                    {{ row.domain }}
                  </span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              :icon="getStatusIcon(row.status)"
              effect="light"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="info" text @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button size="small" type="primary" text @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-dropdown @command="(command) => handleCommand(command, row)" trigger="click">
                <el-button size="small" text>
                  <el-icon><MoreFilled /></el-icon>
                  更多
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="toggle-status">
                      <el-icon>
                        <component :is="row.status === 'ACTIVE' ? 'CircleClose' : 'CircleCheck'" />
                      </el-icon>
                      {{ row.status === 'ACTIVE' ? '禁用站点' : '启用站点' }}
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除站点
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 创建/编辑站点对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingSite ? '编辑站点' : '创建站点'"
      width="680px"
      @close="handleDialogClose"
      class="site-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="siteFormRef"
        :model="siteForm"
        :rules="siteRules"
        label-width="120px"
        class="site-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="站点名称" prop="name">
              <el-input
                v-model="siteForm.name"
                placeholder="请输入站点名称"
                prefix-icon="OfficeBuilding"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="站点代码" prop="code">
              <el-input
                v-model="siteForm.code"
                placeholder="请输入站点代码"
                prefix-icon="Key"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="域名" prop="domain">
          <el-input
            v-model="siteForm.domain"
            placeholder="请输入域名，如：example.com"
            prefix-icon="Link"
            clearable
          />
        </el-form-item>
        
        <!-- 父站点选择 - 可选功能，默认隐藏 -->
        <el-form-item v-if="showAdvancedOptions" label="父站点" prop="parentSiteId">
          <el-select v-model="siteForm.parentSiteId" placeholder="选择父站点（可选）" clearable>
            <el-option
              v-for="site in availableParentSites"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
          <div class="form-tip">选择父站点可创建子站点，留空则创建独立站点</div>
        </el-form-item>

        <!-- 高级选项切换 -->
        <el-form-item>
          <el-button
            type="text"
            @click="showAdvancedOptions = !showAdvancedOptions"
            style="padding: 0; color: #409eff;"
          >
            <el-icon style="margin-right: 4px;">
              <component :is="showAdvancedOptions ? 'ArrowUp' : 'ArrowDown'" />
            </el-icon>
            {{ showAdvancedOptions ? '隐藏高级选项' : '显示高级选项' }}
          </el-button>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="siteForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入站点描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ editingSite ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import { useAppStore } from '../stores/app'
import { sitesApi, type SiteCreateRequest, type SiteUpdateRequest } from '../api/sites'
import type { Site } from '../api'

const router = useRouter()
const appStore = useAppStore()

const loading = ref(false)
const submitting = ref(false)
const showCreateDialog = ref(false)
const showAdvancedOptions = ref(false)
const editingSite = ref<Site | null>(null)
const siteFormRef = ref<FormInstance>()

const sites = ref<Site[]>([])
const allSites = ref<Site[]>([])

const searchForm = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const siteForm = reactive<SiteCreateRequest>({
  name: '',
  code: '',
  domain: '',
  description: '',
  parentSiteId: undefined
})

const siteRules: FormRules = {
  name: [
    { required: true, message: '请输入站点名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入站点代码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '站点代码只能包含字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  domain: [
    { pattern: /^[a-zA-Z0-9.-]*$/, message: '域名格式不正确', trigger: 'blur' }
  ]
}

const availableParentSites = computed(() => {
  return allSites.value.filter(site => 
    !editingSite.value || site.id !== editingSite.value.id
  )
})

// 获取站点列表
const fetchSites = async () => {
  loading.value = true
  try {
    const response = await sitesApi.getSites({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status || undefined
    })
    
    sites.value = response.data.content
    pagination.total = response.data.totalElements
    
    // 获取所有站点用于父站点选择
    if (allSites.value.length === 0) {
      const allResponse = await sitesApi.getSites({ page: 1, size: 1000 })
      allSites.value = allResponse.data.content
    }
  } catch (error) {
    console.error('Failed to fetch sites:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchSites()
}

// 重置搜索
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  pagination.page = 1
  fetchSites()
}

// 分页变化
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchSites()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  fetchSites()
}

// 查看站点
const handleView = (site: Site) => {
  router.push(`/sites/${site.id}`)
}

// 编辑站点
const handleEdit = (site: Site) => {
  editingSite.value = site
  Object.assign(siteForm, {
    name: site.name,
    code: site.code,
    domain: site.domain,
    description: site.description,
    parentSiteId: site.parentSite?.id
  })
  showCreateDialog.value = true
}

// 处理命令
const handleCommand = async (command: string, site: Site) => {
  switch (command) {
    case 'toggle-status':
      await handleToggleStatus(site)
      break
    case 'delete':
      await handleDelete(site)
      break
  }
}

// 切换状态
const handleToggleStatus = async (site: Site) => {
  try {
    const newStatus = site.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
    await sitesApi.updateSiteStatus(site.id, newStatus)
    ElMessage.success('状态更新成功')
    fetchSites()
  } catch (error) {
    console.error('Failed to toggle status:', error)
  }
}

// 删除站点
const handleDelete = async (site: Site) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除站点"${site.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await sitesApi.deleteSite(site.id)
    ElMessage.success('删除成功')
    fetchSites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete site:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!siteFormRef.value) return
  
  try {
    await siteFormRef.value.validate()
    
    submitting.value = true
    
    if (editingSite.value) {
      await sitesApi.updateSite(editingSite.value.id, siteForm as SiteUpdateRequest)
      ElMessage.success('更新成功')
    } else {
      await sitesApi.createSite(siteForm)
      ElMessage.success('创建成功')
    }
    
    showCreateDialog.value = false
    fetchSites()
  } catch (error) {
    console.error('Failed to submit:', error)
  } finally {
    submitting.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  editingSite.value = null
  showAdvancedOptions.value = false  // 重置高级选项显示状态
  Object.assign(siteForm, {
    name: '',
    code: '',
    domain: '',
    description: '',
    parentSiteId: undefined
  })
  siteFormRef.value?.resetFields()
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'ACTIVE': return 'success'
    case 'MAINTENANCE': return 'warning'
    case 'INACTIVE': return 'danger'
    default: return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'ACTIVE': return '活跃'
    case 'MAINTENANCE': return '维护中'
    case 'INACTIVE': return '禁用'
    default: return '未知'
  }
}

// 获取状态图标
const getStatusIcon = (status: string) => {
  switch (status) {
    case 'ACTIVE': return 'CircleCheck'
    case 'MAINTENANCE': return 'Warning'
    case 'INACTIVE': return 'CircleClose'
    default: return 'QuestionFilled'
  }
}

onMounted(() => {
  appStore.setBreadcrumbs([{ title: '站点管理' }])
  fetchSites()
})
</script>

<style scoped>
.sites-page {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-info h1 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
}

.header-desc {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
}

.search-card :deep(.el-card__header) {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.search-form {
  padding: 8px 0;
}

.status-option {
  display: flex;
  align-items: center;
}

.sites-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.sites-card :deep(.el-card__header) {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sites-table {
  border-radius: 8px;
  overflow: hidden;
}

.site-info {
  padding: 8px 0;
}

.site-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.site-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #303133;
}

.site-icon {
  color: #409eff;
  font-size: 16px;
}

.name-text {
  font-size: 15px;
}

.site-tag {
  margin-left: auto;
}

.site-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.site-domain {
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sites-page {
    padding: 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form :deep(.el-form-item) {
    margin-right: 0;
    margin-bottom: 16px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}
</style>

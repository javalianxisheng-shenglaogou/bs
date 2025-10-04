<template>
  <div class="sites">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>站点列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增站点
          </el-button>
        </div>
      </template>

      <!-- 站点卡片列表 -->
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="8" v-for="site in siteList" :key="site.id" style="margin-bottom: 20px">
          <el-card shadow="hover" class="site-card">
            <template #header>
              <div class="site-header">
                <span class="site-name">{{ site.name }}</span>
                <el-tag :type="site.status === 'ACTIVE' ? 'success' : 'info'" size="small">
                  {{ site.status === 'ACTIVE' ? '运行中' : '已停用' }}
                </el-tag>
              </div>
            </template>
            <div class="site-info">
              <div class="site-logo" v-if="site.logoUrl">
                <img :src="site.logoUrl" alt="Logo" />
              </div>
              <p><strong>站点代码：</strong>{{ site.code }}</p>
              <p><strong>域名：</strong><a :href="`http://${site.domain}`" target="_blank">{{ site.domain }}</a></p>
              <p><strong>描述：</strong>{{ site.description || '暂无描述' }}</p>
              <p><strong>语言/时区：</strong>{{ site.language }} / {{ site.timezone }}</p>
              <p v-if="site.isDefault"><el-tag type="warning" size="small">默认站点</el-tag></p>
              <p><strong>创建时间：</strong>{{ formatDate(site.createdAt) }}</p>
            </div>
            <template #footer>
              <div class="site-actions">
                <el-button type="primary" size="small" @click="handleEdit(site)">编辑</el-button>
                <el-button type="success" size="small" @click="handleConfig(site)">配置</el-button>
                <el-button type="danger" size="small" @click="handleDelete(site)">删除</el-button>
              </div>
            </template>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && siteList.length === 0" description="暂无站点数据" />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="站点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入站点名称" />
        </el-form-item>

        <el-form-item label="站点代码" prop="code">
          <el-input
            v-model="formData.code"
            placeholder="请输入站点代码(小写字母、数字、下划线、连字符)"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item label="站点域名" prop="domain">
          <el-input v-model="formData.domain" placeholder="请输入站点域名" />
        </el-form-item>

        <el-form-item label="站点描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入站点描述"
          />
        </el-form-item>

        <el-form-item label="默认语言">
          <el-select v-model="formData.language" placeholder="请选择默认语言">
            <el-option label="简体中文" value="zh_CN" />
            <el-option label="English" value="en_US" />
            <el-option label="繁體中文" value="zh_TW" />
          </el-select>
        </el-form-item>

        <el-form-item label="时区">
          <el-select v-model="formData.timezone" placeholder="请选择时区">
            <el-option label="Asia/Shanghai" value="Asia/Shanghai" />
            <el-option label="Asia/Hong_Kong" value="Asia/Hong_Kong" />
            <el-option label="America/New_York" value="America/New_York" />
            <el-option label="Europe/London" value="Europe/London" />
          </el-select>
        </el-form-item>

        <el-form-item label="站点状态">
          <el-radio-group v-model="formData.status">
            <el-radio label="ACTIVE">运行中</el-radio>
            <el-radio label="INACTIVE">已停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="默认站点">
          <el-switch v-model="formData.isDefault" />
        </el-form-item>

        <el-divider content-position="left">SEO设置</el-divider>

        <el-form-item label="SEO标题">
          <el-input v-model="formData.seoTitle" placeholder="请输入SEO标题" />
        </el-form-item>

        <el-form-item label="SEO关键词">
          <el-input v-model="formData.seoKeywords" placeholder="请输入SEO关键词,多个用逗号分隔" />
        </el-form-item>

        <el-form-item label="SEO描述">
          <el-input
            v-model="formData.seoDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入SEO描述"
          />
        </el-form-item>

        <el-divider content-position="left">联系信息</el-divider>

        <el-form-item label="联系邮箱">
          <el-input v-model="formData.contactEmail" placeholder="请输入联系邮箱" />
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="联系地址">
          <el-input v-model="formData.contactAddress" placeholder="请输入联系地址" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 站点配置对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      title="站点配置"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本配置" name="basic">
          <el-form label-width="120px">
            <el-form-item label="Logo">
              <el-upload
                class="logo-uploader"
                action="/api/files/upload"
                :show-file-list="false"
                :on-success="handleLogoSuccess"
                :headers="uploadHeaders"
              >
                <img v-if="configData.logoUrl" :src="configData.logoUrl" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>

            <el-form-item label="Favicon">
              <el-upload
                class="favicon-uploader"
                action="/api/files/upload"
                :show-file-list="false"
                :on-success="handleFaviconSuccess"
                :headers="uploadHeaders"
              >
                <img v-if="configData.faviconUrl" :src="configData.faviconUrl" class="favicon" />
                <el-icon v-else class="favicon-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="主题配置" name="theme">
          <el-form label-width="120px">
            <el-form-item label="主题色">
              <el-color-picker v-model="themeConfig.primaryColor" />
            </el-form-item>
            <el-form-item label="辅助色">
              <el-color-picker v-model="themeConfig.secondaryColor" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="高级配置" name="advanced">
          <el-form label-width="120px">
            <el-form-item label="自定义CSS">
              <el-input
                v-model="advancedConfig.customCss"
                type="textarea"
                :rows="10"
                placeholder="请输入自定义CSS代码"
              />
            </el-form-item>
            <el-form-item label="自定义JS">
              <el-input
                v-model="advancedConfig.customJs"
                type="textarea"
                :rows="10"
                placeholder="请输入自定义JavaScript代码"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConfig">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAllSitesApi,
  createSiteApi,
  updateSiteApi,
  deleteSiteApi,
  type Site
} from '@/api/site'

// 响应式数据
const loading = ref(false)
const siteList = ref<Site[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增站点')
const formRef = ref<FormInstance>()
const isEdit = ref(false)

// 表单数据
const formData = reactive<Site>({
  name: '',
  code: '',
  domain: '',
  description: '',
  language: 'zh_CN',
  timezone: 'Asia/Shanghai',
  status: 'ACTIVE',
  isDefault: false
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入站点名称', trigger: 'blur' },
    { max: 100, message: '站点名称长度不能超过100', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入站点代码', trigger: 'blur' },
    { max: 50, message: '站点代码长度不能超过50', trigger: 'blur' },
    { pattern: /^[a-z0-9_-]+$/, message: '站点代码只能包含小写字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  domain: [
    { required: true, message: '请输入站点域名', trigger: 'blur' },
    { max: 255, message: '站点域名长度不能超过255', trigger: 'blur' }
  ]
}

// 加载站点列表
const loadSites = async () => {
  loading.value = true
  try {
    const data = await getAllSitesApi()
    siteList.value = data
  } catch (error: any) {
    console.error('加载站点列表失败:', error)
    ElMessage.error(error.message || '加载站点列表失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  formData.name = ''
  formData.code = ''
  formData.domain = ''
  formData.description = ''
  formData.language = 'zh_CN'
  formData.timezone = 'Asia/Shanghai'
  formData.status = 'ACTIVE'
  formData.isDefault = false
  formData.seoTitle = ''
  formData.seoKeywords = ''
  formData.seoDescription = ''
  formData.contactEmail = ''
  formData.contactPhone = ''
  formData.contactAddress = ''
  formRef.value?.clearValidate()
}

// 新增站点
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增站点'
  resetForm()
  dialogVisible.value = true
}

// 编辑站点
const handleEdit = (site: Site) => {
  isEdit.value = true
  dialogTitle.value = '编辑站点'
  Object.assign(formData, site)
  dialogVisible.value = true
}

// 配置相关
const configDialogVisible = ref(false)
const activeTab = ref('basic')
const configData = reactive({
  id: 0,
  logoUrl: '',
  faviconUrl: ''
})
const themeConfig = reactive({
  primaryColor: '#409EFF',
  secondaryColor: '#67C23A'
})
const advancedConfig = reactive({
  customCss: '',
  customJs: ''
})

// 上传请求头
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

// Logo上传成功
const handleLogoSuccess = (response: any) => {
  if (response.code === 200) {
    configData.logoUrl = response.data.url
    ElMessage.success('Logo上传成功')
  }
}

// Favicon上传成功
const handleFaviconSuccess = (response: any) => {
  if (response.code === 200) {
    configData.faviconUrl = response.data.url
    ElMessage.success('Favicon上传成功')
  }
}

// 配置站点
const handleConfig = (site: Site) => {
  configData.id = site.id!
  configData.logoUrl = site.logoUrl || ''
  configData.faviconUrl = site.faviconUrl || ''
  activeTab.value = 'basic'
  configDialogVisible.value = true
}

// 保存配置
const handleSaveConfig = async () => {
  try {
    await updateSiteApi(configData.id, {
      logoUrl: configData.logoUrl,
      faviconUrl: configData.faviconUrl
    } as Site)
    ElMessage.success('配置保存成功')
    configDialogVisible.value = false
    await loadSites()
  } catch (error: any) {
    console.error('保存配置失败:', error)
    ElMessage.error(error.message || '保存配置失败')
  }
}

// 删除站点
const handleDelete = async (site: Site) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除站点 "${site.name}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteSiteApi(site.id!)
    ElMessage.success('删除成功')
    await loadSites()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除站点失败:', error)
      ElMessage.error(error.message || '删除站点失败')
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
          await updateSiteApi(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createSiteApi(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadSites()
      } catch (error: any) {
        console.error('保存站点失败:', error)
        ElMessage.error(error.message || '保存站点失败')
      }
    }
  })
}

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

.site-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.site-card :deep(.el-card__body) {
  flex: 1;
  overflow-y: auto;
}

.site-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.site-name {
  font-size: 16px;
  font-weight: bold;
}

.site-logo {
  text-align: center;
  margin-bottom: 15px;
}

.site-logo img {
  max-width: 100%;
  max-height: 80px;
  object-fit: contain;
}

.site-info p {
  margin: 8px 0;
  color: #606266;
}

.site-info a {
  color: #409eff;
  text-decoration: none;
}

.site-info a:hover {
  text-decoration: underline;
}

.site-actions {
  display: flex;
  justify-content: space-between;
}

.logo-uploader,
.favicon-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.logo-uploader:hover,
.favicon-uploader:hover {
  border-color: #409eff;
}

.logo-uploader {
  width: 178px;
  height: 178px;
}

.favicon-uploader {
  width: 64px;
  height: 64px;
}

.logo-uploader-icon,
.favicon-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.favicon-uploader-icon {
  width: 64px;
  height: 64px;
  line-height: 64px;
}

.logo {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: contain;
}

.favicon {
  width: 64px;
  height: 64px;
  display: block;
  object-fit: contain;
}
</style>


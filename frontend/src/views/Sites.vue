<template>
  <div class="sites">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ t('site.list') }}</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            {{ t('site.add') }}
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
                  {{ t(`site.status.${site.status}`) }}
                </el-tag>
              </div>
            </template>
            <div class="site-info">
              <div class="site-logo" v-if="site.logoUrl">
                <img :src="site.logoUrl" alt="Logo" />
              </div>
              <p><strong>{{ t('site.fields.code') }}：</strong>{{ site.code }}</p>
              <p><strong>{{ t('site.fields.domain') }}：</strong><a :href="`http://${site.domain}`" target="_blank">{{ site.domain }}</a></p>
              <p><strong>{{ t('site.fields.description') }}：</strong>{{ site.description || t('site.noDescription') }}</p>
              <p><strong>{{ t('site.fields.language') }}/{{ t('site.fields.timezone') }}：</strong>{{ site.language }} / {{ site.timezone }}</p>
              <p v-if="site.isDefault"><el-tag type="warning" size="small">{{ t('site.defaultSite') }}</el-tag></p>
              <p><strong>{{ t('site.fields.createdAt') }}：</strong>{{ formatDate(site.createdAt) }}</p>
            </div>
            <template #footer>
              <div class="site-actions">
                <el-button type="primary" size="small" @click="handleEdit(site)">{{ t('site.edit') }}</el-button>
                <el-button type="success" size="small" @click="handleConfig(site)">{{ t('site.config') }}</el-button>
                <el-button type="danger" size="small" @click="handleDelete(site)">{{ t('site.delete') }}</el-button>
              </div>
            </template>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && siteList.length === 0" :description="t('site.empty')" />
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
        <el-form-item :label="t('site.fields.name')" prop="name">
          <el-input v-model="formData.name" :placeholder="t('site.placeholder.name')" />
        </el-form-item>

        <el-form-item :label="t('site.fields.code')" prop="code">
          <el-input
            v-model="formData.code"
            :placeholder="t('site.placeholder.code')"
            :disabled="isEdit"
          />
        </el-form-item>

        <el-form-item :label="t('site.fields.domain')" prop="domain">
          <el-input v-model="formData.domain" :placeholder="t('site.placeholder.domain')" />
        </el-form-item>

        <el-form-item :label="t('site.fields.description')">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            :placeholder="t('site.placeholder.description')"
          />
        </el-form-item>

        <el-form-item :label="t('site.fields.language')">
          <el-select v-model="formData.language" :placeholder="t('site.placeholder.selectLanguage')">
            <el-option :label="t('site.languages.zh_CN')" value="zh_CN" />
            <el-option :label="t('site.languages.en_US')" value="en_US" />
            <el-option :label="t('site.languages.zh_TW')" value="zh_TW" />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('site.fields.timezone')">
          <el-select v-model="formData.timezone" :placeholder="t('site.placeholder.selectTimezone')">
            <el-option label="Asia/Shanghai" value="Asia/Shanghai" />
            <el-option label="Asia/Hong_Kong" value="Asia/Hong_Kong" />
            <el-option label="America/New_York" value="America/New_York" />
            <el-option label="Europe/London" value="Europe/London" />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('site.fields.status')">
          <el-radio-group v-model="formData.status">
            <el-radio label="ACTIVE">{{ t('site.status.ACTIVE') }}</el-radio>
            <el-radio label="INACTIVE">{{ t('site.status.INACTIVE') }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item :label="t('site.fields.isDefault')">
          <el-switch v-model="formData.isDefault" />
        </el-form-item>

        <el-divider content-position="left">{{ t('site.seoSettings') }}</el-divider>

        <el-form-item :label="t('site.fields.seoTitle')">
          <el-input v-model="formData.seoTitle" :placeholder="t('site.placeholder.seoTitle')" />
        </el-form-item>

        <el-form-item :label="t('site.fields.seoKeywords')">
          <el-input v-model="formData.seoKeywords" :placeholder="t('site.placeholder.seoKeywords')" />
        </el-form-item>

        <el-form-item :label="t('site.fields.seoDescription')">
          <el-input
            v-model="formData.seoDescription"
            type="textarea"
            :rows="3"
            :placeholder="t('site.placeholder.seoDescription')"
          />
        </el-form-item>

        <el-divider content-position="left">{{ t('site.contactInfo') }}</el-divider>

        <el-form-item :label="t('site.fields.contactEmail')">
          <el-input v-model="formData.contactEmail" :placeholder="t('site.placeholder.contactEmail')" />
        </el-form-item>

        <el-form-item :label="t('site.fields.contactPhone')">
          <el-input v-model="formData.contactPhone" :placeholder="t('site.placeholder.contactPhone')" />
        </el-form-item>

        <el-form-item :label="t('site.fields.contactAddress')">
          <el-input v-model="formData.contactAddress" :placeholder="t('site.placeholder.contactAddress')" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>

    <!-- 站点配置对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      :title="t('site.siteConfig')"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="t('site.basicConfig')" name="basic">
          <el-form label-width="120px">
            <el-form-item :label="t('site.fields.logo')">
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

        <el-tab-pane :label="t('site.themeConfig')" name="theme">
          <el-form label-width="120px">
            <el-form-item :label="t('site.primaryColor')">
              <el-color-picker v-model="themeConfig.primaryColor" />
            </el-form-item>
            <el-form-item :label="t('site.secondaryColor')">
              <el-color-picker v-model="themeConfig.secondaryColor" />
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane :label="t('site.advancedConfig')" name="advanced">
          <el-form label-width="120px">
            <el-form-item :label="t('site.customCss')">
              <el-input
                v-model="advancedConfig.customCss"
                type="textarea"
                :rows="10"
                :placeholder="t('site.placeholder.customCss')"
              />
            </el-form-item>
            <el-form-item :label="t('site.customJs')">
              <el-input
                v-model="advancedConfig.customJs"
                type="textarea"
                :rows="10"
                :placeholder="t('site.placeholder.customJs')"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <template #footer>
        <el-button @click="configDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSaveConfig">{{ t('site.saveConfig') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import {
  getAllSitesApi,
  createSiteApi,
  updateSiteApi,
  deleteSiteApi,
  type Site
} from '@/api/site'

const { t } = useI18n()

// 响应式数据
const loading = ref(false)
const siteList = ref<Site[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
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
    { required: true, message: () => t('site.validation.nameRequired'), trigger: 'blur' },
    { max: 100, message: () => t('site.validation.nameMaxLength'), trigger: 'blur' }
  ],
  code: [
    { required: true, message: () => t('site.validation.codeRequired'), trigger: 'blur' },
    { max: 50, message: () => t('site.validation.codeMaxLength'), trigger: 'blur' },
    { pattern: /^[a-z0-9_-]+$/, message: () => t('site.validation.codePattern'), trigger: 'blur' }
  ],
  domain: [
    { required: true, message: () => t('site.validation.domainRequired'), trigger: 'blur' },
    { max: 255, message: () => t('site.validation.domainMaxLength'), trigger: 'blur' }
  ]
}

// 加载站点列表
const loadSites = async () => {
  loading.value = true
  try {
    const response = await getAllSitesApi()
    console.log('✅ 站点列表响应:', response)
    if (response.code === 200 && response.data) {
      siteList.value = Array.isArray(response.data) ? response.data : []
    } else {
      siteList.value = []
      ElMessage.error(response.message || t('site.message.loadFailed'))
    }
  } catch (error: any) {
    console.error('❌ 加载站点列表失败:', error)
    siteList.value = []
    ElMessage.error(error.message || t('site.message.loadFailed'))
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
  dialogTitle.value = t('site.add')
  resetForm()
  dialogVisible.value = true
}

// 编辑站点
const handleEdit = (site: Site) => {
  isEdit.value = true
  dialogTitle.value = t('site.edit')
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
    ElMessage.success(t('site.message.logoUploadSuccess'))
  }
}

// Favicon上传成功
const handleFaviconSuccess = (response: any) => {
  if (response.code === 200) {
    configData.faviconUrl = response.data.url
    ElMessage.success(t('site.message.faviconUploadSuccess'))
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
    ElMessage.success(t('site.message.configSaveSuccess'))
    configDialogVisible.value = false
    await loadSites()
  } catch (error: any) {
    console.error('保存配置失败:', error)
    ElMessage.error(error.message || t('site.message.configSaveFailed'))
  }
}

// 删除站点
const handleDelete = async (site: Site) => {
  try {
    await ElMessageBox.confirm(
      t('site.message.deleteConfirm', { name: site.name }),
      t('common.warning'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning',
      }
    )

    await deleteSiteApi(site.id!)
    ElMessage.success(t('site.message.deleteSuccess'))
    await loadSites()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除站点失败:', error)
      ElMessage.error(error.message || t('site.message.deleteFailed'))
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
          ElMessage.success(t('site.message.updateSuccess'))
        } else {
          await createSiteApi(formData)
          ElMessage.success(t('site.message.createSuccess'))
        }
        dialogVisible.value = false
        await loadSites()
      } catch (error: any) {
        console.error('保存站点失败:', error)
        ElMessage.error(error.message || t('site.message.saveFailed'))
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
.sites {
  animation: fadeIn 0.5s ease-in;
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.site-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;
}

.site-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.site-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.site-card:hover::before {
  transform: scaleX(1);
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
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.site-logo {
  text-align: center;
  margin-bottom: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.site-logo img {
  max-width: 100%;
  max-height: 80px;
  object-fit: contain;
  transition: transform 0.3s ease;
}

.site-card:hover .site-logo img {
  transform: scale(1.05);
}

.site-info p {
  margin: 12px 0;
  color: #606266;
  font-size: 14px;
}

.site-info strong {
  color: #303133;
  font-weight: 600;
}

.site-info a {
  color: #409eff;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.site-info a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #409eff;
  transition: width 0.3s ease;
}

.site-info a:hover::after {
  width: 100%;
}

.site-actions {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.site-actions .el-button {
  flex: 1;
  transition: all 0.3s ease;
}

.site-actions .el-button:hover {
  transform: translateY(-2px);
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


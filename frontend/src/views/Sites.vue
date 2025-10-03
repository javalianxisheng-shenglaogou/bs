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
          <el-card shadow="hover">
            <template #header>
              <div class="site-header">
                <span class="site-name">{{ site.name }}</span>
                <el-tag :type="site.status === 'ACTIVE' ? 'success' : 'info'" size="small">
                  {{ site.status === 'ACTIVE' ? '运行中' : '已停用' }}
                </el-tag>
              </div>
            </template>
            <div class="site-info">
              <p><strong>域名：</strong>{{ site.domain }}</p>
              <p><strong>描述：</strong>{{ site.description }}</p>
              <p><strong>创建时间：</strong>{{ site.createdAt }}</p>
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
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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

// 配置站点
const handleConfig = (site: Site) => {
  ElMessage.info(`配置站点功能开发中: ${site.name}`)
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

.site-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.site-name {
  font-size: 16px;
  font-weight: bold;
}

.site-info p {
  margin: 8px 0;
  color: #606266;
}

.site-actions {
  display: flex;
  justify-content: space-between;
}
</style>


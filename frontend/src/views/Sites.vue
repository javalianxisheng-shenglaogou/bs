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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 站点数据类型
interface Site {
  id: number
  name: string
  domain: string
  description: string
  status: string
  createdAt: string
}

// 响应式数据
const loading = ref(false)
const siteList = ref<Site[]>([])

// 加载站点列表
const loadSites = async () => {
  loading.value = true
  try {
    // TODO: 调用真实API
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))

    siteList.value = [
      {
        id: 1,
        name: '官方网站',
        domain: 'www.example.com',
        description: '公司官方网站',
        status: 'ACTIVE',
        createdAt: '2025-01-01 10:00:00'
      },
      {
        id: 2,
        name: '博客站点',
        domain: 'blog.example.com',
        description: '技术博客站点',
        status: 'ACTIVE',
        createdAt: '2025-01-02 10:00:00'
      },
      {
        id: 3,
        name: '产品站点',
        domain: 'product.example.com',
        description: '产品展示站点',
        status: 'INACTIVE',
        createdAt: '2025-01-03 10:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载站点列表失败')
  } finally {
    loading.value = false
  }
}

// 新增站点
const handleAdd = () => {
  ElMessage.info('新增站点功能开发中...')
}

// 编辑站点
const handleEdit = (site: Site) => {
  ElMessage.info(`编辑站点: ${site.name}`)
}

// 配置站点
const handleConfig = (site: Site) => {
  ElMessage.info(`配置站点: ${site.name}`)
}

// 删除站点
const handleDelete = (site: Site) => {
  ElMessageBox.confirm(
    `确定要删除站点 "${site.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadSites()
  }).catch(() => {
    // 取消删除
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


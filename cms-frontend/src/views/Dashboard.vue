<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <div class="header-content">
        <div>
          <h1>仪表盘</h1>
          <p>欢迎回来，{{ authStore.user?.nickname }}！</p>
        </div>
        <div class="header-actions">
          <el-select
            v-model="selectedSiteId"
            placeholder="请选择站点"
            @change="handleSiteChange"
            style="width: 200px"
          >
            <el-option
              v-for="site in availableSites"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            >
              <span>{{ site.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ site.code }}</span>
            </el-option>
          </el-select>
        </div>
      </div>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon sites">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.siteCount }}</div>
              <div class="stats-label">站点数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon contents">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.contentCount }}</div>
              <div class="stats-label">内容数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon categories">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.categoryCount }}</div>
              <div class="stats-label">分类数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon users">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.userCount }}</div>
              <div class="stats-label">用户数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快速操作 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快速操作</span>
            </div>
          </template>
          
          <div class="action-buttons">
            <el-button type="primary" @click="$router.push('/content/create')">
              <el-icon><Plus /></el-icon>
              创建内容
            </el-button>
            <el-button @click="$router.push('/sites')">
              <el-icon><OfficeBuilding /></el-icon>
              管理站点
            </el-button>
            <el-button @click="$router.push('/categories')">
              <el-icon><Folder /></el-icon>
              管理分类
            </el-button>
            <el-button v-if="authStore.isAdmin" @click="$router.push('/users')">
              <el-icon><User /></el-icon>
              管理用户
            </el-button>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近内容</span>
              <el-button text @click="$router.push('/content')">查看全部</el-button>
            </div>
          </template>
          
          <div class="recent-content">
            <div
              v-for="content in recentContents"
              :key="content.id"
              class="content-item"
              @click="$router.push(`/content/${content.id}/edit`)"
            >
              <div class="content-title">{{ content.title }}</div>
              <div class="content-meta">
                <el-tag :type="getStatusType(content.status)" size="small">
                  {{ getStatusText(content.status) }}
                </el-tag>
                <span class="content-date">{{ formatDate(content.updatedAt) }}</span>
              </div>
            </div>
            
            <div v-if="recentContents.length === 0" class="empty-content">
              暂无内容
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useAppStore } from '../stores/app'
import { contentApi } from '../api/content'
import { sitesApi } from '../api/sites'
import type { Content, Site } from '../api'
import {
  OfficeBuilding,
  Document,
  Folder,
  User,
  Plus
} from '@element-plus/icons-vue'

const authStore = useAuthStore()
const appStore = useAppStore()

const stats = ref({
  siteCount: 0,
  contentCount: 0,
  categoryCount: 0,
  userCount: 0
})

const recentContents = ref<Content[]>([])
const availableSites = ref<Site[]>([])

// 计算属性
const selectedSiteId = computed({
  get: () => appStore.currentSite?.id || null,
  set: (value) => {
    if (value) {
      const site = availableSites.value.find(s => s.id === value)
      if (site) {
        appStore.setCurrentSite(site)
      }
    }
  }
})

// 站点选择处理
const handleSiteChange = (siteId: number) => {
  const site = availableSites.value.find(s => s.id === siteId)
  if (site) {
    appStore.setCurrentSite(site)
    // 重新获取统计数据和最近内容
    fetchStats()
    fetchRecentContents()
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取站点统计
    const sitesResponse = await sitesApi.getSites({ page: 1, size: 100 })
    stats.value.siteCount = sitesResponse.data.totalElements
    availableSites.value = sitesResponse.data.content

    // 如果没有当前站点但有站点列表，选择第一个站点
    if (!appStore.currentSite && sitesResponse.data.content.length > 0) {
      appStore.setCurrentSite(sitesResponse.data.content[0])
    }

    // 获取内容统计
    if (appStore.currentSite) {
      try {
        const contentStats = await contentApi.getContentStats(appStore.currentSite.id)
        stats.value.contentCount = contentStats.data.totalCount
      } catch (error) {
        console.warn('Failed to fetch content stats:', error)
        stats.value.contentCount = 0
      }
    } else {
      stats.value.contentCount = 0
    }

    // TODO: 获取分类和用户统计
    stats.value.categoryCount = 0
    stats.value.userCount = 0
  } catch (error) {
    console.error('Failed to fetch stats:', error)
    // 设置默认值
    stats.value.siteCount = 0
    stats.value.contentCount = 0
    stats.value.categoryCount = 0
    stats.value.userCount = 0
  }
}

// 获取最近内容
const fetchRecentContents = async () => {
  try {
    if (appStore.currentSite) {
      const response = await contentApi.getContents({
        page: 1,
        size: 5,
        sort: 'updatedAt',
        direction: 'desc',
        siteId: appStore.currentSite.id
      })
      recentContents.value = response.data.content
    } else {
      recentContents.value = []
    }
  } catch (error) {
    console.error('Failed to fetch recent contents:', error)
    recentContents.value = []
  }
}

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'PUBLISHED':
      return 'success'
    case 'DRAFT':
      return 'info'
    case 'ARCHIVED':
      return 'warning'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'PUBLISHED':
      return '已发布'
    case 'DRAFT':
      return '草稿'
    case 'ARCHIVED':
      return '已归档'
    default:
      return status
  }
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}



onMounted(() => {
  appStore.setBreadcrumbs([{ title: '仪表盘' }])
  fetchStats()
  fetchRecentContents()
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.dashboard-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.dashboard-header p {
  color: #606266;
  font-size: 16px;
  margin: 0;
}

.stats-row {
  margin-bottom: 24px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stats-icon.sites {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.contents {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-icon.categories {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stats-icon.users {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.quick-actions {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.action-buttons .el-button {
  flex: 1;
  min-width: 120px;
}

.recent-content {
  max-height: 300px;
  overflow-y: auto;
}

.content-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.content-item:hover {
  background-color: #f8f9fa;
  margin: 0 -16px;
  padding-left: 16px;
  padding-right: 16px;
}

.content-item:last-child {
  border-bottom: none;
}

.content-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.content-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-date {
  font-size: 12px;
  color: #909399;
}

.empty-content {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    min-width: auto;
  }
}
</style>

<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">
        <img src="/favicon.ico" alt="Logo" class="logo-img" />
        <span v-if="!appStore.sidebarCollapsed" class="logo-text">多站点CMS</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        
        <el-sub-menu index="sites">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>站点管理</span>
          </template>
          <el-menu-item index="/sites">站点列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/content">内容列表</el-menu-item>
          <el-menu-item index="/content/create">创建内容</el-menu-item>
          <el-menu-item index="/categories">分类管理</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/users" v-if="authStore.isAdmin">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/test">
          <el-icon><Tools /></el-icon>
          <template #title>功能测试</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            :icon="appStore.sidebarCollapsed ? Expand : Fold"
            @click="appStore.toggleSidebar"
            text
            class="collapse-btn"
          />
          
          <!-- 面包屑导航 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item
              v-for="item in appStore.breadcrumbs"
              :key="item.title"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <!-- 站点选择器 -->
          <el-select
            v-model="currentSiteId"
            placeholder="选择站点"
            @change="handleSiteChange"
            class="site-selector"
          >
            <el-option
              v-for="site in sites"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>
          
          <!-- 用户菜单 -->
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :src="authStore.user?.avatarUrl" class="user-avatar">
                {{ authStore.user?.nickname?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ authStore.user?.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useAppStore } from '../stores/app'
import { sitesApi } from '../api/sites'
import type { Site } from '../api'
import {
  House,
  OfficeBuilding,
  Document,
  User,
  Expand,
  Fold,
  ArrowDown,
  Tools
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

const sites = ref<Site[]>([])
const currentSiteId = ref<number | null>(null)

const sidebarWidth = computed(() => {
  return appStore.sidebarCollapsed ? '64px' : '200px'
})

const activeMenu = computed(() => {
  return route.path
})

// 获取站点列表
const fetchSites = async () => {
  try {
    const response = await sitesApi.getSites({ page: 1, size: 100 })
    sites.value = response.data.content
    
    // 设置默认站点
    if (sites.value.length > 0 && !currentSiteId.value) {
      const defaultSite = appStore.currentSite || sites.value[0]
      currentSiteId.value = defaultSite.id
      appStore.setCurrentSite(defaultSite)
    }
  } catch (error) {
    console.error('Failed to fetch sites:', error)
  }
}

// 处理站点切换
const handleSiteChange = (siteId: number) => {
  const site = sites.value.find(s => s.id === siteId)
  if (site) {
    appStore.setCurrentSite(site)
  }
}

// 处理用户菜单命令
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      // TODO: 实现设置页面
      break
    case 'logout':
      authStore.logout()
      router.push('/login')
      break
  }
}

onMounted(() => {
  fetchSites()
  
  // 设置当前站点ID
  if (appStore.currentSite) {
    currentSiteId.value = appStore.currentSite.id
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s ease;
}

.logo {
  display: flex;
  align-items: center;
  padding: 16px;
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 8px;
}

.logo-text {
  white-space: nowrap;
}

.sidebar-menu {
  border: none;
  background-color: transparent;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: #263445;
  color: #409eff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff;
  color: white;
}

.main-container {
  background-color: #f5f5f5;
}

.header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  margin-right: 16px;
}

.breadcrumb {
  margin-left: 16px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.site-selector {
  width: 200px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-avatar {
  margin-right: 8px;
}

.user-name {
  margin-right: 4px;
  font-size: 14px;
}

.main-content {
  padding: 20px;
  overflow-y: auto;
}
</style>

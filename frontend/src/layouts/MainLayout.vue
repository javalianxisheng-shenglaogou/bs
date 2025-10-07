<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon :size="32" color="#409EFF" class="logo-icon">
          <OfficeBuilding />
        </el-icon>
        <h2 class="logo-text">多站点CMS</h2>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="transparent"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          class="sidebar-menu"
        >
          <template v-for="route in menuRoutes" :key="route.path">
            <el-menu-item :index="route.path" class="menu-item">
              <el-icon class="menu-icon">
                <component :is="route.meta?.icon" />
              </el-icon>
              <span class="menu-title">{{ getMenuTitle(route.name as string) }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="pageTitle">{{ pageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <!-- 语言切换器 -->
            <LanguageSwitcher />

            <!-- 用户信息 -->
            <el-dropdown @command="handleCommand" class="user-dropdown">
              <span class="el-dropdown-link">
                <el-badge :is-dot="false" class="user-badge">
                  <el-avatar
                    :size="36"
                    :src="userStore.userInfo?.avatarUrl"
                    class="user-avatar"
                  >
                    <el-icon><User /></el-icon>
                  </el-avatar>
                </el-badge>
                <span class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</span>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ArrowDown, OfficeBuilding, User, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'
import { useI18n } from 'vue-i18n'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title as string || '')

/**
 * 根据权限过滤菜单路由
 */
const menuRoutes = computed(() => {
  const layoutRoute = router.getRoutes().find(r => r.name === 'Layout')
  if (!layoutRoute || !layoutRoute.children) {
    return []
  }

  return layoutRoute.children.filter((route: RouteRecordRaw) => {
    // 不在菜单中显示的路由
    if (route.meta?.showInMenu === false) {
      return false
    }

    // 超级管理员和管理员可以看到所有菜单
    if (userStore.isAdmin()) {
      return true
    }

    // 检查权限
    const permissions = route.meta?.permissions as string[] | undefined
    if (permissions && permissions.length > 0) {
      return userStore.hasAnyPermission(permissions)
    }

    return true
  })
})

// 菜单标题国际化映射
const getMenuTitle = (routeName: string) => {
  const menuTitleMap: Record<string, string> = {
    'Dashboard': t('menu.dashboard'),
    'Users': t('menu.user'),
    'Sites': t('menu.site'),
    'Contents': t('menu.content'),
    'Categories': t('menu.category'),
    'Workflows': t('menu.workflow'),
    'WorkflowInstances': t('menu.workflowConfig'),
    'WorkflowTasks': t('menu.myTasks'),
    'Logs': t('menu.logs'),
    'Profile': t('common.app.profile')
  }

  return menuTitleMap[routeName] || routeName
}

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    await userStore.logout()
    ElMessage.success(t('common.message.logoutSuccess'))
    await router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
/* 主布局 */
.main-layout {
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  background: linear-gradient(180deg, #1a2332 0%, #2d3a4f 100%);
  color: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0 20px;
}

.logo-icon {
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.logo-text {
  color: #fff;
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.menu-scrollbar {
  height: calc(100vh - 70px);
}

.sidebar-menu {
  border: none;
  padding: 10px 0;
}

.menu-item {
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.menu-item:hover {
  background: rgba(64, 158, 255, 0.1) !important;
  transform: translateX(5px);
}

.menu-item.is-active {
  background: linear-gradient(90deg, rgba(64, 158, 255, 0.2) 0%, rgba(64, 158, 255, 0.1) 100%) !important;
  border-left: 3px solid #409EFF;
}

.menu-icon {
  font-size: 18px;
  margin-right: 8px;
}

.menu-title {
  font-size: 14px;
  font-weight: 500;
}

/* 主容器 */
.main-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 顶部导航栏 */
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  padding: 0 24px;
  z-index: 10;
  transition: all 0.3s;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.header-left :deep(.el-breadcrumb__inner) {
  color: #606266;
  font-weight: 500;
}

.header-left :deep(.el-breadcrumb__inner:hover) {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 用户下拉菜单 */
.user-dropdown {
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
}

.el-dropdown-link:hover {
  background: #f5f7fa;
}

.user-avatar {
  border: 2px solid #409EFF;
  transition: all 0.3s;
}

.user-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.user-name {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.arrow-icon {
  color: #909399;
  transition: transform 0.3s;
}

.el-dropdown-link:hover .arrow-icon {
  transform: rotate(180deg);
}

/* 主内容区 */
.main-content {
  background: #f0f2f5;
  padding: 24px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  transition: all 0.3s;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f5f7fa;
  color: #409EFF;
}

:deep(.el-dropdown-menu__item .el-icon) {
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 64px !important;
  }

  .logo-text {
    display: none;
  }

  .menu-title {
    display: none;
  }

  .user-name {
    display: none;
  }

  .header-left {
    display: none;
  }
}
</style>


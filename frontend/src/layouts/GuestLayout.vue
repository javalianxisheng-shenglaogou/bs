<template>
  <div class="guest-layout">
    <!-- 顶部导航栏 -->
    <header class="guest-header">
      <div class="container">
        <div class="header-left">
          <!-- Logo -->
          <div class="logo" @click="handleGoHome">
            <el-icon :size="28" color="#409EFF"><OfficeBuilding /></el-icon>
            <span class="logo-text">{{ currentSiteName }}</span>
          </div>
        </div>

        <div class="header-center">
          <!-- 站点选择器 -->
          <el-select
            v-model="currentSiteId"
            @change="handleSiteChange"
            class="site-selector"
            placeholder="选择站点"
          >
            <el-option
              v-for="site in sites"
              :key="site.id"
              :label="site.name"
              :value="site.id"
            />
          </el-select>

          <!-- 搜索框 -->
          <el-input
            v-model="searchKeyword"
            placeholder="搜索内容..."
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>

        <div class="header-right">
          <!-- 用户信息（已登录） -->
          <el-dropdown v-if="userStore.isLoggedIn" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.user?.avatarUrl">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.user?.nickname || userStore.user?.username || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  编辑个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 登录/注册按钮（未登录） -->
          <div v-else class="auth-buttons">
            <el-button @click="handleLogin">登录</el-button>
            <el-button type="primary" @click="handleRegister">注册</el-button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="guest-main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="guest-footer">
      <div class="container">
        <p>&copy; 2025 多站点CMS系统. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { OfficeBuilding, Search, User, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getActiveSites, type SiteDTO } from '@/api/guest'

const router = useRouter()
const userStore = useUserStore()

// 状态
const sites = ref<SiteDTO[]>([])
const currentSiteId = ref<number>(1)
const searchKeyword = ref('')

// 计算属性
const currentSiteName = computed(() => {
  const site = sites.value.find(s => s.id === currentSiteId.value)
  return site?.name || '多站点CMS'
})

// 加载站点列表
const loadSites = async () => {
  try {
    const res = await getActiveSites()
    sites.value = res.data || []
    
    // 设置默认站点
    if (sites.value.length > 0 && !currentSiteId.value) {
      const defaultSite = sites.value.find(s => s.id === 1)
      currentSiteId.value = defaultSite?.id || sites.value[0].id
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载站点列表失败')
  }
}

// 站点切换
const handleSiteChange = (siteId: number) => {
  currentSiteId.value = siteId
  // 如果在首页，则刷新数据
  if (router.currentRoute.value.name === 'GuestHome') {
    router.go(0) // 刷新当前页面
  } else {
    // 否则跳转到首页
    router.push('/guest')
  }
}

// 搜索
const handleSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  router.push({
    path: '/guest/search',
    query: {
      keyword: searchKeyword.value.trim(),
      siteId: currentSiteId.value
    }
  })
}

// 回到首页
const handleGoHome = () => {
  router.push('/guest')
}

// 下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/guest/profile')
  }
}

// 登录
const handleLogin = () => {
  router.push('/login')
}

// 注册
const handleRegister = () => {
  router.push('/register')
}

// 初始化
onMounted(() => {
  loadSites()
  
  // 从查询参数获取站点ID
  const siteIdFromQuery = router.currentRoute.value.query.siteId
  if (siteIdFromQuery) {
    currentSiteId.value = Number(siteIdFromQuery)
  }
})

// 暴露给子组件使用
defineExpose({
  currentSiteId
})
</script>

<style scoped lang="scss">
.guest-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.guest-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;

  .container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
    gap: 20px;
  }

  .header-left {
    .logo {
      display: flex;
      align-items: center;
      gap: 12px;
      cursor: pointer;
      transition: opacity 0.2s;

      &:hover {
        opacity: 0.8;
      }

      .logo-text {
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }
    }
  }

  .header-center {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    max-width: 800px;

    .site-selector {
      width: 160px;
    }

    .search-input {
      flex: 1;
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 12px;
      border-radius: 4px;
      transition: background-color 0.2s;

      &:hover {
        background-color: #f5f7fa;
      }

      .user-name {
        font-size: 14px;
        color: #606266;
      }
    }

    .auth-buttons {
      display: flex;
      gap: 12px;
    }
  }
}

.guest-main {
  flex: 1;
  padding: 24px 0;

  :deep(.container) {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
  }
}

.guest-footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 20px 0;
  text-align: center;

  .container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
  }

  p {
    margin: 0;
    color: #909399;
    font-size: 14px;
  }
}
</style>


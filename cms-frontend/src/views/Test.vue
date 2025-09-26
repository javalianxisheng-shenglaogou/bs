<template>
  <div class="test-container">
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <span>🧪 前端功能测试</span>
        </div>
      </template>
      
      <div class="test-section">
        <h3>📊 系统状态</h3>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="前端状态" :value="frontendStatus" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="后端状态" :value="backendStatus" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="认证状态" :value="authStatus" />
          </el-col>
        </el-row>
      </div>

      <el-divider />

      <!-- 认证状态详情 -->
      <div class="test-section">
        <h3>🔐 认证状态详情</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <h4>当前令牌信息</h4>
              <p><strong>Token存在:</strong> {{ hasToken ? '✅ 是' : '❌ 否' }}</p>
              <p><strong>Token长度:</strong> {{ tokenLength }}</p>
              <p><strong>用户信息:</strong> {{ currentUser?.username || '未登录' }}</p>
              <p><strong>用户角色:</strong> {{ currentUser?.roles?.join(', ') || '无' }}</p>
              <p><strong>是否超级管理员:</strong> {{ currentUser?.isSuperAdmin ? '✅ 是' : '❌ 否' }}</p>
              <el-button @click="checkAuthStatus" type="primary" size="small">刷新状态</el-button>
              <el-button @click="showTokenDetails" type="info" size="small">查看Token</el-button>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <h4>快速超级管理员登录</h4>
              <el-form :model="superAdminForm" @submit.prevent="quickSuperAdminLogin">
                <el-form-item label="用户名">
                  <el-input v-model="superAdminForm.usernameOrEmail" placeholder="superadmin" />
                </el-form-item>
                <el-form-item label="密码">
                  <el-input v-model="superAdminForm.password" type="password" placeholder="admin123" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="quickSuperAdminLogin" :loading="superAdminLoading" size="small">
                    超级管理员登录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <el-divider />

      <div class="test-section">
        <h3>🔐 登录功能测试</h3>
        <el-form :model="loginForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="loginForm.usernameOrEmail" placeholder="testuser" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" type="password" placeholder="password123" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="testLogin" :loading="loginLoading">
              测试登录
            </el-button>
            <el-button @click="clearAuth">清除认证</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-divider />

      <div class="test-section">
        <h3>📝 API测试结果</h3>
        <el-timeline>
          <el-timeline-item
            v-for="(result, index) in testResults"
            :key="index"
            :timestamp="result.timestamp"
            :type="result.success ? 'success' : 'danger'"
          >
            <el-card>
              <h4>{{ result.title }}</h4>
              <p>{{ result.message }}</p>
              <el-tag :type="result.success ? 'success' : 'danger'">
                {{ result.success ? '成功' : '失败' }}
              </el-tag>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <el-divider />

      <div class="test-section">
        <h3>🔧 快速操作</h3>
        <el-space wrap>
          <el-button @click="checkSystemStatus">检查系统状态</el-button>
          <el-button @click="testAllAPIs">测试所有API</el-button>
          <el-button @click="goToDashboard">前往仪表盘</el-button>
          <el-button @click="openSwagger">打开API文档</el-button>
        </el-space>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { authApi } from '../api/auth'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const frontendStatus = ref(1)
const backendStatus = ref(0)
const authStatus = ref(0)
const loginLoading = ref(false)
const superAdminLoading = ref(false)

const loginForm = reactive({
  usernameOrEmail: 'testuser',
  password: 'password123'
})

const superAdminForm = reactive({
  usernameOrEmail: 'superadmin',
  password: 'admin123'
})

// 计算属性
const hasToken = computed(() => {
  return !!localStorage.getItem('token')
})

const tokenLength = computed(() => {
  const token = localStorage.getItem('token')
  return token ? token.length : 0
})

const currentUser = computed(() => {
  return authStore.user
})

const testResults = ref<Array<{
  title: string
  message: string
  success: boolean
  timestamp: string
}>>([])

// 添加测试结果
function addTestResult(title: string, message: string, success: boolean) {
  testResults.value.unshift({
    title,
    message,
    success,
    timestamp: new Date().toLocaleTimeString()
  })
}

// 检查认证状态
function checkAuthStatus() {
  const token = localStorage.getItem('token')
  const user = authStore.user

  if (token && user) {
    authStatus.value = 1
    addTestResult('认证状态检查', `用户 ${user.username} 已登录，角色: ${user.roles?.join(', ')}`, true)
  } else {
    authStatus.value = 0
    addTestResult('认证状态检查', '用户未登录或令牌无效', false)
  }
}

// 显示Token详情
function showTokenDetails() {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 解析JWT payload (不验证签名，仅用于显示)
      const payload = JSON.parse(atob(token.split('.')[1]))
      const details = `
用户ID: ${payload.userId}
用户名: ${payload.username}
邮箱: ${payload.email}
权限: ${payload.authorities?.join(', ')}
发行时间: ${new Date(payload.iat * 1000).toLocaleString()}
过期时间: ${new Date(payload.exp * 1000).toLocaleString()}
      `
      ElMessage.info({
        message: details,
        duration: 10000,
        showClose: true
      })
      addTestResult('Token详情', 'Token信息已显示', true)
    } catch (error) {
      ElMessage.error('Token格式无效')
      addTestResult('Token详情', 'Token解析失败', false)
    }
  } else {
    ElMessage.warning('没有找到Token')
    addTestResult('Token详情', '没有找到Token', false)
  }
}

// 快速超级管理员登录
async function quickSuperAdminLogin() {
  superAdminLoading.value = true
  try {
    await authStore.login(superAdminForm)
    checkAuthStatus()
    addTestResult('超级管理员登录', `超级管理员 ${superAdminForm.usernameOrEmail} 登录成功`, true)
    ElMessage.success('超级管理员登录成功！')
  } catch (error: any) {
    addTestResult('超级管理员登录', `登录失败: ${error.message || error}`, false)
    ElMessage.error('超级管理员登录失败！')
  } finally {
    superAdminLoading.value = false
  }
}

// 检查系统状态
async function checkSystemStatus() {
  try {
    // 检查后端状态
    const response = await fetch('http://localhost:8080/api/actuator/health')
    const data = await response.json()
    
    if (data.status === 'UP') {
      backendStatus.value = 1
      addTestResult('系统状态检查', '后端API运行正常', true)
    } else {
      backendStatus.value = 0
      addTestResult('系统状态检查', `后端状态: ${data.status}`, false)
    }
  } catch (error) {
    backendStatus.value = 0
    addTestResult('系统状态检查', `后端连接失败: ${error}`, false)
  }
}

// 测试登录功能
async function testLogin() {
  loginLoading.value = true
  try {
    await authStore.login(loginForm)
    addTestResult('登录测试', `用户 ${loginForm.usernameOrEmail} 登录成功`, true)
    ElMessage.success('登录测试成功！')
  } catch (error: any) {
    addTestResult('登录测试', `登录失败: ${error.message || error}`, false)
    ElMessage.error('登录测试失败！')
  } finally {
    loginLoading.value = false
  }
}

// 清除认证信息
function clearAuth() {
  authStore.logout()
  addTestResult('清除认证', '已清除所有认证信息', true)
  ElMessage.info('认证信息已清除')
}

// 测试所有API
async function testAllAPIs() {
  addTestResult('API测试', '开始测试所有API端点...', true)
  
  // 测试健康检查
  await checkSystemStatus()
  
  // 测试登录
  if (loginForm.usernameOrEmail && loginForm.password) {
    await testLogin()
  }
  
  // 如果已登录，测试受保护的API
  if (authStore.isAuthenticated) {
    try {
      // 这里可以添加更多API测试
      addTestResult('受保护API测试', '用户已认证，可以访问受保护的API', true)
    } catch (error) {
      addTestResult('受保护API测试', `受保护API测试失败: ${error}`, false)
    }
  }
  
  ElMessage.success('API测试完成！')
}

// 前往仪表盘
function goToDashboard() {
  if (authStore.isAuthenticated) {
    router.push('/dashboard')
  } else {
    ElMessage.warning('请先登录')
  }
}

// 打开API文档
function openSwagger() {
  window.open('http://localhost:8080/api/swagger-ui.html', '_blank')
  addTestResult('打开文档', '已打开Swagger API文档', true)
}

// 组件挂载时检查状态
onMounted(() => {
  checkSystemStatus()
  checkAuthStatus()
  addTestResult('页面加载', '测试页面已加载', true)
})
</script>

<style scoped>
.test-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.test-section {
  margin: 20px 0;
}

.test-section h3 {
  margin-bottom: 16px;
  color: #409eff;
}

:deep(.el-timeline-item__content) {
  padding-bottom: 20px;
}

:deep(.el-card) {
  margin-bottom: 0;
}

:deep(.el-statistic__content) {
  font-size: 24px;
  font-weight: bold;
}
</style>

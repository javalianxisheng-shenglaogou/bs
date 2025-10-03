<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <el-card class="login-card" shadow="always">
        <!-- Logo和标题 -->
        <div class="card-header">
          <div class="logo-wrapper">
            <el-icon :size="60" color="#667eea">
              <OfficeBuilding />
            </el-icon>
          </div>
          <h1 class="system-title">多站点CMS系统</h1>
          <p class="system-subtitle">Multi-Site Content Management System</p>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-width="0"
          size="large"
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-button"
              @click="handleLogin"
            >
              <span v-if="!loading">立即登录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 测试账号提示 -->
        <div class="tips">
          <el-collapse accordion>
            <el-collapse-item title="📝 测试账号" name="1">
              <div class="test-accounts">
                <div class="account-item">
                  <el-tag type="danger" effect="dark">超级管理员</el-tag>
                  <span class="account-info">admin / password</span>
                </div>
                <div class="account-item">
                  <el-tag type="warning" effect="dark">站点管理员</el-tag>
                  <span class="account-info">siteadmin / password</span>
                </div>
                <div class="account-item">
                  <el-tag type="success" effect="dark">编辑者</el-tag>
                  <span class="account-info">editor1 / password</span>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-card>

      <!-- 页脚 -->
      <div class="footer">
        <p>© 2025 Multi-Site CMS. All rights reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { User, Lock, OfficeBuilding } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true

      try {
        // 调用真实的登录API
        const success = await userStore.login({
          username: loginForm.username,
          password: loginForm.password
        })

        if (success) {
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error('登录失败，请检查用户名和密码')
        }
      } catch (error: any) {
        console.error('登录错误:', error)
        ElMessage.error(error.message || '登录失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 登录包装器 */
.login-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  padding: 20px;
}

.login-card {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.login-card :deep(.el-card__body) {
  padding: 40px;
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  margin-bottom: 20px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.system-title {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.system-subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
  letter-spacing: 1px;
}

/* 登录表单 */
.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.login-button:active {
  transform: translateY(0);
}

/* 测试账号提示 */
.tips {
  margin-top: 30px;
}

.tips :deep(.el-collapse) {
  border: none;
}

.tips :deep(.el-collapse-item__header) {
  background: transparent;
  border: none;
  color: #667eea;
  font-weight: 500;
  padding: 0;
}

.tips :deep(.el-collapse-item__wrap) {
  background: transparent;
  border: none;
}

.tips :deep(.el-collapse-item__content) {
  padding: 15px 0 0 0;
}

.test-accounts {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.account-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.account-item:hover {
  background: #e8eaf0;
  transform: translateX(5px);
}

.account-info {
  font-family: 'Courier New', monospace;
  color: #606266;
  font-size: 14px;
}

/* 页脚 */
.footer {
  text-align: center;
  margin-top: 30px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.footer p {
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-wrapper {
    max-width: 100%;
  }

  .login-card :deep(.el-card__body) {
    padding: 30px 20px;
  }

  .system-title {
    font-size: 24px;
  }
}
</style>


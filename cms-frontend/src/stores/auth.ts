import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/auth'
import type { User, LoginRequest, RegisterRequest } from '../api'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const user = ref<User | null>(null)
  const loading = ref(false)
  const initialized = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => {
    if (user.value?.isSuperAdmin) return true

    // 检查角色数组
    if (Array.isArray(user.value?.roles)) {
      return user.value.roles.some(role => {
        if (typeof role === 'string') {
          return role === 'SUPER_ADMIN' || role === 'SITE_ADMIN'
        }
        return role.code === 'SUPER_ADMIN' || role.code === 'SITE_ADMIN'
      })
    }

    return false
  })

  // 方法
  const login = async (loginData: LoginRequest) => {
    loading.value = true
    try {
      const response = await authApi.login(loginData)
      const { accessToken: newToken, refreshToken: newRefreshToken, user: userData } = response.data
      
      // 保存令牌
      token.value = newToken
      refreshToken.value = newRefreshToken
      user.value = userData
      
      // 持久化存储
      localStorage.setItem('token', newToken)
      localStorage.setItem('refreshToken', newRefreshToken)
      localStorage.setItem('user', JSON.stringify(userData))
      
      return response
    } finally {
      loading.value = false
    }
  }

  const register = async (registerData: RegisterRequest) => {
    loading.value = true
    try {
      const response = await authApi.register(registerData)
      return response
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    try {
      if (token.value) {
        await authApi.logout()
      }
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      // 清除状态
      token.value = null
      refreshToken.value = null
      user.value = null
      
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
    }
  }

  const getCurrentUser = async () => {
    if (!token.value) return null

    try {
      const response = await authApi.getCurrentUser()
      user.value = response.data
      localStorage.setItem('user', JSON.stringify(response.data))
      return response.data
    } catch (error) {
      console.error('Get current user error:', error)
      // 不在这里自动调用logout，让调用方决定如何处理错误
      throw error
    }
  }

  const refreshTokens = async () => {
    if (!refreshToken.value) {
      await logout()
      return false
    }

    try {
      const response = await authApi.refreshToken(refreshToken.value)
      const { token: newToken, refreshToken: newRefreshToken } = response.data
      
      token.value = newToken
      refreshToken.value = newRefreshToken
      
      localStorage.setItem('token', newToken)
      localStorage.setItem('refreshToken', newRefreshToken)
      
      return true
    } catch (error) {
      console.error('Refresh token error:', error)
      await logout()
      return false
    }
  }

  const changePassword = async (oldPassword: string, newPassword: string) => {
    return await authApi.changePassword({ oldPassword, newPassword })
  }

  // 初始化用户信息
  const initializeAuth = async () => {
    if (initialized.value) return

    const storedUser = localStorage.getItem('user')
    if (storedUser && token.value) {
      try {
        user.value = JSON.parse(storedUser)
        // 只有在有令牌的情况下才验证令牌是否有效
        // 如果验证失败，会自动清除认证状态
        await getCurrentUser()
      } catch (error) {
        console.warn('Token validation failed during initialization, clearing auth state:', error)
        // 清除无效的认证状态，但不显示错误消息
        token.value = null
        refreshToken.value = null
        user.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('refreshToken')
        localStorage.removeItem('user')
      }
    }

    initialized.value = true
  }

  return {
    // 状态
    token,
    refreshToken,
    user,
    loading,
    initialized,

    // 计算属性
    isAuthenticated,
    isAdmin,

    // 方法
    login,
    register,
    logout,
    getCurrentUser,
    refreshTokens,
    changePassword,
    initializeAuth
  }
})

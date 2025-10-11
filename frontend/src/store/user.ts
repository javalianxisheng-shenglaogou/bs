import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getCurrentUser as getCurrentUserApi, logout as logoutApi, type LoginRequest, type UserInfo as ApiUserInfo } from '@/api/auth'

export interface UserInfo {
  userId: number
  username: string
  email: string
  nickname: string
  avatarUrl?: string
  status: string
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
  }

  /**
   * 用户登录
   */
  const login = async (loginData: LoginRequest) => {
    try {
      const response = await loginApi(loginData)
      if (response.code === 200 && response.data) {
        const { token: newToken, userId, username, nickname, email, roles, permissions } = response.data
        setToken(newToken)
        setUserInfo({
          userId,
          username,
          nickname,
          email,
          status: 'ACTIVE',
          roles,
          permissions
        })
        // 登录后尝试获取完整用户信息(包括头像)，失败也不影响登录
        try {
          await fetchUserInfo()
        } catch (error) {
          console.warn('获取完整用户信息失败，使用登录返回的基本信息:', error)
        }
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      throw error // 抛出错误以便上层处理
    }
  }

  /**
   * 获取当前用户信息
   */
  const fetchUserInfo = async () => {
    try {
      const response = await getCurrentUserApi()
      if (response.code === 200 && response.data) {
        setUserInfo(response.data)
        return true
      }
      return false
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return false
    }
  }

  /**
   * 退出登录
   */
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
    }
  }

  /**
   * 检查是否有指定权限
   */
  const hasPermission = (permission: string): boolean => {
    const permissions = userInfo.value?.permissions || []
    return permissions.includes(permission)
  }

  /**
   * 检查是否有任意一个权限
   */
  const hasAnyPermission = (permissions: string[]): boolean => {
    if (!permissions || permissions.length === 0) {
      return true
    }
    return permissions.some(permission => hasPermission(permission))
  }

  /**
   * 检查是否有所有权限
   */
  const hasAllPermissions = (permissions: string[]): boolean => {
    if (!permissions || permissions.length === 0) {
      return true
    }
    return permissions.every(permission => hasPermission(permission))
  }

  /**
   * 检查是否有指定角色
   */
  const hasRole = (role: string): boolean => {
    const roles = userInfo.value?.roles || []
    return roles.includes(role)
  }

  /**
   * 检查是否有任意一个角色
   */
  const hasAnyRole = (roles: string[]): boolean => {
    if (!roles || roles.length === 0) {
      return true
    }
    return roles.some(role => hasRole(role))
  }

  /**
   * 检查是否有所有角色
   */
  const hasAllRoles = (roles: string[]): boolean => {
    if (!roles || roles.length === 0) {
      return true
    }
    return roles.every(role => hasRole(role))
  }

  /**
   * 检查是否是管理员
   */
  const isAdmin = (): boolean => {
    return hasRole('ADMIN') || hasRole('SUPER_ADMIN')
  }

  // 计算属性：是否已登录
  const isLoggedIn = computed(() => !!token.value)

  return {
    token,
    userInfo,
    user: userInfo, // 兼容别名
    isLoggedIn, // 已登录状态（计算属性）
    setToken,
    setUserInfo,
    login,
    fetchUserInfo,
    logout,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasRole,
    hasAnyRole,
    hasAllRoles,
    isAdmin
  }
})


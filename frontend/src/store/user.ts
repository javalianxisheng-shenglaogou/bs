import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getCurrentUser as getCurrentUserApi, logout as logoutApi, type LoginRequest, type UserInfo as ApiUserInfo } from '@/api/auth'

export interface UserInfo {
  userId: number
  username: string
  email: string
  nickname: string
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
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
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

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    login,
    fetchUserInfo,
    logout
  }
})


import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

// API基础配置
const API_BASE_URL = '/api'

// 创建axios实例
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    // 从localStorage获取token，避免在初始化时使用store
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    
    // 如果是成功响应，直接返回数据
    if (data.success) {
      return data
    }
    
    // 如果是失败响应，显示错误信息
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  (error) => {
    const { response } = error
    
    if (response) {
      switch (response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          // 清除本地存储的token
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          localStorage.removeItem('user')
          window.location.href = '/login'
          break
        case 403:
          // 403错误可能是令牌无效，清除认证信息并重新登录
          console.warn('403 Forbidden - Token may be invalid, clearing auth data')
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          localStorage.removeItem('user')

          // 如果不是登录页面，跳转到登录页
          if (window.location.pathname !== '/login') {
            ElMessage.error('登录已过期，请重新登录')
            setTimeout(() => {
              window.location.href = '/login'
            }, 1500)
          } else {
            ElMessage.error('权限不足')
          }
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

// API响应类型定义
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  timestamp: string
}

export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// 用户相关接口
export interface User {
  id: number
  username: string
  email: string
  nickname: string
  displayName?: string
  avatarUrl?: string
  status?: string
  createdAt?: string
  updatedAt?: string
  roles?: Role[] | string[]  // 支持角色对象数组或字符串数组
  managedSites?: string[]
  isSuperAdmin?: boolean
}

export interface Role {
  id: number
  name: string
  code: string
  description: string
}

export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  confirmPassword: string
  nickname: string
}

// 站点相关接口
export interface Site {
  id: number
  name: string
  code: string
  domain: string
  description: string
  status: string
  parentSite?: Site
  childSites?: Site[]
  createdAt: string
  updatedAt: string
}

// 内容相关接口
export interface Content {
  id: number
  title: string
  slug: string
  content: string
  excerpt: string
  status: string
  type: string
  viewCount: number
  publishedAt?: string
  createdAt: string
  updatedAt: string
  author: User
  site: Site
  category: ContentCategory
}

export interface ContentCategory {
  id: number
  name: string
  code: string
  description: string
  sortOrder: number
  parentCategory?: ContentCategory
  childCategories?: ContentCategory[]
  site: Site
}

export default api

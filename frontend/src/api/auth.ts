import request from '../utils/request'

/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 登录响应数据
 */
export interface LoginResponse {
  token: string
  tokenType: string
  userId: number
  username: string
  nickname: string
  email: string
  roles: string[]
  permissions: string[]
}

/**
 * 用户信息
 */
export interface UserInfo {
  userId: number
  username: string
  nickname: string
  email: string
  mobile?: string
  realName?: string
  gender?: string
  birthday?: string
  bio?: string
  avatarUrl?: string
  status: string
  roles: string[]
  permissions: string[]
  createdAt?: string
  lastLoginAt?: string
}

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return request<LoginResponse>({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request<UserInfo>({
    url: '/auth/me',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}


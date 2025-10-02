import request from '@/utils/request'

/**
 * 用户信息
 */
export interface User {
  id: number
  username: string
  email: string
  mobile?: string
  nickname?: string
  realName?: string
  avatarUrl?: string
  gender?: string
  birthday?: string
  bio?: string
  status: string
  emailVerified: boolean
  mobileVerified: boolean
  lastLoginAt?: string
  lastLoginIp?: string
  loginCount: number
  roles: string[]
  createdAt: string
  updatedAt: string
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

/**
 * 创建用户请求
 */
export interface UserCreateRequest {
  username: string
  email: string
  mobile?: string
  password: string
  nickname?: string
  realName?: string
  gender?: string
  birthday?: string
  bio?: string
  status?: string
  roleIds?: number[]
}

/**
 * 更新用户请求
 */
export interface UserUpdateRequest {
  email?: string
  mobile?: string
  nickname?: string
  realName?: string
  gender?: string
  birthday?: string
  bio?: string
  status?: string
  roleIds?: number[]
}

/**
 * 获取用户列表
 */
export function getUserList(params: {
  keyword?: string
  status?: string
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: string
}) {
  return request<PageResponse<User>>({
    url: '/users',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 */
export function getUserById(id: number) {
  return request<User>({
    url: `/users/${id}`,
    method: 'get'
  })
}

/**
 * 创建用户
 */
export function createUser(data: UserCreateRequest) {
  return request<User>({
    url: '/users',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(id: number, data: UserUpdateRequest) {
  return request<User>({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id: number) {
  return request<void>({
    url: `/users/${id}`,
    method: 'delete'
  })
}


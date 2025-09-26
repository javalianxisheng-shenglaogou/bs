import api from './index'
import type { ApiResponse, PageResult, User } from './index'

export interface UserCreateRequest {
  username: string
  email: string
  password: string
  nickname: string
  roleIds: number[]
}

export interface UserUpdateRequest {
  username?: string
  email?: string
  nickname?: string
  avatarUrl?: string
  roleIds?: number[]
}

export interface ProfileUpdateRequest {
  email: string
  nickname: string
  phone?: string
  avatarUrl?: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

// 用户相关API
export const usersApi = {
  // 分页查询用户
  getUsers(params: {
    page?: number
    size?: number
    sort?: string
    direction?: string
    keyword?: string
    status?: string
    roleId?: number
  }): Promise<ApiResponse<PageResult<User>>> {
    return api.get('/users', { params })
  },

  // 获取用户详情
  getUserById(id: number): Promise<ApiResponse<User>> {
    return api.get(`/users/${id}`)
  },

  // 创建用户
  createUser(data: UserCreateRequest): Promise<ApiResponse<User>> {
    return api.post('/users', data)
  },

  // 更新用户
  updateUser(id: number, data: UserUpdateRequest): Promise<ApiResponse<User>> {
    return api.put(`/users/${id}`, data)
  },

  // 删除用户
  deleteUser(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/users/${id}`)
  },

  // 更新用户状态
  updateUserStatus(id: number, status: string): Promise<ApiResponse<User>> {
    return api.patch(`/users/${id}/status`, { status })
  },

  // 重置用户密码
  resetUserPassword(id: number, newPassword: string): Promise<ApiResponse<void>> {
    return api.patch(`/users/${id}/password`, { newPassword })
  },

  // 获取用户角色
  getUserRoles(id: number): Promise<ApiResponse<any[]>> {
    return api.get(`/users/${id}/roles`)
  },

  // 分配用户角色
  assignUserRoles(id: number, roleIds: number[]): Promise<ApiResponse<void>> {
    return api.post(`/users/${id}/roles`, { roleIds })
  }
}

// 个人资料相关API
export const updateProfile = (data: ProfileUpdateRequest): Promise<ApiResponse<User>> => {
  return api.put('/users/profile', data)
}

export const changePassword = (data: ChangePasswordRequest): Promise<ApiResponse<void>> => {
  return api.put('/users/password', data)
}

export const uploadFile = (formData: FormData): Promise<ApiResponse<{ url: string }>> => {
  return api.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

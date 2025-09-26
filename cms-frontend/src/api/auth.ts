import api from './index'
import type { ApiResponse, LoginRequest, RegisterRequest, User } from './index'

// 认证相关API
export const authApi = {
  // 用户登录
  login(data: LoginRequest): Promise<ApiResponse<{ accessToken: string; refreshToken: string; user: User }>> {
    return api.post('/auth/login', data)
  },

  // 用户注册
  register(data: RegisterRequest): Promise<ApiResponse<User>> {
    return api.post('/auth/register', data)
  },

  // 刷新令牌
  refreshToken(refreshToken: string): Promise<ApiResponse<{ token: string; refreshToken: string }>> {
    return api.post('/auth/refresh', null, {
      params: { refreshToken }
    })
  },

  // 验证令牌
  validateToken(token: string): Promise<ApiResponse<{ valid: boolean; user: User }>> {
    return api.get('/auth/validate', {
      params: { token }
    })
  },

  // 用户登出
  logout(): Promise<ApiResponse<void>> {
    return api.post('/auth/logout')
  },

  // 获取当前用户信息
  getCurrentUser(): Promise<ApiResponse<User>> {
    return api.get('/auth/me')
  },

  // 修改密码
  changePassword(data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> {
    return api.post('/auth/change-password', data)
  },

  // 重置密码
  resetPassword(data: { email: string }): Promise<ApiResponse<void>> {
    return api.post('/auth/reset-password', data)
  }
}

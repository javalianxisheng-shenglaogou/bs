import request from '../utils/request'

/**
 * 权限DTO
 */
export interface PermissionDTO {
  id?: number
  name: string
  code: string
  description?: string
  module: string
  resource: string
  action: string
  isSystem?: boolean
  sortOrder?: number
  createdAt?: string
  updatedAt?: string
}

/**
 * 获取所有权限
 */
export function getAllPermissions() {
  return request<PermissionDTO[]>({
    url: '/permissions/all',
    method: 'get'
  })
}

/**
 * 分页获取权限列表
 */
export function getPermissions(params: {
  module?: string
  page?: number
  size?: number
}) {
  return request({
    url: '/permissions',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取权限
 */
export function getPermissionById(id: number) {
  return request<PermissionDTO>({
    url: `/permissions/${id}`,
    method: 'get'
  })
}

/**
 * 根据模块获取权限
 */
export function getPermissionsByModule(module: string) {
  return request<PermissionDTO[]>({
    url: `/permissions/module/${module}`,
    method: 'get'
  })
}

/**
 * 创建权限
 */
export function createPermission(data: PermissionDTO) {
  return request<PermissionDTO>({
    url: '/permissions',
    method: 'post',
    data
  })
}

/**
 * 更新权限
 */
export function updatePermission(id: number, data: PermissionDTO) {
  return request<PermissionDTO>({
    url: `/permissions/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除权限
 */
export function deletePermission(id: number) {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  })
}


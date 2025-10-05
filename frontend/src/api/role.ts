import request from '../utils/request'
import type { PermissionDTO } from './permission'

/**
 * 角色DTO
 */
export interface RoleDTO {
  id?: number
  name: string
  code: string
  description?: string
  level?: number
  isSystem?: boolean
  isDefault?: boolean
  permissionIds?: number[]
  permissions?: PermissionDTO[]
  createdAt?: string
  updatedAt?: string
}

/**
 * 获取所有角色
 */
export function getAllRoles() {
  return request<RoleDTO[]>({
    url: '/roles/all',
    method: 'get'
  })
}

/**
 * 分页获取角色列表
 */
export function getRoles(params: {
  page?: number
  size?: number
}) {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取角色
 */
export function getRoleById(id: number) {
  return request<RoleDTO>({
    url: `/roles/${id}`,
    method: 'get'
  })
}

/**
 * 创建角色
 */
export function createRole(data: RoleDTO) {
  return request<RoleDTO>({
    url: '/roles',
    method: 'post',
    data
  })
}

/**
 * 更新角色
 */
export function updateRole(id: number, data: RoleDTO) {
  return request<RoleDTO>({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export function deleteRole(id: number) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

/**
 * 为角色分配权限
 */
export function assignPermissions(roleId: number, permissionIds: number[]) {
  return request<RoleDTO>({
    url: `/roles/${roleId}/permissions`,
    method: 'put',
    data: permissionIds
  })
}


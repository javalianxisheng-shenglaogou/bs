import { useUserStore } from '@/store/user'

/**
 * 权限检查工具函数
 */

/**
 * 检查是否有指定权限
 * @param permission 权限代码
 * @returns 是否有权限
 */
export function hasPermission(permission: string): boolean {
  const userStore = useUserStore()
  const permissions = userStore.userInfo?.permissions || []
  return permissions.includes(permission)
}

/**
 * 检查是否有任意一个权限
 * @param permissions 权限代码数组
 * @returns 是否有任意一个权限
 */
export function hasAnyPermission(permissions: string[]): boolean {
  if (!permissions || permissions.length === 0) {
    return true
  }
  return permissions.some(permission => hasPermission(permission))
}

/**
 * 检查是否有所有权限
 * @param permissions 权限代码数组
 * @returns 是否有所有权限
 */
export function hasAllPermissions(permissions: string[]): boolean {
  if (!permissions || permissions.length === 0) {
    return true
  }
  return permissions.every(permission => hasPermission(permission))
}

/**
 * 检查是否有指定角色
 * @param role 角色代码
 * @returns 是否有角色
 */
export function hasRole(role: string): boolean {
  const userStore = useUserStore()
  const roles = userStore.userInfo?.roles || []
  return roles.includes(role)
}

/**
 * 检查是否有任意一个角色
 * @param roles 角色代码数组
 * @returns 是否有任意一个角色
 */
export function hasAnyRole(roles: string[]): boolean {
  if (!roles || roles.length === 0) {
    return true
  }
  return roles.some(role => hasRole(role))
}

/**
 * 检查是否有所有角色
 * @param roles 角色代码数组
 * @returns 是否有所有角色
 */
export function hasAllRoles(roles: string[]): boolean {
  if (!roles || roles.length === 0) {
    return true
  }
  return roles.every(role => hasRole(role))
}

/**
 * 检查是否是管理员
 * @returns 是否是管理员
 */
export function isAdmin(): boolean {
  return hasRole('ADMIN') || hasRole('SUPER_ADMIN')
}

/**
 * 检查是否已登录
 * @returns 是否已登录
 */
export function isAuthenticated(): boolean {
  const userStore = useUserStore()
  return !!userStore.token && !!userStore.userInfo
}


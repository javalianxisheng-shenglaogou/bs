import type { Directive, DirectiveBinding } from 'vue'
import { hasPermission, hasAnyPermission, hasAllPermissions, hasRole, hasAnyRole, hasAllRoles } from '@/utils/permission'

/**
 * 权限指令
 * 用法:
 * v-permission="'user:create'" - 单个权限
 * v-permission="['user:create', 'user:update']" - 任意一个权限
 * v-permission:all="['user:create', 'user:update']" - 所有权限
 * v-permission:role="'ADMIN'" - 单个角色
 * v-permission:role="['ADMIN', 'EDITOR']" - 任意一个角色
 * v-permission:role:all="['ADMIN', 'EDITOR']" - 所有角色
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding)
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding)
  }
}

function checkPermission(el: HTMLElement, binding: DirectiveBinding) {
  const { value, arg, modifiers } = binding
  
  if (!value) {
    return
  }

  let hasAuth = false

  // 角色检查
  if (arg === 'role') {
    if (Array.isArray(value)) {
      hasAuth = modifiers.all ? hasAllRoles(value) : hasAnyRole(value)
    } else {
      hasAuth = hasRole(value)
    }
  }
  // 权限检查
  else {
    if (Array.isArray(value)) {
      hasAuth = modifiers.all ? hasAllPermissions(value) : hasAnyPermission(value)
    } else {
      hasAuth = hasPermission(value)
    }
  }

  // 如果没有权限，移除元素
  if (!hasAuth) {
    el.parentNode?.removeChild(el)
  }
}

/**
 * 注册权限指令
 */
export function setupPermissionDirective(app: any) {
  app.directive('permission', permission)
  // 别名
  app.directive('auth', permission)
}


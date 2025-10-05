import { request } from '@/utils/request'
import type { PageData } from '@/types/api'

/**
 * 工作流API
 */

// ==================== 工作流定义 ====================

/**
 * 创建工作流
 */
export function createWorkflowApi(data: any) {
  return request({
    url: '/workflows',
    method: 'post',
    data
  })
}

/**
 * 更新工作流
 */
export function updateWorkflowApi(id: number, data: any) {
  return request({
    url: `/workflows/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除工作流
 */
export function deleteWorkflowApi(id: number) {
  return request({
    url: `/workflows/${id}`,
    method: 'delete'
  })
}

/**
 * 获取工作流详情
 */
export function getWorkflowApi(id: number) {
  return request({
    url: `/workflows/${id}`,
    method: 'get'
  })
}

/**
 * 根据代码获取工作流
 */
export function getWorkflowByCodeApi(code: string) {
  return request({
    url: `/workflows/code/${code}`,
    method: 'get'
  })
}

/**
 * 获取所有工作流
 */
export function getAllWorkflowsApi() {
  return request({
    url: '/workflows/all',
    method: 'get'
  })
}

/**
 * 分页查询工作流
 */
export function getWorkflowsApi(params: any) {
  return request<PageData<any>>({
    url: '/workflows',
    method: 'get',
    params
  })
}

/**
 * 更新工作流状态
 */
export function updateWorkflowStatusApi(id: number, status: string) {
  return request({
    url: `/workflows/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// ==================== 工作流实例 ====================

/**
 * 启动工作流
 */
export function startWorkflowApi(data: any) {
  return request({
    url: '/workflow-instances',
    method: 'post',
    data
  })
}

/**
 * 获取实例详情
 */
export function getInstanceApi(id: number) {
  return request({
    url: `/workflow-instances/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询实例
 */
export function getInstancesApi(params: any) {
  return request({
    url: '/workflow-instances',
    method: 'get',
    params
  })
}

/**
 * 取消实例
 */
export function cancelInstanceApi(id: number) {
  return request({
    url: `/workflow-instances/${id}/cancel`,
    method: 'post'
  })
}

// ==================== 工作流任务 ====================

/**
 * 获取我的待办任务
 */
export function getMyPendingTasksApi(params: any) {
  return request({
    url: '/workflow-tasks/pending',
    method: 'get',
    params
  })
}

/**
 * 获取我的已办任务
 */
export function getMyCompletedTasksApi(params: any) {
  return request({
    url: '/workflow-tasks/completed',
    method: 'get',
    params
  })
}

/**
 * 获取任务详情
 */
export function getTaskApi(id: number) {
  return request({
    url: `/workflow-tasks/${id}`,
    method: 'get'
  })
}

/**
 * 通过任务
 */
export function approveTaskApi(id: number, data: any) {
  return request({
    url: `/workflow-tasks/${id}/approve`,
    method: 'post',
    data
  })
}

/**
 * 拒绝任务
 */
export function rejectTaskApi(id: number, data: any) {
  return request({
    url: `/workflow-tasks/${id}/reject`,
    method: 'post',
    data
  })
}


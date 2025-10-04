import request from '@/utils/request'

/**
 * 系统日志接口
 */
export interface SystemLog {
  id?: number
  userId?: number
  username?: string
  module: string
  action: string
  level: string
  description?: string
  requestMethod?: string
  requestUrl?: string
  requestParams?: string
  responseResult?: string
  ipAddress?: string
  browser?: string
  operatingSystem?: string
  executionTime?: number
  isSuccess?: boolean
  errorMessage?: string
  createdAt?: string
}

/**
 * 日志查询参数
 */
export interface LogQueryParams {
  userId?: number
  username?: string
  module?: string
  action?: string
  level?: string
  ipAddress?: string
  isSuccess?: boolean
  startTime?: string
  endTime?: string
  page?: number
  size?: number
  sortBy?: string
  sortDir?: string
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  total: number
  totalPages: number
}

/**
 * 获取日志列表(分页)
 */
export const getLogsApi = (params?: LogQueryParams) => {
  return request<PageResponse<SystemLog>>({
    url: '/logs',
    method: 'get',
    params
  })
}

/**
 * 获取日志详情
 */
export const getLogApi = (id: number) => {
  return request<SystemLog>({
    url: `/logs/${id}`,
    method: 'get'
  })
}

/**
 * 批量删除日志
 */
export const deleteLogsApi = (ids: number[]) => {
  return request({
    url: '/logs',
    method: 'delete',
    data: ids
  })
}

/**
 * 清空日志
 */
export const clearLogsApi = (params?: {
  level?: string
  startTime?: string
  endTime?: string
}) => {
  return request({
    url: '/logs/clear',
    method: 'delete',
    params
  })
}

/**
 * 获取日志统计
 */
export const getLogStatisticsApi = () => {
  return request<any[]>({
    url: '/logs/statistics',
    method: 'get'
  })
}


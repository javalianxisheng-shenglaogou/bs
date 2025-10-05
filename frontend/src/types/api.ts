/**
 * API响应基础类型
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: string
}

/**
 * 分页响应数据
 */
export interface PageData<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}

/**
 * 分页参数
 */
export interface PageParams {
  page?: number
  size?: number
  sortBy?: string
  sortDir?: string
}


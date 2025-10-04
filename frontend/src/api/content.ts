import request from '@/utils/request'

/**
 * 内容接口
 */
export interface Content {
  id?: number
  siteId: number
  categoryId?: number
  title: string
  slug: string
  summary?: string
  content?: string
  contentType?: string
  template?: string
  coverImage?: string
  authorId?: number
  authorName?: string
  status?: string
  publishedAt?: string
  viewCount?: number
  isTop?: boolean
  isFeatured?: boolean
  isOriginal?: boolean
  createdAt?: string
  updatedAt?: string
  createdBy?: number
  updatedBy?: number
  version?: number
  // 审批相关字段
  workflowInstanceId?: number
  approvalStatus?: string
  submittedAt?: string
  approvedAt?: string
  approvedBy?: number
  rejectReason?: string
}

/**
 * 内容查询参数
 */
export interface ContentQueryParams {
  siteId?: number
  categoryId?: number
  title?: string
  contentType?: string
  status?: string
  authorId?: number
  isTop?: boolean
  isFeatured?: boolean
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
 * 获取内容列表(分页)
 */
export const getContentsApi = (params?: ContentQueryParams) => {
  return request<PageResponse<Content>>({
    url: '/contents',
    method: 'get',
    params
  })
}

/**
 * 获取所有内容列表(不分页)
 */
export const getAllContentsApi = (siteId: number) => {
  return request<Content[]>({
    url: '/contents/all',
    method: 'get',
    params: { siteId }
  })
}

/**
 * 获取内容详情
 */
export const getContentApi = (id: number) => {
  return request<Content>({
    url: `/contents/${id}`,
    method: 'get'
  })
}

/**
 * 根据slug获取内容
 */
export const getContentBySlugApi = (siteId: number, slug: string) => {
  return request<Content>({
    url: `/contents/slug/${siteId}/${slug}`,
    method: 'get'
  })
}

/**
 * 创建内容
 */
export const createContentApi = (data: Content) => {
  return request<Content>({
    url: '/contents',
    method: 'post',
    data
  })
}

/**
 * 更新内容
 */
export const updateContentApi = (id: number, data: Content) => {
  return request<Content>({
    url: `/contents/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除内容
 */
export const deleteContentApi = (id: number) => {
  return request({
    url: `/contents/${id}`,
    method: 'delete'
  })
}

/**
 * 更新内容状态
 */
export const updateContentStatusApi = (id: number, status: string) => {
  return request({
    url: `/contents/${id}/status`,
    method: 'patch',
    params: { status }
  })
}

/**
 * 提交审批
 */
export const submitApprovalApi = (id: number) => {
  return request({
    url: `/contents/${id}/submit-approval`,
    method: 'post'
  })
}

/**
 * 撤回审批
 */
export const withdrawApprovalApi = (id: number) => {
  return request({
    url: `/contents/${id}/withdraw-approval`,
    method: 'post'
  })
}


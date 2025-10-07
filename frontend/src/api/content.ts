import { request } from '@/utils/request'
import type { PageData } from '@/types/api'

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
  return request<PageData<Content>>({
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
 * 提交审批（简单版本）
 */
export const submitApprovalApi = (id: number) => {
  return request({
    url: `/contents/${id}/submit-approval`,
    method: 'post'
  })
}

/**
 * 提交审批（带选项）
 */
export interface SubmitApprovalOptions {
  contentId: number
  workflowCode: string
  approvalMode: 'SERIAL' | 'PARALLEL'
  approverIds: number[]
  comment?: string
}

export const submitApprovalWithOptionsApi = (data: SubmitApprovalOptions) => {
  return request({
    url: `/contents/${data.contentId}/submit-approval-with-options`,
    method: 'post',
    data
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


// 版本DTO类型定义（与后端 ContentVersionDTO 对应），用于版本相关接口的类型声明
export interface ContentVersionDTO {
  id: number
  contentId: number
  versionNumber: number
  title: string
  slug?: string
  summary?: string
  content?: string
  coverImage?: string
  contentType?: string
  status?: string
  isTop?: boolean
  isFeatured?: boolean
  isOriginal?: boolean
  tags?: string
  metadata?: string
  changeSummary?: string
  changeType?: string
  createdBy?: number
  createdByName?: string
  createdAt?: string
  isTagged?: boolean
  tagName?: string
  fileSize?: number
}

// 获取内容的版本列表（分页）
export const getVersionListApi = (contentId: number, page = 0, size = 10) => {
  // 调用后端 GET /contents/{contentId}/versions，返回分页的版本数据
  return request<PageData<ContentVersionDTO>>({
    url: `/contents/${contentId}/versions`,
    method: 'get',
    params: { page, size }
  })
}

// 获取指定版本详情
export const getVersionApi = (contentId: number, versionNumber: number) => {
  // 调用后端 GET /contents/{contentId}/versions/{versionNumber}
  return request<ContentVersionDTO>({
    url: `/contents/${contentId}/versions/${versionNumber}`,
    method: 'get'
  })
}

// 恢复到指定版本
export const restoreVersionApi = (contentId: number, versionNumber: number) => {
  // 调用后端 POST /contents/{contentId}/versions/{versionNumber}/restore
  return request({
    url: `/contents/${contentId}/versions/${versionNumber}/restore`,
    method: 'post'
  })
}

// 比较两个版本差异
export const compareVersionsApi = (contentId: number, version1: number, version2: number) => {
  // 调用后端 GET /contents/{contentId}/versions/compare?version1=&version2=
  return request<{ version1: ContentVersionDTO; version2: ContentVersionDTO; differences: Record<string, boolean> }>({
    url: `/contents/${contentId}/versions/compare`,
    method: 'get',
    params: { version1, version2 }
  })
}

// 获取版本统计信息
export const getVersionStatisticsApi = (contentId: number) => {
  // 调用后端 GET /contents/{contentId}/versions/statistics
  return request<{ totalVersions: number; currentVersion: number }>({
    url: `/contents/${contentId}/versions/statistics`,
    method: 'get'
  })
}

// 为指定版本打标签（重要版本标记）
export const tagVersionApi = (contentId: number, versionNumber: number, tagName: string) => {
  // 调用后端 POST /contents/{contentId}/versions/{versionNumber}/tag?tagName=
  return request<ContentVersionDTO>({
    url: `/contents/${contentId}/versions/${versionNumber}/tag`,
    method: 'post',
    params: { tagName }
  })
}

// 删除指定版本
export const deleteVersionApi = (contentId: number, versionNumber: number) => {
  // 调用后端 DELETE /contents/{contentId}/versions/{versionNumber}
  return request({
    url: `/contents/${contentId}/versions/${versionNumber}`,
    method: 'delete'
  })
}


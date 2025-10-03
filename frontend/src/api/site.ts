import request from '@/utils/request'

/**
 * 站点接口
 */
export interface Site {
  id?: number
  name: string
  code: string
  domain: string
  description?: string
  logoUrl?: string
  faviconUrl?: string
  language?: string
  timezone?: string
  status?: string
  isDefault?: boolean
  seoTitle?: string
  seoKeywords?: string
  seoDescription?: string
  contactEmail?: string
  contactPhone?: string
  contactAddress?: string
  createdAt?: string
  updatedAt?: string
  createdBy?: number
  updatedBy?: number
  version?: number
}

/**
 * 站点查询参数
 */
export interface SiteQueryParams {
  name?: string
  code?: string
  domain?: string
  status?: string
  isDefault?: boolean
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
 * 获取站点列表(分页)
 */
export const getSitesApi = (params?: SiteQueryParams) => {
  return request<PageResponse<Site>>({
    url: '/sites',
    method: 'get',
    params
  })
}

/**
 * 获取所有站点列表(不分页)
 */
export const getAllSitesApi = () => {
  return request<Site[]>({
    url: '/sites/all',
    method: 'get'
  })
}

/**
 * 获取站点详情
 */
export const getSiteApi = (id: number) => {
  return request<Site>({
    url: `/sites/${id}`,
    method: 'get'
  })
}

/**
 * 根据代码获取站点
 */
export const getSiteByCodeApi = (code: string) => {
  return request<Site>({
    url: `/sites/code/${code}`,
    method: 'get'
  })
}

/**
 * 获取默认站点
 */
export const getDefaultSiteApi = () => {
  return request<Site>({
    url: '/sites/default',
    method: 'get'
  })
}

/**
 * 创建站点
 */
export const createSiteApi = (data: Site) => {
  return request<Site>({
    url: '/sites',
    method: 'post',
    data
  })
}

/**
 * 更新站点
 */
export const updateSiteApi = (id: number, data: Site) => {
  return request<Site>({
    url: `/sites/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除站点
 */
export const deleteSiteApi = (id: number) => {
  return request({
    url: `/sites/${id}`,
    method: 'delete'
  })
}

/**
 * 更新站点状态
 */
export const updateSiteStatusApi = (id: number, status: string) => {
  return request({
    url: `/sites/${id}/status`,
    method: 'patch',
    data: { status }
  })
}

/**
 * 设置默认站点
 */
export const setDefaultSiteApi = (id: number) => {
  return request({
    url: `/sites/${id}/default`,
    method: 'patch'
  })
}


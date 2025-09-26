import api from './index'
import type { ApiResponse, PageResult, Site } from './index'

export interface SiteCreateRequest {
  name: string
  code: string
  domain: string
  description?: string
  parentSiteId?: number
}

export interface SiteUpdateRequest {
  name?: string
  code?: string
  domain?: string
  description?: string
  status?: string
}

// 站点相关API
export const sitesApi = {
  // 分页查询站点
  getSites(params: {
    page?: number
    size?: number
    sort?: string
    direction?: string
    keyword?: string
    status?: string
    parentSiteId?: number
  }): Promise<ApiResponse<PageResult<Site>>> {
    return api.get('/sites', { params })
  },

  // 获取站点详情
  getSiteById(id: number): Promise<ApiResponse<Site>> {
    return api.get(`/sites/${id}`)
  },

  // 创建站点
  createSite(data: SiteCreateRequest): Promise<ApiResponse<Site>> {
    return api.post('/sites', data)
  },

  // 更新站点
  updateSite(id: number, data: SiteUpdateRequest): Promise<ApiResponse<Site>> {
    return api.put(`/sites/${id}`, data)
  },

  // 删除站点
  deleteSite(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/sites/${id}`)
  },

  // 更新站点状态
  updateSiteStatus(id: number, status: string): Promise<ApiResponse<Site>> {
    return api.patch(`/sites/${id}/status?status=${status}`)
  },

  // 获取站点树结构
  getSiteTree(): Promise<ApiResponse<Site[]>> {
    return api.get('/sites/tree')
  },

  // 获取根站点列表
  getRootSites(): Promise<ApiResponse<Site[]>> {
    return api.get('/sites/roots')
  },

  // 获取子站点列表
  getChildSites(parentId: number): Promise<ApiResponse<Site[]>> {
    return api.get(`/sites/${parentId}/children`)
  },

  // 获取站点统计信息
  getSiteStats(id: number): Promise<ApiResponse<{
    contentCount: number
    categoryCount: number
    userCount: number
    viewCount: number
  }>> {
    return api.get(`/sites/${id}/stats`)
  }
}

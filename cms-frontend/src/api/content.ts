import api from './index'
import type { ApiResponse, PageResult, Content } from './index'

export interface ContentCreateRequest {
  title: string
  slug: string
  content: string
  excerpt?: string
  type: string
  status: string
  siteId: number
  categoryId: number
  publishedAt?: string
}

export interface ContentUpdateRequest {
  title?: string
  slug?: string
  content?: string
  excerpt?: string
  type?: string
  status?: string
  categoryId?: number
  publishedAt?: string
}

// 内容相关API
export const contentApi = {
  // 分页查询内容
  getContents(params: {
    page?: number
    size?: number
    sort?: string
    direction?: string
    keyword?: string
    status?: string
    type?: string
    siteId?: number
    categoryId?: number
    authorId?: number
  }): Promise<ApiResponse<PageResult<Content>>> {
    return api.get('/contents', { params })
  },

  // 获取内容详情
  getContentById(id: number): Promise<ApiResponse<Content>> {
    return api.get(`/contents/${id}`)
  },

  // 根据slug获取内容
  getContentBySlug(siteId: number, slug: string): Promise<ApiResponse<Content>> {
    return api.get(`/contents/slug/${siteId}/${slug}`)
  },

  // 创建内容
  createContent(data: ContentCreateRequest): Promise<ApiResponse<Content>> {
    return api.post('/contents', data)
  },

  // 更新内容
  updateContent(id: number, data: ContentUpdateRequest): Promise<ApiResponse<Content>> {
    return api.put(`/contents/${id}`, data)
  },

  // 删除内容
  deleteContent(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/contents/${id}`)
  },

  // 发布内容
  publishContent(id: number): Promise<ApiResponse<Content>> {
    return api.post(`/contents/${id}/publish`)
  },

  // 取消发布内容
  unpublishContent(id: number): Promise<ApiResponse<Content>> {
    return api.post(`/contents/${id}/unpublish`)
  },

  // 归档内容
  archiveContent(id: number): Promise<ApiResponse<Content>> {
    return api.post(`/contents/${id}/archive`)
  },

  // 获取内容统计信息
  getContentStats(siteId: number): Promise<ApiResponse<{
    totalCount: number
    publishedCount: number
    draftCount: number
    archivedCount: number
    totalViews: number
  }>> {
    return api.get('/contents/stats', { params: { siteId } })
  },

  // 搜索内容
  searchContents(params: {
    keyword: string
    siteId?: number
    categoryId?: number
    type?: string
    status?: string
    page?: number
    size?: number
  }): Promise<ApiResponse<PageResult<Content>>> {
    return api.get('/contents/search', { params })
  }
}

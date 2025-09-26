import api from './index'
import type { ApiResponse, PageResult, ContentCategory } from './index'

export interface CategoryCreateRequest {
  name: string
  code: string
  description?: string
  sortOrder?: number
  parentCategoryId?: number
  siteId: number
}

export interface CategoryUpdateRequest {
  name?: string
  code?: string
  description?: string
  sortOrder?: number
  parentCategoryId?: number
}

// 分类相关API
export const categoriesApi = {
  // 分页查询分类
  getCategories(params: {
    page?: number
    size?: number
    sort?: string
    direction?: string
    siteId: number
    keyword?: string
  }): Promise<ApiResponse<PageResult<ContentCategory>>> {
    return api.get('/categories', { params })
  },

  // 获取分类树结构
  getCategoryTree(siteId: number): Promise<ApiResponse<ContentCategory[]>> {
    return api.get('/categories/tree', { params: { siteId } })
  },

  // 获取分类详情
  getCategoryById(id: number): Promise<ApiResponse<ContentCategory>> {
    return api.get(`/categories/${id}`)
  },

  // 创建分类
  createCategory(data: CategoryCreateRequest): Promise<ApiResponse<ContentCategory>> {
    return api.post('/categories', data)
  },

  // 更新分类
  updateCategory(id: number, data: CategoryUpdateRequest): Promise<ApiResponse<ContentCategory>> {
    return api.put(`/categories/${id}`, data)
  },

  // 删除分类
  deleteCategory(id: number): Promise<ApiResponse<void>> {
    return api.delete(`/categories/${id}`)
  },

  // 获取子分类列表
  getChildCategories(parentId: number, siteId: number): Promise<ApiResponse<ContentCategory[]>> {
    return api.get(`/categories/${parentId}/children`, { params: { siteId } })
  },

  // 移动分类
  moveCategory(id: number, data: { parentCategoryId?: number; sortOrder: number }): Promise<ApiResponse<ContentCategory>> {
    return api.patch(`/categories/${id}/move`, data)
  }
}

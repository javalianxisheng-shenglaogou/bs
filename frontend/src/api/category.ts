import request from '@/utils/request'

/**
 * 分类接口
 */
export interface Category {
  id?: number
  siteId: number
  parentId?: number
  name: string
  code: string
  description?: string
  iconUrl?: string
  coverUrl?: string
  sortOrder?: number
  level?: number
  path?: string
  isVisible?: boolean
  seoTitle?: string
  seoKeywords?: string
  seoDescription?: string
  createdAt?: string
  updatedAt?: string
  createdBy?: number
  updatedBy?: number
  version?: number
  children?: Category[]
}

/**
 * 分类查询参数
 */
export interface CategoryQueryParams {
  siteId?: number
  parentId?: number
  name?: string
  code?: string
  isVisible?: boolean
  level?: number
  page?: number
  size?: number
  sortBy?: string
  direction?: string
}

/**
 * 获取分类树
 */
export const getCategoryTreeApi = (siteId: number) => {
  return request<Category[]>({
    url: '/categories/tree',
    method: 'get',
    params: { siteId }
  })
}

/**
 * 获取可见分类树
 */
export const getVisibleCategoryTreeApi = (siteId: number) => {
  return request<Category[]>({
    url: '/categories/tree/visible',
    method: 'get',
    params: { siteId }
  })
}

/**
 * 分页查询分类
 */
export const getCategoriesApi = (params: CategoryQueryParams) => {
  return request({
    url: '/categories',
    method: 'get',
    params
  })
}

/**
 * 获取分类详情
 */
export const getCategoryByIdApi = (id: number) => {
  return request<Category>({
    url: `/categories/${id}`,
    method: 'get'
  })
}

/**
 * 创建分类
 */
export const createCategoryApi = (data: Category) => {
  return request<Category>({
    url: '/categories',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 */
export const updateCategoryApi = (id: number, data: Category) => {
  return request<Category>({
    url: `/categories/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export const deleteCategoryApi = (id: number) => {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}

/**
 * 更新分类可见性
 */
export const updateCategoryVisibilityApi = (id: number, isVisible: boolean) => {
  return request({
    url: `/categories/${id}/visibility`,
    method: 'put',
    data: { isVisible }
  })
}


import { request } from '@/utils/request'
import type { PageData } from '@/types/api'

/**
 * 访客端API接口
 */

// 公开内容DTO
export interface PublicContentDTO {
  id: number
  title: string
  summary?: string
  coverImage?: string
  categoryId?: number
  categoryName?: string
  authorName?: string
  viewCount: number
  publishedAt: string
  isTop: boolean
  isFeatured: boolean
}

// 公开内容详情DTO
export interface PublicContentDetailDTO {
  id: number
  siteId: number
  siteName?: string
  title: string
  content: string
  summary?: string
  coverImage?: string
  categoryId?: number
  categoryName?: string
  authorName?: string
  viewCount: number
  publishedAt: string
  isTop: boolean
  isFeatured: boolean
  isOriginal: boolean
}

// 分类树DTO
export interface CategoryTreeDTO {
  id: number
  name: string
  code: string
  iconUrl?: string
  contentCount: number
  children: CategoryTreeDTO[]
}

// 首页数据DTO
export interface HomePageDTO {
  categories: CategoryTreeDTO[]
  topContents: PublicContentDTO[]
  featuredContents: PublicContentDTO[]
}

// 站点DTO
export interface SiteDTO {
  id: number
  name: string
  code: string
  domain: string
  description?: string
  logoUrl?: string
  status: string
}

/**
 * 获取首页数据
 */
export const getHomePageData = (siteId: number) => {
  return request<HomePageDTO>({
    url: '/guest/contents/home',
    method: 'get',
    params: { siteId }
  })
}

/**
 * 获取已发布内容列表
 */
export const getPublishedContents = (params: {
  siteId: number
  categoryId?: number
  page?: number
  size?: number
}) => {
  return request<PageData<PublicContentDTO>>({
    url: '/guest/contents',
    method: 'get',
    params
  })
}

/**
 * 搜索内容
 */
export const searchContents = (params: {
  siteId: number
  keyword: string
  page?: number
  size?: number
}) => {
  return request<PageData<PublicContentDTO>>({
    url: '/guest/contents/search',
    method: 'get',
    params
  })
}

/**
 * 获取内容详情
 */
export const getContentDetail = (id: number) => {
  return request<PublicContentDetailDTO>({
    url: `/guest/contents/${id}`,
    method: 'get'
  })
}

/**
 * 获取相关内容
 */
export const getRelatedContents = (id: number, limit: number = 5) => {
  return request<PublicContentDTO[]>({
    url: `/guest/contents/${id}/related`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取分类树
 */
export const getCategoryTree = (siteId: number) => {
  return request<CategoryTreeDTO[]>({
    url: '/guest/categories',
    method: 'get',
    params: { siteId }
  })
}

/**
 * 获取活跃站点列表
 */
export const getActiveSites = () => {
  return request<SiteDTO[]>({
    url: '/guest/sites',
    method: 'get'
  })
}


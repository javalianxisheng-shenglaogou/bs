/**
 * 用户状态
 */
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'LOCKED'

/**
 * 内容状态
 */
export type ContentStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED'

/**
 * 审批状态
 */
export type ApprovalStatus = 'NONE' | 'PENDING' | 'APPROVED' | 'REJECTED'

/**
 * 工作流状态
 */
export type WorkflowStatus = 'ACTIVE' | 'INACTIVE'

/**
 * 工作流实例状态
 */
export type WorkflowInstanceStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

/**
 * 任务状态
 */
export type TaskStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

/**
 * 用户接口
 */
export interface User {
  id: number
  username: string
  nickname: string
  realName?: string
  email: string
  mobile?: string
  avatar?: string
  gender?: string
  birthday?: string
  bio?: string
  status: UserStatus
  roles: Role[]
  createdAt: string
  updatedAt: string
}

/**
 * 角色接口
 */
export interface Role {
  id: number
  code: string
  name: string
  description?: string
  permissions: Permission[]
  createdAt: string
  updatedAt: string
}

/**
 * 权限接口
 */
export interface Permission {
  id: number
  code: string
  name: string
  module: string
  description?: string
  createdAt: string
  updatedAt: string
}

/**
 * 站点接口
 */
export interface Site {
  id: number
  name: string
  code: string
  domain: string
  description?: string
  logo?: string
  favicon?: string
  keywords?: string
  seoDescription?: string
  status: 'ACTIVE' | 'INACTIVE'
  createdAt: string
  updatedAt: string
}

/**
 * 分类接口
 */
export interface Category {
  id: number
  name: string
  slug: string
  description?: string
  parentId?: number
  siteId: number
  sortOrder: number
  visible: boolean
  icon?: string
  coverImage?: string
  seoTitle?: string
  seoKeywords?: string
  seoDescription?: string
  children?: Category[]
  createdAt: string
  updatedAt: string
}

/**
 * 内容接口
 */
export interface Content {
  id: number
  title: string
  slug: string
  content: string
  excerpt?: string
  coverImage?: string
  status: ContentStatus
  approvalStatus: ApprovalStatus
  siteId: number
  siteName?: string
  categoryId: number
  categoryName?: string
  authorId: number
  authorName?: string
  tags?: string[]
  viewCount: number
  likeCount: number
  commentCount: number
  featured: boolean
  allowComment: boolean
  seoTitle?: string
  seoKeywords?: string
  seoDescription?: string
  publishedAt?: string
  workflowInstanceId?: number
  submittedAt?: string
  approvedAt?: string
  approvedBy?: number
  createdAt: string
  updatedAt: string
}

/**
 * 工作流接口
 */
export interface Workflow {
  id: number
  name: string
  code: string
  description?: string
  status: WorkflowStatus
  steps: WorkflowStep[]
  createdAt: string
  updatedAt: string
}

/**
 * 工作流步骤接口
 */
export interface WorkflowStep {
  id: number
  name: string
  stepOrder: number
  approverType: 'USER' | 'ROLE'
  approverId?: number
  approverName?: string
  required: boolean
}

/**
 * 工作流实例接口
 */
export interface WorkflowInstance {
  id: number
  workflowId: number
  workflowName?: string
  entityType: string
  entityId: number
  status: WorkflowInstanceStatus
  currentStep: number
  initiatorId: number
  initiatorName?: string
  comment?: string
  tasks: WorkflowTask[]
  createdAt: string
  updatedAt: string
}

/**
 * 工作流任务接口
 */
export interface WorkflowTask {
  id: number
  instanceId: number
  stepId: number
  stepName?: string
  assigneeId: number
  assigneeName?: string
  status: TaskStatus
  comment?: string
  completedAt?: string
  createdAt: string
  updatedAt: string
}

/**
 * 系统日志接口
 */
export interface SystemLog {
  id: number
  userId?: number
  username?: string
  module: string
  action: string
  method: string
  url: string
  ip: string
  userAgent?: string
  requestParams?: string
  responseData?: string
  status: number
  errorMessage?: string
  duration: number
  success: boolean
  createdAt: string
}

/**
 * 分页响应接口
 */
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
  empty: boolean
}

/**
 * API响应接口
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  success: boolean
  timestamp: string
}

/**
 * 登录请求接口
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 登录响应接口
 */
export interface LoginResponse {
  token: string
  user: User
}

/**
 * 用户创建请求接口
 */
export interface UserCreateRequest {
  username: string
  password: string
  email: string
  mobile?: string
  nickname: string
  realName?: string
  gender?: string
  birthday?: string
  bio?: string
  status?: UserStatus
  roleIds: number[]
}

/**
 * 用户更新请求接口
 */
export interface UserUpdateRequest {
  email?: string
  mobile?: string
  nickname?: string
  realName?: string
  gender?: string
  birthday?: string
  bio?: string
  status?: UserStatus
  roleIds?: number[]
}

/**
 * 内容创建/更新请求接口
 */
export interface ContentRequest {
  title: string
  slug?: string
  content: string
  excerpt?: string
  coverImage?: string
  status?: ContentStatus
  siteId: number
  categoryId: number
  tags?: string[]
  featured?: boolean
  allowComment?: boolean
  seoTitle?: string
  seoKeywords?: string
  seoDescription?: string
}

/**
 * 提交审批选项接口
 */
export interface SubmitApprovalOptions {
  workflowId: number
  comment?: string
  assigneeId?: number
}


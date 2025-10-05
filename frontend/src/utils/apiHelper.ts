import { handleError } from './errorHandler'

/**
 * 获取列表数据的通用函数
 * @param apiFunc API函数
 * @param params 请求参数
 * @returns 数据数组
 */
export async function fetchList<T>(
  apiFunc: Function,
  params: any = {}
): Promise<T[]> {
  try {
    const response = await apiFunc(params)
    console.log('📊 API响应:', response)
    
    // 处理不同的响应格式
    if (response.data?.data) {
      const data = response.data.data
      
      // 如果是分页数据
      if (data.content && Array.isArray(data.content)) {
        return data.content
      }
      
      // 如果是数组
      if (Array.isArray(data)) {
        return data
      }
      
      // 如果是对象，尝试获取items或list字段
      if (data.items && Array.isArray(data.items)) {
        return data.items
      }
      
      if (data.list && Array.isArray(data.list)) {
        return data.list
      }
    }
    
    // 如果直接是数组
    if (Array.isArray(response.data)) {
      return response.data
    }
    
    console.warn('⚠️ 未知的响应格式:', response)
    return []
  } catch (error) {
    handleError(error)
    return []
  }
}

/**
 * 获取分页数据的通用函数
 * @param apiFunc API函数
 * @param params 请求参数
 * @returns 分页数据
 */
export async function fetchPage<T>(
  apiFunc: Function,
  params: any = {}
): Promise<{ content: T[], total: number, page: number, size: number }> {
  try {
    const response = await apiFunc(params)
    console.log('📊 API响应:', response)
    
    if (response.data?.data) {
      const data = response.data.data
      
      // 标准分页格式
      if (data.content && Array.isArray(data.content)) {
        return {
          content: data.content,
          total: data.totalElements || data.total || 0,
          page: data.number || data.page || 0,
          size: data.size || data.pageSize || 10
        }
      }
      
      // 其他分页格式
      if (data.items && Array.isArray(data.items)) {
        return {
          content: data.items,
          total: data.total || 0,
          page: data.page || 0,
          size: data.size || 10
        }
      }
    }
    
    console.warn('⚠️ 未知的分页响应格式:', response)
    return {
      content: [],
      total: 0,
      page: 0,
      size: 10
    }
  } catch (error) {
    handleError(error)
    return {
      content: [],
      total: 0,
      page: 0,
      size: 10
    }
  }
}

/**
 * 获取单个数据的通用函数
 * @param apiFunc API函数
 * @param id 数据ID
 * @returns 数据对象或null
 */
export async function fetchOne<T>(
  apiFunc: Function,
  id: number | string
): Promise<T | null> {
  try {
    const response = await apiFunc(id)
    console.log('📊 API响应:', response)
    
    if (response.data?.data) {
      return response.data.data
    }
    
    if (response.data) {
      return response.data
    }
    
    return null
  } catch (error) {
    handleError(error)
    return null
  }
}

/**
 * 创建数据的通用函数
 * @param apiFunc API函数
 * @param data 数据对象
 * @param successMessage 成功消息
 * @returns 创建的数据或null
 */
export async function createData<T>(
  apiFunc: Function,
  data: any,
  successMessage: string = '创建成功'
): Promise<T | null> {
  try {
    const response = await apiFunc(data)
    console.log('✅ 创建成功:', response)
    
    const { ElMessage } = await import('element-plus')
    ElMessage.success(successMessage)
    
    if (response.data?.data) {
      return response.data.data
    }
    
    return response.data
  } catch (error) {
    handleError(error, '创建失败')
    return null
  }
}

/**
 * 更新数据的通用函数
 * @param apiFunc API函数
 * @param id 数据ID
 * @param data 数据对象
 * @param successMessage 成功消息
 * @returns 更新的数据或null
 */
export async function updateData<T>(
  apiFunc: Function,
  id: number | string,
  data: any,
  successMessage: string = '更新成功'
): Promise<T | null> {
  try {
    const response = await apiFunc(id, data)
    console.log('✅ 更新成功:', response)
    
    const { ElMessage } = await import('element-plus')
    ElMessage.success(successMessage)
    
    if (response.data?.data) {
      return response.data.data
    }
    
    return response.data
  } catch (error) {
    handleError(error, '更新失败')
    return null
  }
}

/**
 * 删除数据的通用函数
 * @param apiFunc API函数
 * @param id 数据ID
 * @param successMessage 成功消息
 * @returns 是否成功
 */
export async function deleteData(
  apiFunc: Function,
  id: number | string,
  successMessage: string = '删除成功'
): Promise<boolean> {
  try {
    // 确认删除
    const { ElMessageBox } = await import('element-plus')
    await ElMessageBox.confirm('确定要删除吗？此操作不可恢复。', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await apiFunc(id)
    console.log('✅ 删除成功:', response)
    
    const { ElMessage } = await import('element-plus')
    ElMessage.success(successMessage)
    
    return true
  } catch (error: any) {
    if (error !== 'cancel') {
      handleError(error, '删除失败')
    }
    return false
  }
}

/**
 * 批量删除数据的通用函数
 * @param apiFunc API函数
 * @param ids 数据ID数组
 * @param successMessage 成功消息
 * @returns 是否成功
 */
export async function batchDelete(
  apiFunc: Function,
  ids: (number | string)[],
  successMessage: string = '批量删除成功'
): Promise<boolean> {
  try {
    if (ids.length === 0) {
      const { ElMessage } = await import('element-plus')
      ElMessage.warning('请选择要删除的数据')
      return false
    }
    
    // 确认删除
    const { ElMessageBox } = await import('element-plus')
    await ElMessageBox.confirm(
      `确定要删除选中的 ${ids.length} 条数据吗？此操作不可恢复。`,
      '确认批量删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await apiFunc(ids)
    console.log('✅ 批量删除成功:', response)
    
    const { ElMessage } = await import('element-plus')
    ElMessage.success(successMessage)
    
    return true
  } catch (error: any) {
    if (error !== 'cancel') {
      handleError(error, '批量删除失败')
    }
    return false
  }
}


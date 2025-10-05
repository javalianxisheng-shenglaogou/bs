import { ElMessage } from 'element-plus'
import router from '@/router'

/**
 * 统一错误处理函数
 * @param error 错误对象
 * @param customMessage 自定义错误消息
 */
export function handleError(error: any, customMessage?: string) {
  console.error('❌ 错误详情:', error)
  
  if (error.response) {
    const status = error.response.status
    const message = error.response.data?.message || customMessage || '请求失败'
    
    switch (status) {
      case 400:
        ElMessage.error(`请求参数错误: ${message}`)
        break
      case 401:
        ElMessage.error('未登录或登录已过期，请重新登录')
        // 清除token
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        // 跳转到登录页
        router.push('/login')
        break
      case 403:
        ElMessage.error(`没有权限访问: ${message}`)
        break
      case 404:
        ElMessage.error('请求的资源不存在')
        break
      case 500:
        ElMessage.error(`服务器错误: ${message}`)
        break
      case 502:
        ElMessage.error('网关错误，请稍后重试')
        break
      case 503:
        ElMessage.error('服务暂时不可用，请稍后重试')
        break
      default:
        ElMessage.error(message)
    }
  } else if (error.request) {
    // 请求已发送但没有收到响应
    ElMessage.error('网络错误，请检查网络连接')
  } else {
    // 其他错误
    ElMessage.error(customMessage || error.message || '未知错误')
  }
}

/**
 * 处理API响应
 * @param response API响应
 * @param successMessage 成功消息
 * @returns 是否成功
 */
export function handleResponse(response: any, successMessage?: string): boolean {
  if (response.data?.code === 200 || response.data?.success) {
    if (successMessage) {
      ElMessage.success(successMessage)
    }
    return true
  } else {
    const message = response.data?.message || '操作失败'
    ElMessage.error(message)
    return false
  }
}

/**
 * 确认对话框
 * @param message 确认消息
 * @param title 标题
 * @returns Promise<boolean>
 */
export async function confirm(message: string, title: string = '确认操作'): Promise<boolean> {
  try {
    const { ElMessageBox } = await import('element-plus')
    await ElMessageBox.confirm(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    return true
  } catch {
    return false
  }
}

/**
 * 显示成功消息
 * @param message 消息内容
 */
export function showSuccess(message: string) {
  ElMessage.success(message)
}

/**
 * 显示错误消息
 * @param message 消息内容
 */
export function showError(message: string) {
  ElMessage.error(message)
}

/**
 * 显示警告消息
 * @param message 消息内容
 */
export function showWarning(message: string) {
  ElMessage.warning(message)
}

/**
 * 显示信息消息
 * @param message 消息内容
 */
export function showInfo(message: string) {
  ElMessage.info(message)
}


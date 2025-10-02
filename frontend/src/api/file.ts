import request from '@/utils/request'

/**
 * 文件上传响应
 */
export interface FileUploadResponse {
  filename: string
  originalFilename: string
  url: string
  size: number
  contentType: string
  path: string
}

/**
 * 上传头像
 */
export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request<FileUploadResponse>({
    url: '/files/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传图片
 */
export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request<FileUploadResponse>({
    url: '/files/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传文件
 */
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request<FileUploadResponse>({
    url: '/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}


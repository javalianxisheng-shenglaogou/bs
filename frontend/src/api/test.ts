import request from '../utils/request'

/**
 * 健康检查
 */
export function healthCheck() {
  return request({
    url: '/test/health',
    method: 'get'
  })
}

/**
 * Hello测试
 */
export function hello(name?: string) {
  return request({
    url: '/test/hello',
    method: 'get',
    params: { name }
  })
}


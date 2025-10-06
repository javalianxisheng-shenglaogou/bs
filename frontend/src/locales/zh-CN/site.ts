/**
 * 中文语言包 - 站点模块
 */
export default {
  title: '站点管理',
  list: '站点列表',
  add: '创建站点',
  edit: '编辑站点',
  delete: '删除站点',
  detail: '站点详情',
  
  fields: {
    id: 'ID',
    name: '站点名称',
    domain: '域名',
    description: '描述',
    status: '状态',
    language: '语言',
    timezone: '时区',
    theme: '主题',
    logo: 'Logo',
    favicon: 'Favicon',
    seoTitle: 'SEO标题',
    seoKeywords: 'SEO关键词',
    seoDescription: 'SEO描述',
    createdAt: '创建时间',
    updatedAt: '更新时间'
  },
  
  status: {
    active: '启用',
    inactive: '停用',
    maintenance: '维护中'
  },
  
  placeholder: {
    name: '请输入站点名称',
    domain: '请输入域名',
    description: '请输入描述',
    searchKeyword: '搜索站点名称或域名',
    selectStatus: '选择状态',
    selectLanguage: '选择语言',
    selectTimezone: '选择时区'
  },
  
  validation: {
    nameRequired: '请输入站点名称',
    nameLength: '站点名称长度为1-100个字符',
    domainRequired: '请输入域名',
    domainInvalid: '域名格式不正确'
  },
  
  message: {
    addSuccess: '站点创建成功',
    addFailed: '站点创建失败',
    updateSuccess: '站点更新成功',
    updateFailed: '站点更新失败',
    deleteSuccess: '站点删除成功',
    deleteFailed: '站点删除失败',
    deleteConfirm: '确定要删除站点 "{name}" 吗？',
    loadFailed: '加载站点列表失败'
  }
}


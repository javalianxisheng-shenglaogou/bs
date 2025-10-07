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
    code: '站点代码',
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
    contactEmail: '联系邮箱',
    contactPhone: '联系电话',
    contactAddress: '联系地址',
    isDefault: '默认站点',
    createdAt: '创建时间',
    updatedAt: '更新时间'
  },
  
  status: {
    active: '启用',
    inactive: '停用',
    maintenance: '维护中',
    ACTIVE: '运行中',
    INACTIVE: '已停用'
  },
  
  placeholder: {
    name: '请输入站点名称',
    code: '请输入站点代码(小写字母、数字、下划线、连字符)',
    domain: '请输入站点域名',
    description: '请输入站点描述',
    searchKeyword: '搜索站点名称或域名',
    selectStatus: '选择状态',
    selectLanguage: '请选择默认语言',
    selectTimezone: '请选择时区',
    selectSite: '选择站点',
    seoTitle: '请输入SEO标题',
    seoKeywords: '请输入SEO关键词,多个用逗号分隔',
    seoDescription: '请输入SEO描述',
    contactEmail: '请输入联系邮箱',
    contactPhone: '请输入联系电话',
    contactAddress: '请输入联系地址',
    customCss: '请输入自定义CSS代码',
    customJs: '请输入自定义JavaScript代码'
  },
  
  validation: {
    nameRequired: '请输入站点名称',
    nameLength: '站点名称长度为1-100个字符',
    nameMaxLength: '站点名称长度不能超过100',
    codeRequired: '请输入站点代码',
    codeMaxLength: '站点代码长度不能超过50',
    codePattern: '站点代码只能包含小写字母、数字、下划线和连字符',
    domainRequired: '请输入站点域名',
    domainMaxLength: '站点域名长度不能超过255',
    domainInvalid: '域名格式不正确',
    siteRequired: '请选择站点'
  },
  
  message: {
    addSuccess: '站点创建成功',
    addFailed: '站点创建失败',
    updateSuccess: '站点更新成功',
    updateFailed: '站点更新失败',
    deleteSuccess: '站点删除成功',
    deleteFailed: '站点删除失败',
    deleteConfirm: '确定要删除站点 "{name}" 吗？',
    loadFailed: '加载站点列表失败',
    createSuccess: '创建成功',
    saveFailed: '保存站点失败',
    configSaveSuccess: '配置保存成功',
    configSaveFailed: '保存配置失败',
    logoUploadSuccess: 'Logo上传成功',
    faviconUploadSuccess: 'Favicon上传成功'
  },

  // 新增内容
  addSite: '新增站点',
  config: '配置',
  empty: '暂无站点数据',
  noDescription: '暂无描述',
  defaultSite: '默认站点',

  // 配置相关
  siteConfig: '站点配置',
  basicConfig: '基本配置',
  themeConfig: '主题配置',
  advancedConfig: '高级配置',
  primaryColor: '主题色',
  secondaryColor: '辅助色',
  customCss: '自定义CSS',
  customJs: '自定义JS',
  saveConfig: '保存配置',

  // SEO设置
  seoSettings: 'SEO设置',
  contactInfo: '联系信息',

  // 语言选项
  languages: {
    zh_CN: '简体中文',
    en_US: 'English',
    zh_TW: '繁體中文'
  }
}


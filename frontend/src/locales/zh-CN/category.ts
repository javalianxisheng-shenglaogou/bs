/**
 * 中文语言包 - 分类模块
 */
export default {
  title: '分类管理',
  list: '分类列表',
  add: '新增分类',
  addChild: '新增子分类',
  edit: '编辑分类',
  delete: '删除分类',
  query: '查询',
  
  fields: {
    id: 'ID',
    name: '分类名称',
    code: '分类编码',
    description: '描述',
    sortOrder: '排序',
    level: '层级',
    isVisible: '可见性',
    iconUrl: '分类图标',
    coverUrl: '封面图',
    seoTitle: 'SEO标题',
    seoKeywords: 'SEO关键词',
    seoDescription: 'SEO描述',
    parentId: '父分类',
    createdAt: '创建时间'
  },
  
  status: {
    visible: '可见',
    hidden: '隐藏'
  },
  
  actions: {
    addChild: '子分类',
    edit: '编辑',
    show: '显示',
    hide: '隐藏',
    delete: '删除'
  },
  
  tabs: {
    basic: '基本信息',
    images: '图片设置',
    seo: 'SEO设置'
  },
  
  placeholder: {
    selectSite: '请选择站点',
    selectParent: '请选择父分类(不选则为顶级分类)',
    name: '请输入分类名称',
    code: '请输入分类编码(英文字母、数字、下划线)',
    description: '请输入分类描述',
    seoTitle: '请输入SEO标题(不填则使用分类名称)',
    seoKeywords: '请输入SEO关键词,多个关键词用逗号分隔',
    seoDescription: '请输入SEO描述'
  },
  
  validation: {
    siteRequired: '请选择站点',
    nameRequired: '请输入分类名称',
    nameMaxLength: '分类名称长度不能超过100',
    codeRequired: '请输入分类编码',
    codeMaxLength: '分类编码长度不能超过50',
    codePattern: '分类编码只能包含字母、数字和下划线'
  },
  
  message: {
    loadSitesFailed: '加载站点列表失败',
    loadTreeFailed: '加载分类树失败',
    selectSiteFirst: '请先选择站点',
    createSuccess: '创建成功',
    updateSuccess: '更新成功',
    deleteSuccess: '删除成功',
    saveFailed: '保存分类失败',
    deleteFailed: '删除分类失败',
    deleteConfirm: '确定要删除分类"{name}"吗?',
    uploadSuccess: '上传成功',
    uploadFailed: '上传失败',
    iconUploadSuccess: '图标上传成功',
    coverUploadSuccess: '封面上传成功',
    onlyJpgPng: '只能上传 JPG/PNG 格式的图片!',
    sizeTooLarge: '图片大小不能超过 2MB!'
  },
  
  tips: {
    sortOrder: '数字越小越靠前',
    iconSize: '建议尺寸: 64x64px, 支持jpg/png格式, 大小不超过2MB',
    coverSize: '建议尺寸: 800x450px, 支持jpg/png格式, 大小不超过2MB',
    deleteIcon: '删除图标',
    deleteCover: '删除封面'
  }
}

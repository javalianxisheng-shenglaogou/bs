/**
 * 中文语言包 - 内容模块
 */
export default {
  title: '内容管理',
  list: '内容列表',
  add: '创建内容',
  edit: '编辑内容',
  delete: '删除内容',
  detail: '内容详情',
  publish: '发布内容',
  unpublish: '撤回内容',
  
  fields: {
    id: 'ID',
    title: '标题',
    slug: 'URL别名',
    content: '内容',
    excerpt: '摘要',
    author: '作者',
    category: '分类',
    tags: '标签',
    status: '状态',
    publishedAt: '发布时间',
    createdAt: '创建时间',
    updatedAt: '更新时间',
    views: '浏览量',
    featured: '推荐',
    seoTitle: 'SEO标题',
    seoKeywords: 'SEO关键词',
    seoDescription: 'SEO描述',
    contentType: '内容类型',
    approvalStatus: '审批状态',
    authorName: '作者',
    viewCount: '浏览量',
    isTop: '置顶',
    isFeatured: '推荐',
    coverImage: '封面图',
    attributes: '内容属性',
    isOriginal: '原创'
  },
  
  status: {
    draft: '草稿',
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回',
    published: '已发布',
    archived: '已归档'
  },

  contentTypes: {
    article: '文章',
    page: '页面',
    news: '新闻',
    product: '产品',
    custom: '自定义',
    other: '其他'
  },

  approvalStatus: {
    none: '未提交',
    pending: '审批中',
    approved: '已通过',
    rejected: '已拒绝'
  },
  
  placeholder: {
    title: '请输入标题',
    slug: '请输入URL别名',
    content: '请输入内容',
    excerpt: '请输入摘要',
    searchKeyword: '搜索标题或内容',
    selectCategory: '选择分类',
    selectStatus: '选择状态',
    selectTags: '选择标签',
    selectContentType: '请选择内容类型'
  },
  
  validation: {
    titleRequired: '请输入标题',
    titleLength: '标题长度为1-200个字符',
    contentRequired: '请输入内容',
    categoryRequired: '请选择分类',
    slugRequired: '请输入URL别名',
    slugLength: 'URL别名长度不能超过200',
    slugPattern: 'URL别名只能包含小写字母、数字和连字符'
  },
  
  message: {
    addSuccess: '内容创建成功',
    addFailed: '内容创建失败',
    updateSuccess: '内容更新成功',
    updateFailed: '内容更新失败',
    deleteSuccess: '内容删除成功',
    deleteFailed: '内容删除失败',
    publishSuccess: '内容发布成功',
    publishFailed: '内容发布失败',
    unpublishSuccess: '内容撤回成功',
    unpublishFailed: '内容撤回失败',
    deleteConfirm: '确定要删除内容 "{title}" 吗？',
    loadFailed: '加载内容列表失败',
    cannotEditPublished: '已发布的内容不能直接编辑，请先撤回',
    preview: '预览',
    uploadSuccess: '封面上传成功',
    uploadFailed: '封面上传失败',
    uploadFormatError: '封面图只能是 JPG/PNG 格式!',
    uploadSizeError: '封面图大小不能超过 2MB!'
  },
  
  category: {
    title: '分类管理',
    add: '新增分类',
    edit: '编辑分类',
    delete: '删除分类',
    name: '分类名称',
    slug: '分类别名',
    description: '分类描述',
    parent: '父分类'
  },

  // 版本控制相关翻译
  version: {
    title: '版本控制',
    history: '版本历史',
    detail: '版本详情',
    compare: '版本对比',
    restore: '版本恢复',
    tag: '版本标记',
    delete: '删除版本',

    fields: {
      versionNumber: '版本号',
      changeType: '变更类型',
      changeSummary: '变更摘要',
      createdBy: '变更人',
      createdAt: '变更时间',
      tagName: '标签',
      currentVersion: '当前版本',
      totalVersions: '版本总数'
    },

    changeTypes: {
      CREATE: '创建',
      UPDATE: '更新',
      PUBLISH: '发布',
      UNPUBLISH: '撤回',
      RESTORE: '恢复',
      STATUS_CHANGE: '状态变更'
    },

    actions: {
      view: '查看',
      restore: '恢复',
      tag: '打标',
      delete: '删除',
      compare: '对比',
      compareSelected: '对比选中两个版本',
      viewHistory: '查看并管理内容版本历史'
    },

    messages: {
      restoreSuccess: '版本恢复成功',
      restoreFailed: '版本恢复失败',
      tagSuccess: '版本标记成功',
      tagFailed: '版本标记失败',
      deleteSuccess: '版本删除成功',
      deleteFailed: '版本删除失败',
      loadFailed: '加载版本列表失败',
      restoreConfirm: '确定要恢复到版本 {version} 吗？',
      deleteConfirm: '确定要删除版本 {version} 吗？删除后无法恢复！',
      selectTwoVersions: '请选择两个版本进行对比',
      noVersions: '暂无版本记录'
    },

    dialog: {
      versionDetail: '版本详情',
      versionCompare: '版本对比',
      versionA: '版本A（{version}）',
      versionB: '版本B（{version}）',
      tagVersion: '标记版本',
      tagName: '标签名称',
      tagPlaceholder: '请输入标签名称，如：里程碑、发布前等'
    },

    tagged: '已标记',
    diffFields: '差异字段',
    changed: '已变更',
    unchanged: '无变化',
    noDifferences: '未检测到差异或服务未返回差异字段。'
  },

  uploadTip: '建议尺寸: 800x450px, 支持jpg/png格式, 大小不超过2MB'
}


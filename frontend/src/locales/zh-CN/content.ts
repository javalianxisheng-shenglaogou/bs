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
    seoDescription: 'SEO描述'
  },
  
  status: {
    draft: '草稿',
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回',
    published: '已发布',
    archived: '已归档'
  },
  
  placeholder: {
    title: '请输入标题',
    slug: '请输入URL别名',
    content: '请输入内容',
    excerpt: '请输入摘要',
    searchKeyword: '搜索标题或内容',
    selectCategory: '选择分类',
    selectStatus: '选择状态',
    selectTags: '选择标签'
  },
  
  validation: {
    titleRequired: '请输入标题',
    titleLength: '标题长度为1-200个字符',
    contentRequired: '请输入内容',
    categoryRequired: '请选择分类'
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
    cannotEditPublished: '已发布的内容不能直接编辑，请先撤回'
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
  }
}


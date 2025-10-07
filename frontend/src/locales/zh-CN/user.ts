/**
 * 中文语言包 - 用户模块
 */
export default {
  title: '用户管理',
  list: '用户列表',
  add: '新增用户',
  edit: '编辑用户',
  delete: '删除用户',
  detail: '用户详情',
  
  fields: {
    id: 'ID',
    username: '用户名',
    password: '密码',
    confirmPassword: '确认密码',
    nickname: '昵称',
    realName: '真实姓名',
    email: '邮箱',
    mobile: '手机号',
    avatar: '头像',
    status: '状态',
    roles: '角色',
    createdAt: '创建时间',
    updatedAt: '更新时间',
    lastLoginAt: '最后登录时间',
    lastLoginIp: '最后登录IP'
  },
  
  status: {
    all: '全部状态',
    active: '正常',
    inactive: '停用',
    locked: '锁定',
    pending: '待激活'
  },
  
  placeholder: {
    username: '请输入用户名',
    password: '请输入密码',
    confirmPassword: '请再次输入密码',
    nickname: '请输入昵称',
    realName: '请输入真实姓名',
    email: '请输入邮箱',
    mobile: '请输入手机号',
    searchKeyword: '搜索用户名或邮箱',
    selectStatus: '状态',
    selectRole: '选择角色'
  },
  
  validation: {
    usernameRequired: '请输入用户名',
    usernameLength: '用户名长度为3-20个字符',
    passwordRequired: '请输入密码',
    passwordLength: '密码长度为6-20个字符',
    passwordMismatch: '两次输入的密码不一致',
    emailRequired: '请输入邮箱',
    emailInvalid: '邮箱格式不正确',
    mobileInvalid: '手机号格式不正确',
    roleRequired: '请选择至少一个角色'
  },
  
  message: {
    addSuccess: '用户添加成功',
    addFailed: '用户添加失败',
    updateSuccess: '用户更新成功',
    updateFailed: '用户更新失败',
    deleteSuccess: '删除成功',
    deleteFailed: '删除失败',
    deleteConfirm: '确定要删除用户 "{username}" 吗？',
    loadFailed: '加载用户列表失败',
    getUserInfoFailed: '获取用户信息失败'
  },

  info: '用户信息',
  operation: '操作',
  search: '搜索',
  table: '用户表格',
  pagination: '分页',
  dialog: '用户对话框',
  roles: {
    SUPER_ADMIN: '超级管理员',
    SITE_ADMIN: '站点管理员',
    EDITOR: '编辑者',
    VIEWER: '查看者'
  },
  buttons: {
    confirm: '确定',
    cancel: '取消'
  }
}


/**
 * 中文语言包 - 工作流模块
 */
export default {
  title: '工作流管理',
  myTasks: '我的任务',
  pendingTasks: '待办任务',
  completedTasks: '已完成任务',
  taskDetail: '任务详情',
  approve: '审批',
  reject: '驳回',
  submitApproval: '提交审批',
  withdrawApproval: '撤回审批',
  
  fields: {
    id: 'ID',
    taskName: '任务名称',
    taskType: '任务类型',
    assignee: '处理人',
    status: '状态',
    priority: '优先级',
    dueDate: '截止日期',
    createdAt: '创建时间',
    updatedAt: '更新时间',
    completedAt: '完成时间',
    comment: '审批意见',
    contentTitle: '内容标题',
    submitter: '提交人'
  },
  
  status: {
    pending: '待处理',
    inProgress: '处理中',
    approved: '已通过',
    rejected: '已驳回',
    cancelled: '已取消'
  },
  
  priority: {
    low: '低',
    normal: '普通',
    high: '高',
    urgent: '紧急'
  },
  
  taskType: {
    approval: '审批',
    review: '审核',
    notification: '通知'
  },
  
  placeholder: {
    comment: '请输入审批意见',
    searchKeyword: '搜索任务名称',
    selectStatus: '选择状态',
    selectPriority: '选择优先级'
  },
  
  validation: {
    commentRequired: '请输入审批意见'
  },
  
  message: {
    approveSuccess: '审批通过',
    approveFailed: '审批失败',
    rejectSuccess: '已驳回',
    rejectFailed: '驳回失败',
    approveConfirm: '确定要通过此任务吗？',
    rejectConfirm: '确定要驳回此任务吗？',
    loadFailed: '加载任务列表失败',
    noTasks: '暂无任务'
  },
  
  tab: {
    pending: '待办',
    completed: '已完成'
  }
}


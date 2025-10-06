/**
 * English Language Pack - Workflow Module
 */
export default {
  title: 'Workflow Management',
  myTasks: 'My Tasks',
  pendingTasks: 'Pending Tasks',
  completedTasks: 'Completed Tasks',
  taskDetail: 'Task Details',
  approve: 'Approve',
  reject: 'Reject',
  
  fields: {
    id: 'ID',
    taskName: 'Task Name',
    taskType: 'Task Type',
    assignee: 'Assignee',
    status: 'Status',
    priority: 'Priority',
    dueDate: 'Due Date',
    createdAt: 'Created At',
    updatedAt: 'Updated At',
    completedAt: 'Completed At',
    comment: 'Comment',
    contentTitle: 'Content Title',
    submitter: 'Submitter'
  },
  
  status: {
    pending: 'Pending',
    inProgress: 'In Progress',
    approved: 'Approved',
    rejected: 'Rejected',
    cancelled: 'Cancelled'
  },
  
  priority: {
    low: 'Low',
    normal: 'Normal',
    high: 'High',
    urgent: 'Urgent'
  },
  
  taskType: {
    approval: 'Approval',
    review: 'Review',
    notification: 'Notification'
  },
  
  placeholder: {
    comment: 'Enter comment',
    searchKeyword: 'Search task name',
    selectStatus: 'Select status',
    selectPriority: 'Select priority'
  },
  
  validation: {
    commentRequired: 'Comment is required'
  },
  
  message: {
    approveSuccess: 'Approved successfully',
    approveFailed: 'Failed to approve',
    rejectSuccess: 'Rejected successfully',
    rejectFailed: 'Failed to reject',
    approveConfirm: 'Are you sure you want to approve this task?',
    rejectConfirm: 'Are you sure you want to reject this task?',
    loadFailed: 'Failed to load task list',
    noTasks: 'No tasks available'
  },
  
  tab: {
    pending: 'Pending',
    completed: 'Completed'
  }
}


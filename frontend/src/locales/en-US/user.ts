/**
 * English Language Pack - User Module
 */
export default {
  title: 'User Management',
  list: 'User List',
  add: 'Add User',
  edit: 'Edit User',
  delete: 'Delete User',
  detail: 'User Details',
  
  fields: {
    id: 'ID',
    username: 'Username',
    password: 'Password',
    confirmPassword: 'Confirm Password',
    nickname: 'Nickname',
    realName: 'Real Name',
    email: 'Email',
    mobile: 'Mobile',
    avatar: 'Avatar',
    status: 'Status',
    roles: 'Roles',
    createdAt: 'Created At',
    updatedAt: 'Updated At',
    lastLoginAt: 'Last Login',
    lastLoginIp: 'Last Login IP'
  },
  
  status: {
    active: 'Active',
    inactive: 'Inactive',
    locked: 'Locked',
    pending: 'Pending'
  },
  
  placeholder: {
    username: 'Enter username',
    password: 'Enter password',
    confirmPassword: 'Enter password again',
    nickname: 'Enter nickname',
    realName: 'Enter real name',
    email: 'Enter email',
    mobile: 'Enter mobile number',
    searchKeyword: 'Search username or email',
    selectStatus: 'Select status',
    selectRole: 'Select role'
  },
  
  validation: {
    usernameRequired: 'Username is required',
    usernameLength: 'Username must be 3-20 characters',
    passwordRequired: 'Password is required',
    passwordLength: 'Password must be 6-20 characters',
    passwordMismatch: 'Passwords do not match',
    emailRequired: 'Email is required',
    emailInvalid: 'Invalid email format',
    mobileInvalid: 'Invalid mobile number format',
    roleRequired: 'Please select at least one role'
  },
  
  message: {
    addSuccess: 'User added successfully',
    addFailed: 'Failed to add user',
    updateSuccess: 'User updated successfully',
    updateFailed: 'Failed to update user',
    deleteSuccess: 'User deleted successfully',
    deleteFailed: 'Failed to delete user',
    deleteConfirm: 'Are you sure you want to delete user "{username}"?',
    loadFailed: 'Failed to load user list'
  },
  
  info: 'User Information',
  operation: 'Operation'
}


<template>
  <div class="users-page">
    <div class="page-header">
      <h1>用户管理</h1>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建用户
      </el-button>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名、邮箱或昵称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="users-card">
      <div v-loading="loading">
        <el-table :data="userList" empty-text="暂无用户数据">
          <el-table-column prop="username" label="用户名" width="150" />

          <el-table-column prop="email" label="邮箱" min-width="200" />

          <el-table-column prop="nickname" label="昵称" width="150">
            <template #default="{ row }">
              <div class="user-info">
                <el-avatar :size="32" :src="row.avatarUrl">
                  {{ row.nickname?.charAt(0) }}
                </el-avatar>
                <span>{{ row.nickname }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="roles" label="角色" width="200">
            <template #default="{ row }">
              <div class="roles-container">
                <el-tag
                  v-for="role in row.roles"
                  :key="role.id"
                  type="primary"
                  size="small"
                  class="role-tag"
                >
                  {{ role.name }}
                </el-tag>
                <span v-if="!row.roles || row.roles.length === 0" class="no-roles">
                  暂无角色
                </span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag
                :type="row.status === 'ACTIVE' ? 'success' : 'danger'"
                size="small"
              >
                {{ row.status === 'ACTIVE' ? '活跃' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="lastLoginAt" label="最后登录" width="180">
            <template #default="{ row }">
              {{ row.lastLoginAt ? formatDate(row.lastLoginAt) : '从未登录' }}
            </template>
          </el-table-column>

          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button text type="primary" @click="editUser(row)">
                编辑
              </el-button>
              <el-button text type="primary" @click="manageRoles(row)">
                角色
              </el-button>
              <el-button
                text
                :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
                @click="toggleUserStatus(row)"
              >
                {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 创建/编辑用户对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingUser ? '编辑用户' : '创建用户'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="!!editingUser"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item v-if="!editingUser" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号（可选）" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio label="ACTIVE">活跃</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ editingUser ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useAppStore } from '../stores/app'
import { usersApi, type UserCreateRequest, type UserUpdateRequest } from '../api/users'
import type { User } from '../api'

const appStore = useAppStore()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const showCreateDialog = ref(false)
const userList = ref<User[]>([])
const editingUser = ref<User | null>(null)

const searchForm = reactive({
  keyword: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 表单相关
const formRef = ref<FormInstance>()
const userForm = reactive<UserCreateRequest>({
  username: '',
  email: '',
  nickname: '',
  password: '',
  phone: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 方法
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }

    const response = await usersApi.getUsers(params)
    userList.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    console.error('Failed to fetch users:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const editUser = (user: User) => {
  editingUser.value = user
  userForm.username = user.username
  userForm.email = user.email
  userForm.nickname = user.nickname
  userForm.phone = user.phone || ''
  userForm.status = user.status
  userForm.password = '' // 编辑时不显示密码
  showCreateDialog.value = true
}

const manageRoles = (user: User) => {
  ElMessage.info('角色管理功能开发中...')
}

const toggleUserStatus = async (user: User) => {
  const newStatus = user.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  const action = newStatus === 'ACTIVE' ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(
      `确定要${action}用户"${user.username}"吗？`,
      `确认${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await usersApi.updateUser(user.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    await fetchUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('Failed to toggle user status:', error)
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    submitting.value = true

    if (editingUser.value) {
      // 更新用户
      const { password, ...updateData } = userForm
      await usersApi.updateUser(editingUser.value.id, updateData)
      ElMessage.success('更新成功')
    } else {
      // 创建用户
      await usersApi.createUser(userForm)
      ElMessage.success('创建成功')
    }

    showCreateDialog.value = false
    await fetchUsers()
  } catch (error) {
    console.error('Failed to submit user:', error)
    ElMessage.error(editingUser.value ? '更新失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  editingUser.value = null
  Object.assign(userForm, {
    username: '',
    email: '',
    nickname: '',
    password: '',
    phone: '',
    status: 'ACTIVE'
  })
  formRef.value?.resetFields()
}

const handleSearch = () => {
  pagination.page = 1
  fetchUsers()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: ''
  })
  pagination.page = 1
  fetchUsers()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  fetchUsers()
}

const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  fetchUsers()
}

// 生命周期
onMounted(() => {
  appStore.setBreadcrumbs([{ title: '用户管理' }])
  fetchUsers()
})
</script>

<style scoped>
.users-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

.search-card {
  margin-bottom: 20px;
}

.users-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.roles-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.role-tag {
  margin: 0;
}

.no-roles {
  color: #909399;
  font-size: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.el-table .el-table__row) {
  cursor: pointer;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .users-page {
    padding: 16px;
  }

  :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-right: 0;
    margin-bottom: 16px;
  }
}
</style>

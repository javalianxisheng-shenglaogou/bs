<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div>
            <el-select
              v-model="selectedStatus"
              placeholder="状态"
              style="width: 120px; margin-right: 10px"
              clearable
              @change="handleSearch"
            >
              <el-option label="全部状态" value="" />
              <el-option label="正常" value="ACTIVE" />
              <el-option label="停用" value="INACTIVE" />
              <el-option label="锁定" value="LOCKED" />
              <el-option label="待激活" value="PENDING" />
            </el-select>
            <el-input
              v-model="searchText"
              placeholder="搜索用户名或邮箱"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="handleSearch">搜索</el-button>
            <el-button
              v-permission="'user:create'"
              type="primary"
              @click="handleAdd"
              style="margin-left: 10px"
            >
              <el-icon><Plus /></el-icon>
              新增用户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 用户表格 -->
      <el-table :data="userList" border stripe v-loading="loading" class="user-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="用户信息" width="280">
          <template #default="{ row }">
            <div class="user-info-cell">
              <el-avatar :size="40" class="user-avatar">
                {{ row.nickname?.charAt(0) || row.username?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="user-details">
                <div class="user-name">{{ row.nickname || row.username }}</div>
                <div class="user-username">@{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="220">
          <template #default="{ row }">
            <div class="email-cell">
              <el-icon><Message /></el-icon>
              <span>{{ row.email }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号" width="150">
          <template #default="{ row }">
            <div class="mobile-cell">
              <el-icon><Phone /></el-icon>
              <span>{{ row.mobile || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success" effect="dark">正常</el-tag>
            <el-tag v-else-if="row.status === 'LOCKED'" type="danger" effect="dark">锁定</el-tag>
            <el-tag v-else-if="row.status === 'INACTIVE'" type="info" effect="dark">停用</el-tag>
            <el-tag v-else type="warning" effect="dark">待激活</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="180">
          <template #default="{ row }">
            <div class="roles-cell">
              <el-tag
                v-for="role in row.roles"
                :key="role"
                size="small"
                :type="getRoleTagType(role)"
                effect="plain"
                class="role-tag"
              >
                {{ getRoleName(role) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              v-permission="'user:update'"
              type="primary"
              size="small"
              @click="handleEdit(row)"
              :icon="Edit"
            >
              编辑
            </el-button>
            <el-button
              v-permission="'user:delete'"
              type="danger"
              size="small"
              @click="handleDelete(row)"
              :icon="Delete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          :current-page="currentPage + 1"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户对话框 -->
    <UserDialog
      v-model:visible="dialogVisible"
      :user-id="currentUserId"
      :user-data="currentUserData"
      @success="handleDialogSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Message, Phone } from '@element-plus/icons-vue'
import { getUserList, deleteUser, getUserById, type User } from '@/api/user'
import UserDialog from '@/components/UserDialog.vue'

// 响应式数据
const loading = ref(false)
const searchText = ref('')
const selectedStatus = ref('')
const userList = ref<User[]>([])
const currentPage = ref(0)  // 从0开始，匹配后端分页
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const currentUserId = ref<number>()
const currentUserData = ref<any>()

// 获取角色名称
const getRoleName = (roleCode: string) => {
  const roleMap: Record<string, string> = {
    'SUPER_ADMIN': '超级管理员',
    'SITE_ADMIN': '站点管理员',
    'EDITOR': '编辑者',
    'VIEWER': '查看者'
  }
  return roleMap[roleCode] || roleCode
}

// 获取角色标签类型
const getRoleTagType = (roleCode: string) => {
  const typeMap: Record<string, any> = {
    'SUPER_ADMIN': 'danger',
    'SITE_ADMIN': 'warning',
    'EDITOR': 'success',
    'VIEWER': 'info'
  }
  return typeMap[roleCode] || ''
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  console.log('🔍 开始加载用户列表，参数:', {
    keyword: searchText.value,
    status: selectedStatus.value,
    page: currentPage.value,
    size: pageSize.value
  })

  try {
    const response = await getUserList({
      keyword: searchText.value,
      status: selectedStatus.value,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: 'createdAt',
      sortOrder: 'DESC'
    })

    console.log('✅ 用户列表响应:', response)

    if (response.code === 200 && response.data) {
      userList.value = Array.isArray(response.data.content) ? response.data.content : []
      total.value = response.data.totalElements || 0
      console.log('✅ 用户列表加载成功:', {
        total: total.value,
        count: userList.value.length
      })
    } else {
      console.error('❌ 响应code不是200:', response)
      userList.value = []
      total.value = 0
      ElMessage.error(response.message || '加载用户列表失败')
    }
  } catch (error: any) {
    console.error('❌ 加载用户列表失败:', error)
    userList.value = []
    total.value = 0
    ElMessage.error(error.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 0  // 重置到第一页（从0开始）
  loadUsers()
}

// 新增用户
const handleAdd = () => {
  currentUserId.value = undefined
  currentUserData.value = undefined
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = async (row: User) => {
  try {
    const response = await getUserById(row.id)
    if (response.code === 200 && response.data) {
      currentUserId.value = row.id
      currentUserData.value = response.data
      dialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取用户信息失败')
    }
  } catch (error: any) {
    console.error('获取用户信息失败:', error)
    ElMessage.error(error.message || '获取用户信息失败')
  }
}

// 对话框成功回调
const handleDialogSuccess = () => {
  loadUsers()
}

// 删除用户
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${row.username}" 吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await deleteUser(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadUsers()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadUsers()
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val - 1  // Element Plus从1开始，转换为从0开始
  loadUsers()
}

// 组件挂载时加载数据
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users {
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 用户表格样式 */
.user-table {
  border-radius: 8px;
  overflow: hidden;
}

.user-table :deep(.el-table__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-table :deep(.el-table__header th) {
  background: transparent;
  color: white;
  font-weight: 600;
}

.user-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.user-table :deep(.el-table__row:hover) {
  transform: scale(1.01);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 用户信息单元格 */
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-username {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 邮箱单元格 */
.email-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.email-cell .el-icon {
  color: #409eff;
  flex-shrink: 0;
}

/* 手机号单元格 */
.mobile-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.mobile-cell .el-icon {
  color: #67c23a;
  flex-shrink: 0;
}

/* 角色单元格 */
.roles-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.role-tag {
  font-weight: 500;
  border-radius: 4px;
}
</style>


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
            <el-button type="primary" @click="handleAdd" style="margin-left: 10px">
              <el-icon><Plus /></el-icon>
              新增用户
            </el-button>
          </div>
        </div>
      </template>

      <!-- 用户表格 -->
      <el-table :data="userList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="mobile" label="手机号" width="130" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'ACTIVE'" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 'LOCKED'" type="danger">锁定</el-tag>
            <el-tag v-else-if="row.status === 'INACTIVE'" type="info">停用</el-tag>
            <el-tag v-else type="warning">待激活</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role" size="small" style="margin-right: 5px">
              {{ getRoleName(role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
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
import { Search, Plus } from '@element-plus/icons-vue'
import { getUserList, deleteUser, getUserById, type User } from '@/api/user'
import UserDialog from '@/components/UserDialog.vue'

// 响应式数据
const loading = ref(false)
const searchText = ref('')
const selectedStatus = ref('')
const userList = ref<User[]>([])
const currentPage = ref(1)
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

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const response = await getUserList({
      keyword: searchText.value,
      status: selectedStatus.value,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: 'createdAt',
      sortOrder: 'DESC'
    })

    if (response.code === 200 && response.data) {
      userList.value = response.data.content
      total.value = response.data.totalElements
    } else {
      ElMessage.error(response.message || '加载用户列表失败')
    }
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
    ElMessage.error(error.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
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
  currentPage.value = val
  loadUsers()
}

// 组件挂载时加载数据
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
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
</style>


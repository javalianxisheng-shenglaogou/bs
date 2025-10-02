<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div>
            <el-input
              v-model="searchText"
              placeholder="搜索用户名或邮箱"
              style="width: 200px; margin-right: 10px"
              clearable
              @clear="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd">
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

// 用户数据类型
interface User {
  id: number
  username: string
  nickname: string
  email: string
  mobile: string
  status: string
  roles: string[]
  createdAt: string
}

// 响应式数据
const loading = ref(false)
const searchText = ref('')
const userList = ref<User[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
    // TODO: 调用真实API
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))

    userList.value = [
      {
        id: 1,
        username: 'admin',
        nickname: '超级管理员',
        email: 'admin@cms.com',
        mobile: '13800000001',
        status: 'ACTIVE',
        roles: ['SUPER_ADMIN'],
        createdAt: '2025-01-01 10:00:00'
      },
      {
        id: 2,
        username: 'siteadmin',
        nickname: '站点管理员',
        email: 'siteadmin@cms.com',
        mobile: '13800000002',
        status: 'ACTIVE',
        roles: ['SITE_ADMIN'],
        createdAt: '2025-01-02 10:00:00'
      },
      {
        id: 3,
        username: 'editor1',
        nickname: '编辑者1',
        email: 'editor1@cms.com',
        mobile: '13800000003',
        status: 'ACTIVE',
        roles: ['EDITOR'],
        createdAt: '2025-01-03 10:00:00'
      }
    ]
    total.value = 3
  } catch (error) {
    ElMessage.error('加载用户列表失败')
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
  ElMessage.info('新增用户功能开发中...')
}

// 编辑用户
const handleEdit = (row: User) => {
  ElMessage.info(`编辑用户: ${row.username}`)
}

// 删除用户
const handleDelete = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadUsers()
  }).catch(() => {
    // 取消删除
  })
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


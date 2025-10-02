<template>
  <div class="contents">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>内容列表</span>
          <div>
            <el-select v-model="selectedSite" placeholder="选择站点" style="width: 150px; margin-right: 10px">
              <el-option label="全部站点" value="" />
              <el-option label="官方网站" value="1" />
              <el-option label="博客站点" value="2" />
            </el-select>
            <el-select v-model="selectedStatus" placeholder="状态" style="width: 120px; margin-right: 10px">
              <el-option label="全部状态" value="" />
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已发布" value="PUBLISHED" />
            </el-select>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增内容
            </el-button>
          </div>
        </div>
      </template>

      <!-- 内容表格 -->
      <el-table :data="contentList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="siteName" label="所属站点" width="120" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PUBLISHED'" type="success">已发布</el-tag>
            <el-tag v-else-if="row.status === 'DRAFT'" type="info">草稿</el-tag>
            <el-tag v-else type="warning">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handlePublish(row)" v-if="row.status === 'DRAFT'">
              发布
            </el-button>
            <el-button type="warning" size="small" @click="handleUnpublish(row)" v-else>
              下线
            </el-button>
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
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 内容数据类型
interface Content {
  id: number
  title: string
  siteName: string
  categoryName: string
  status: string
  author: string
  viewCount: number
  createdAt: string
}

// 响应式数据
const loading = ref(false)
const selectedSite = ref('')
const selectedStatus = ref('')
const contentList = ref<Content[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载内容列表
const loadContents = async () => {
  loading.value = true
  try {
    // TODO: 调用真实API
    // 模拟数据
    await new Promise(resolve => setTimeout(resolve, 500))

    contentList.value = [
      {
        id: 1,
        title: '欢迎使用多站点CMS系统',
        siteName: '官方网站',
        categoryName: '公告',
        status: 'PUBLISHED',
        author: '管理员',
        viewCount: 1234,
        createdAt: '2025-01-01 10:00:00'
      },
      {
        id: 2,
        title: 'Spring Boot最佳实践',
        siteName: '博客站点',
        categoryName: '技术',
        status: 'PUBLISHED',
        author: '编辑者1',
        viewCount: 567,
        createdAt: '2025-01-02 10:00:00'
      },
      {
        id: 3,
        title: 'Vue 3新特性介绍',
        siteName: '博客站点',
        categoryName: '技术',
        status: 'DRAFT',
        author: '编辑者1',
        viewCount: 0,
        createdAt: '2025-01-03 10:00:00'
      }
    ]
    total.value = 3
  } catch (error) {
    ElMessage.error('加载内容列表失败')
  } finally {
    loading.value = false
  }
}

// 监听筛选条件变化
watch([selectedSite, selectedStatus], () => {
  currentPage.value = 1
  loadContents()
})

// 新增内容
const handleAdd = () => {
  ElMessage.info('新增内容功能开发中...')
}

// 编辑内容
const handleEdit = (row: Content) => {
  ElMessage.info(`编辑内容: ${row.title}`)
}

// 发布内容
const handlePublish = (row: Content) => {
  ElMessageBox.confirm(
    `确定要发布内容 "${row.title}" 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(() => {
    ElMessage.success('发布成功')
    loadContents()
  }).catch(() => {
    // 取消发布
  })
}

// 下线内容
const handleUnpublish = (row: Content) => {
  ElMessageBox.confirm(
    `确定要下线内容 "${row.title}" 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    ElMessage.success('下线成功')
    loadContents()
  }).catch(() => {
    // 取消下线
  })
}

// 删除内容
const handleDelete = (row: Content) => {
  ElMessageBox.confirm(
    `确定要删除内容 "${row.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error',
    }
  ).then(() => {
    ElMessage.success('删除成功')
    loadContents()
  }).catch(() => {
    // 取消删除
  })
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadContents()
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadContents()
}

// 组件挂载时加载数据
onMounted(() => {
  loadContents()
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


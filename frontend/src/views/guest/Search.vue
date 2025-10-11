<template>
  <div class="guest-search">
    <div class="container">
      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
          v-model="keyword"
          size="large"
          placeholder="请输入关键词搜索内容..."
          @keyup.enter="handleSearch"
          clearable
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" type="primary">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 搜索信息 -->
      <div v-if="searched" class="search-info">
        <span>找到 <strong>{{ total }}</strong> 条结果</span>
        <el-tag v-if="keyword" closable @close="clearKeyword" type="info">
          关键词: {{ keyword }}
        </el-tag>
      </div>

      <!-- 搜索结果 -->
      <el-skeleton :loading="loading" animated :count="3">
        <template #default>
          <div v-if="searchResults.length" class="search-results">
            <ContentListItem
              v-for="item in searchResults"
              :key="item.id"
              :content="item"
              :highlight-keyword="keyword"
              @click="viewDetail(item.id)"
            />
            
            <!-- 分页 -->
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next, jumper"
              @current-change="handleSearch"
              class="pagination"
            />
          </div>

          <!-- 无结果 -->
          <el-empty v-else-if="searched && !loading" description="未找到相关内容">
            <el-button type="primary" @click="clearKeyword">重新搜索</el-button>
          </el-empty>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import ContentListItem from '@/components/guest/ContentListItem.vue'
import { searchContents, type PublicContentDTO } from '@/api/guest'

const router = useRouter()
const route = useRoute()
const instance = getCurrentInstance()

// 状态
const loading = ref(false)
const keyword = ref('')
const searchResults = ref<PublicContentDTO[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searched = ref(false)

// 获取当前站点ID
const getCurrentSiteId = (): number => {
  // 从父组件获取
  const parent = instance?.parent
  if (parent && parent.exposed && parent.exposed.currentSiteId) {
    return parent.exposed.currentSiteId.value
  }
  // 从路由参数获取
  const siteIdFromQuery = route.query.siteId
  if (siteIdFromQuery) {
    return Number(siteIdFromQuery)
  }
  return 1 // 默认站点
}

// 执行搜索
const handleSearch = async () => {
  const trimmedKeyword = keyword.value.trim()
  
  if (!trimmedKeyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  searched.value = true

  try {
    const siteId = getCurrentSiteId()
    const res = await searchContents({
      siteId,
      keyword: trimmedKeyword,
      page: currentPage.value - 1,
      size: pageSize.value
    })

    searchResults.value = res.data.content || []
    total.value = res.data.totalElements || 0

    // 更新URL
    router.replace({
      query: {
        ...route.query,
        keyword: trimmedKeyword,
        page: currentPage.value
      }
    })
  } catch (error: any) {
    ElMessage.error(error.message || '搜索失败')
  } finally {
    loading.value = false
  }
}

// 清除关键词
const clearKeyword = () => {
  keyword.value = ''
  searchResults.value = []
  total.value = 0
  searched.value = false
  currentPage.value = 1
  router.replace({ query: {} })
}

// 查看详情
const viewDetail = (id: number) => {
  router.push(`/guest/content/${id}`)
}

// 初始化
onMounted(() => {
  // 从URL获取关键词
  const keywordFromQuery = route.query.keyword as string
  if (keywordFromQuery) {
    keyword.value = keywordFromQuery
    const pageFromQuery = route.query.page as string
    if (pageFromQuery) {
      currentPage.value = Number(pageFromQuery)
    }
    handleSearch()
  }
})
</script>

<style scoped lang="scss">
.guest-search {
  .search-box {
    margin-bottom: 24px;
    background: #fff;
    padding: 32px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    :deep(.el-input-group__append) {
      padding: 0;
      
      .el-button {
        margin: 0;
        border-radius: 0 4px 4px 0;
      }
    }
  }

  .search-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding: 12px 20px;
    background: #f5f7fa;
    border-radius: 4px;

    strong {
      color: #409eff;
    }
  }

  .search-results {
    background: #fff;
    border-radius: 4px;
    overflow: hidden;

    .pagination {
      margin: 20px 0;
      display: flex;
      justify-content: center;
    }
  }

  :deep(.el-empty) {
    padding: 60px 0;
  }
}
</style>


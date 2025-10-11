<template>
  <div class="guest-home">
    <div class="container">
      <el-row :gutter="20">
        <!-- 左侧分类 -->
        <el-col :xs="24" :sm="24" :md="6" :lg="5">
          <el-card class="category-card">
            <template #header>
              <div class="card-header">
                <el-icon><Folder /></el-icon>
                <span>内容分类</span>
              </div>
            </template>
            <el-tree
              :data="categories"
              :props="{ label: 'name', children: 'children' }"
              node-key="id"
              :default-expand-all="false"
              @node-click="handleCategoryClick"
            >
              <template #default="{ data }">
                <span class="category-node">
                  <span>{{ data.name }}</span>
                  <el-tag size="small" type="info">{{ data.contentCount }}</el-tag>
                </span>
              </template>
            </el-tree>
          </el-card>
        </el-col>

        <!-- 右侧内容 -->
        <el-col :xs="24" :sm="24" :md="18" :lg="19">
          <el-skeleton :loading="loading" animated :count="3">
            <template #default>
              <!-- 置顶内容 -->
              <section v-if="topContents.length" class="content-section">
                <h3 class="section-title">
                  <el-icon><Top /></el-icon>
                  置顶推荐
                </h3>
                <el-row :gutter="16">
                  <el-col
                    v-for="item in topContents"
                    :key="item.id"
                    :xs="24"
                    :sm="12"
                    :md="8"
                    :lg="8"
                  >
                    <ContentCard :content="item" @click="viewDetail(item.id)" />
                  </el-col>
                </el-row>
              </section>

              <!-- 推荐内容 -->
              <section v-if="featuredContents.length" class="content-section">
                <h3 class="section-title">
                  <el-icon><Star /></el-icon>
                  精选内容
                </h3>
                <el-row :gutter="16">
                  <el-col
                    v-for="item in featuredContents"
                    :key="item.id"
                    :xs="24"
                    :sm="12"
                    :md="6"
                    :lg="6"
                  >
                    <ContentCard :content="item" @click="viewDetail(item.id)" />
                  </el-col>
                </el-row>
              </section>

              <!-- 最新内容 -->
              <section class="content-section">
                <h3 class="section-title">
                  <el-icon><Clock /></el-icon>
                  {{ selectedCategoryName ? `${selectedCategoryName} - 最新发布` : '最新发布' }}
                </h3>
                
                <el-empty v-if="!latestContents.length && !loading" description="暂无内容" />
                
                <div v-else class="latest-contents">
                  <ContentListItem
                    v-for="item in latestContents"
                    :key="item.id"
                    :content="item"
                    @click="viewDetail(item.id)"
                  />
                </div>

                <!-- 分页 -->
                <el-pagination
                  v-if="total > 0"
                  v-model:current-page="currentPage"
                  :page-size="pageSize"
                  :total="total"
                  layout="prev, pager, next, total"
                  @current-change="loadLatestContents"
                  class="pagination"
                />
              </section>
            </template>
          </el-skeleton>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Folder, Top, Star, Clock } from '@element-plus/icons-vue'
import ContentCard from '@/components/guest/ContentCard.vue'
import ContentListItem from '@/components/guest/ContentListItem.vue'
import {
  getHomePageData,
  getPublishedContents,
  type PublicContentDTO,
  type CategoryTreeDTO
} from '@/api/guest'

const router = useRouter()
const route = useRoute()
const instance = getCurrentInstance()

// 状态
const loading = ref(false)
const categories = ref<CategoryTreeDTO[]>([])
const topContents = ref<PublicContentDTO[]>([])
const featuredContents = ref<PublicContentDTO[]>([])
const latestContents = ref<PublicContentDTO[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedCategoryId = ref<number | undefined>(undefined)
const selectedCategoryName = ref<string>('')

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

// 加载首页数据
const loadHomeData = async () => {
  loading.value = true
  try {
    const siteId = getCurrentSiteId()
    const res = await getHomePageData(siteId)
    
    categories.value = res.data.categories || []
    topContents.value = res.data.topContents || []
    featuredContents.value = res.data.featuredContents || []
    
    // 加载最新内容
    await loadLatestContents()
  } catch (error: any) {
    ElMessage.error(error.message || '加载首页数据失败')
  } finally {
    loading.value = false
  }
}

// 加载最新内容
const loadLatestContents = async () => {
  try {
    const siteId = getCurrentSiteId()
    const res = await getPublishedContents({
      siteId,
      categoryId: selectedCategoryId.value,
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    latestContents.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载内容列表失败')
  }
}

// 分类点击
const handleCategoryClick = (category: CategoryTreeDTO) => {
  selectedCategoryId.value = category.id
  selectedCategoryName.value = category.name
  currentPage.value = 1
  loadLatestContents()
}

// 查看详情
const viewDetail = (id: number) => {
  router.push(`/guest/content/${id}`)
}

// 初始化
onMounted(() => {
  loadHomeData()
})
</script>

<style scoped lang="scss">
.guest-home {
  .category-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 600;
    }

    :deep(.el-tree) {
      .category-node {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding-right: 8px;
      }

      .el-tree-node__content {
        height: 36px;
      }
    }
  }

  .content-section {
    margin-bottom: 32px;

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 20px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 20px 0;
      padding-bottom: 12px;
      border-bottom: 2px solid #409eff;
    }

    .latest-contents {
      background: #fff;
      border-radius: 4px;
      overflow: hidden;
    }

    .pagination {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}
</style>


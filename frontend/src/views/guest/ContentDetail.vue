<template>
  <div class="guest-content-detail">
    <div class="container">
      <el-skeleton :loading="loading" animated>
        <template #default>
          <!-- 面包屑 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/guest' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="content.categoryName">
              {{ content.categoryName }}
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ content.title }}</el-breadcrumb-item>
          </el-breadcrumb>

          <!-- 文章内容 -->
          <article class="content-article">
            <h1 class="article-title">{{ content.title }}</h1>

            <div class="article-meta">
              <el-space :size="20">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ content.authorName || '未知作者' }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDateTime(content.publishedAt) }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ content.viewCount }} 次浏览
                </span>
                <el-tag v-if="content.isOriginal" type="success" size="small">原创</el-tag>
                <el-tag v-if="content.isTop" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="content.isFeatured" type="warning" size="small">推荐</el-tag>
              </el-space>
            </div>

            <!-- 封面图 -->
            <el-image
              v-if="content.coverImage"
              :src="content.coverImage"
              fit="cover"
              class="cover-image"
              lazy
            >
              <template #error>
                <div class="image-error">
                  <el-icon :size="60"><Picture /></el-icon>
                </div>
              </template>
            </el-image>

            <!-- 正文内容 -->
            <div class="article-body" v-html="content.content"></div>
          </article>

          <!-- 相关推荐 -->
          <section v-if="relatedContents.length" class="related-section">
            <h3 class="section-title">
              <el-icon><Link /></el-icon>
              相关推荐
            </h3>
            <el-row :gutter="16">
              <el-col
                v-for="item in relatedContents"
                :key="item.id"
                :xs="24"
                :sm="12"
                :md="6"
                :lg="6"
              >
                <ContentCard :content="item" @click="viewRelated(item.id)" />
              </el-col>
            </el-row>
          </section>

          <!-- 操作按钮 -->
          <div class="actions">
            <el-button @click="goBack">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <el-button type="primary" @click="goHome">
              <el-icon><HomeFilled /></el-icon>
              回到首页
            </el-button>
          </div>
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  Clock,
  View,
  Picture,
  Link,
  ArrowLeft,
  HomeFilled
} from '@element-plus/icons-vue'
import ContentCard from '@/components/guest/ContentCard.vue'
import {
  getContentDetail,
  getRelatedContents,
  type PublicContentDetailDTO,
  type PublicContentDTO
} from '@/api/guest'

const router = useRouter()
const route = useRoute()

// 状态
const loading = ref(false)
const content = ref<PublicContentDetailDTO>({
  id: 0,
  siteId: 0,
  title: '',
  content: '',
  viewCount: 0,
  publishedAt: '',
  isTop: false,
  isFeatured: false,
  isOriginal: false
})
const relatedContents = ref<PublicContentDTO[]>([])

// 加载内容详情
const loadContentDetail = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    
    // 获取内容详情
    const res = await getContentDetail(id)
    content.value = res.data

    // 获取相关内容
    const relatedRes = await getRelatedContents(id, 4)
    relatedContents.value = relatedRes.data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载内容失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看相关内容
const viewRelated = (id: number) => {
  router.push(`/guest/content/${id}`)
  // 重新加载数据
  loadContentDetail()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 返回
const goBack = () => {
  router.back()
}

// 回到首页
const goHome = () => {
  router.push('/guest')
}

// 初始化
onMounted(() => {
  loadContentDetail()
})
</script>

<style scoped lang="scss">
.guest-content-detail {
  .breadcrumb {
    margin-bottom: 20px;
  }

  .content-article {
    background: #fff;
    padding: 32px;
    border-radius: 8px;
    margin-bottom: 24px;

    .article-title {
      font-size: 32px;
      font-weight: 700;
      color: #303133;
      margin: 0 0 20px 0;
      line-height: 1.4;
    }

    .article-meta {
      padding-bottom: 20px;
      margin-bottom: 24px;
      border-bottom: 1px solid #e4e7ed;

      .meta-item {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        color: #606266;
        font-size: 14px;
      }
    }

    .cover-image {
      width: 100%;
      max-height: 500px;
      border-radius: 8px;
      margin-bottom: 24px;

      .image-error {
        width: 100%;
        height: 300px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: #c0c4cc;
        border-radius: 8px;
      }
    }

    .article-body {
      font-size: 16px;
      line-height: 1.8;
      color: #303133;

      :deep(p) {
        margin: 1em 0;
      }

      :deep(img) {
        max-width: 100%;
        height: auto;
        border-radius: 4px;
        margin: 16px 0;
      }

      :deep(h1),
      :deep(h2),
      :deep(h3),
      :deep(h4),
      :deep(h5),
      :deep(h6) {
        margin: 1.5em 0 0.8em 0;
        font-weight: 600;
      }

      :deep(ul),
      :deep(ol) {
        margin: 1em 0;
        padding-left: 2em;
      }

      :deep(blockquote) {
        margin: 1em 0;
        padding: 12px 20px;
        background: #f5f7fa;
        border-left: 4px solid #409eff;
        color: #606266;
      }

      :deep(code) {
        padding: 2px 6px;
        background: #f5f7fa;
        border-radius: 3px;
        font-family: 'Courier New', monospace;
      }

      :deep(pre) {
        margin: 1em 0;
        padding: 16px;
        background: #282c34;
        color: #abb2bf;
        border-radius: 4px;
        overflow-x: auto;

        code {
          background: none;
          color: inherit;
          padding: 0;
        }
      }
    }
  }

  .related-section {
    background: #fff;
    padding: 32px;
    border-radius: 8px;
    margin-bottom: 24px;

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
  }

  .actions {
    display: flex;
    gap: 12px;
    justify-content: center;
    padding: 24px 0;
  }
}
</style>


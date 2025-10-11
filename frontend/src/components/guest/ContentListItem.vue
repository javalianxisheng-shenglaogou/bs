<template>
  <div class="content-list-item" @click="$emit('click')">
    <div class="item-main">
      <div class="item-header">
        <h3 class="item-title">
          <el-tag v-if="content.isTop" type="danger" size="small" style="margin-right: 8px">置顶</el-tag>
          <el-tag v-if="content.isFeatured" type="warning" size="small" style="margin-right: 8px">推荐</el-tag>
          <span v-html="highlightKeyword(content.title)"></span>
        </h3>
        <span class="item-category" v-if="content.categoryName">
          <el-icon><Folder /></el-icon>
          {{ content.categoryName }}
        </span>
      </div>
      
      <p v-if="content.summary" class="item-summary" v-html="highlightKeyword(content.summary)"></p>
      
      <div class="item-meta">
        <span class="meta-item">
          <el-icon><User /></el-icon>
          {{ content.authorName || '未知作者' }}
        </span>
        <span class="meta-item">
          <el-icon><Clock /></el-icon>
          {{ formatDate(content.publishedAt) }}
        </span>
        <span class="meta-item">
          <el-icon><View /></el-icon>
          {{ content.viewCount || 0 }} 次浏览
        </span>
      </div>
    </div>
    
    <div v-if="content.coverImage" class="item-cover">
      <el-image
        :src="content.coverImage"
        fit="cover"
        class="cover-image"
        lazy
      >
        <template #error>
          <div class="image-error">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-image>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Folder, User, Clock, View, Picture } from '@element-plus/icons-vue'
import type { PublicContentDTO } from '@/api/guest'

interface Props {
  content: PublicContentDTO
  highlightKeyword?: string
}

const props = withDefaults(defineProps<Props>(), {
  highlightKeyword: ''
})

defineEmits<{
  (e: 'click'): void
}>()

// 关键词高亮
const highlightKeyword = (text: string): string => {
  if (!text || !props.highlightKeyword) return text
  const keyword = props.highlightKeyword.trim()
  if (!keyword) return text
  
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<mark>$1</mark>')
}

// 格式化日期
const formatDate = (dateString: string): string => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
</script>

<style scoped lang="scss">
.content-list-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #f5f7fa;
  }

  .item-main {
    flex: 1;
    min-width: 0;

    .item-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;

      .item-title {
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin: 0;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        :deep(mark) {
          background-color: #fef0f0;
          color: #f56c6c;
          padding: 2px 4px;
          border-radius: 2px;
        }
      }

      .item-category {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 14px;
        color: #909399;
        white-space: nowrap;
      }
    }

    .item-summary {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
      margin: 0 0 12px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;

      :deep(mark) {
        background-color: #fef0f0;
        color: #f56c6c;
        padding: 2px 4px;
        border-radius: 2px;
      }
    }

    .item-meta {
      display: flex;
      gap: 24px;
      font-size: 13px;
      color: #909399;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .item-cover {
    width: 140px;
    height: 105px;
    flex-shrink: 0;

    .cover-image {
      width: 100%;
      height: 100%;
      border-radius: 4px;
    }

    .image-error {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f5f7fa;
      color: #c0c4cc;
      border-radius: 4px;
    }
  }
}
</style>


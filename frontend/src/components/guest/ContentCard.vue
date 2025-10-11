<template>
  <el-card class="content-card" :body-style="{ padding: '0px' }" shadow="hover" @click="$emit('click')">
    <!-- 封面图 -->
    <div class="card-image">
      <el-image
        v-if="content.coverImage"
        :src="content.coverImage"
        fit="cover"
        class="image"
        lazy
      >
        <template #error>
          <div class="image-placeholder">
            <el-icon :size="40"><Picture /></el-icon>
          </div>
        </template>
      </el-image>
      <div v-else class="image-placeholder">
        <el-icon :size="40"><Document /></el-icon>
      </div>
      
      <!-- 标签 -->
      <div class="card-tags">
        <el-tag v-if="content.isTop" type="danger" size="small">置顶</el-tag>
        <el-tag v-if="content.isFeatured" type="warning" size="small">推荐</el-tag>
      </div>
    </div>

    <!-- 内容信息 -->
    <div class="card-content">
      <h4 class="card-title" :title="content.title">{{ content.title }}</h4>
      <p v-if="content.summary" class="card-summary">{{ truncateSummary(content.summary) }}</p>
      
      <div class="card-meta">
        <span class="meta-item">
          <el-icon><User /></el-icon>
          {{ content.authorName || '未知作者' }}
        </span>
        <span class="meta-item">
          <el-icon><View /></el-icon>
          {{ content.viewCount || 0 }}
        </span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { Picture, Document, User, View } from '@element-plus/icons-vue'
import type { PublicContentDTO } from '@/api/guest'

interface Props {
  content: PublicContentDTO
}

const props = defineProps<Props>()

defineEmits<{
  (e: 'click'): void
}>()

// 截断摘要
const truncateSummary = (text: string, maxLength: number = 60): string => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<style scoped lang="scss">
.content-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
  }

  .card-image {
    position: relative;
    width: 100%;
    height: 180px;
    overflow: hidden;
    background: #f5f7fa;

    .image {
      width: 100%;
      height: 100%;
    }

    .image-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #c0c4cc;
    }

    .card-tags {
      position: absolute;
      top: 8px;
      right: 8px;
      display: flex;
      gap: 4px;
    }
  }

  .card-content {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .card-summary {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
      margin: 0 0 12px 0;
      flex: 1;
    }

    .card-meta {
      display: flex;
      gap: 16px;
      font-size: 12px;
      color: #909399;
      margin-top: auto;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>


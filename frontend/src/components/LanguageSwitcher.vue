<template>
  <el-dropdown @command="handleLanguageChange" trigger="click">
    <span class="language-switcher">
      <el-icon><Setting /></el-icon>
      <span class="language-text">{{ currentLanguageName }}</span>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in supportedLocales"
          :key="lang.code"
          :command="lang.code"
          :disabled="locale === lang.code"
        >
          <span :class="{ 'is-active': locale === lang.code }">
            {{ lang.name }}
          </span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Setting } from '@element-plus/icons-vue'
import { setLocale, supportedLocales } from '@/locales'
import { ElMessage } from 'element-plus'

const { locale } = useI18n()

// 当前语言名称
const currentLanguageName = computed(() => {
  const current = supportedLocales.find(lang => lang.code === locale.value)
  return current?.shortName || '中文'
})

// 切换语言
const handleLanguageChange = (command: string) => {
  if (command === locale.value) {
    return
  }
  
  try {
    setLocale(command)
    const langName = supportedLocales.find(lang => lang.code === command)?.name
    ElMessage.success(`语言已切换为 ${langName}`)
  } catch (error) {
    console.error('切换语言失败:', error)
    ElMessage.error('切换语言失败')
  }
}
</script>

<style scoped>
.language-switcher {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: var(--el-text-color-regular);
}

.language-switcher:hover {
  background-color: var(--el-fill-color-light);
  color: var(--el-color-primary);
}

.language-text {
  font-size: 14px;
  user-select: none;
}

.is-active {
  color: var(--el-color-primary);
  font-weight: 600;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  cursor: not-allowed;
  opacity: 0.6;
}
</style>


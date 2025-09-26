<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth'
import { useAppStore } from './stores/app'
import Layout from './components/Layout.vue'

const route = useRoute()
const authStore = useAuthStore()
const appStore = useAppStore()

onMounted(async () => {
  // 初始化应用状态
  appStore.initializeApp()

  // 初始化认证状态
  await authStore.initializeAuth()
})
</script>

<template>
  <div id="app">
    <Layout v-if="authStore.isAuthenticated && route.meta.requiresAuth !== false" />
    <router-view v-else />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f5f5;
}

#app {
  height: 100vh;
  overflow: hidden;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

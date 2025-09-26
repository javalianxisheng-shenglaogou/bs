<template>
  <div class="site-detail">
    <h1>站点详情</h1>
    <p>站点ID: {{ $route.params.id }}</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '../stores/app'

const appStore = useAppStore()

onMounted(() => {
  appStore.setBreadcrumbs([
    { title: '站点管理', path: '/sites' },
    { title: '站点详情' }
  ])
})
</script>

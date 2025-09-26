<template>
  <div class="site-selector">
    <el-select
      v-model="selectedSiteId"
      placeholder="请选择站点"
      @change="handleSiteChange"
      style="width: 200px"
    >
      <el-option
        v-for="site in sites"
        :key="site.id"
        :label="site.name"
        :value="site.id"
      >
        <span>{{ site.name }}</span>
        <span style="float: right; color: #8492a6; font-size: 13px">{{ site.code }}</span>
      </el-option>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAppStore } from '../stores/app'
import { sitesApi, type Site } from '../api'

const appStore = useAppStore()

const sites = ref<Site[]>([])
const selectedSiteId = computed({
  get: () => appStore.currentSite?.id || null,
  set: (value) => {
    if (value) {
      const site = sites.value.find(s => s.id === value)
      if (site) {
        appStore.setCurrentSite(site)
      }
    }
  }
})

const handleSiteChange = (siteId: number) => {
  const site = sites.value.find(s => s.id === siteId)
  if (site) {
    appStore.setCurrentSite(site)
    // 触发自定义事件，通知父组件站点已更改
    emit('site-changed', site)
  }
}

const fetchSites = async () => {
  try {
    const response = await sitesApi.getSites({ page: 1, size: 100 })
    sites.value = response.data.content
    
    // 如果没有当前站点但有站点列表，选择第一个站点
    if (!appStore.currentSite && sites.value.length > 0) {
      appStore.setCurrentSite(sites.value[0])
    }
  } catch (error) {
    console.error('Failed to fetch sites:', error)
  }
}

const emit = defineEmits<{
  'site-changed': [site: Site]
}>()

onMounted(() => {
  fetchSites()
})
</script>

<style scoped>
.site-selector {
  display: inline-block;
}
</style>

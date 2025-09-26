import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Site } from '../api'

export const useAppStore = defineStore('app', () => {
  // 状态
  const sidebarCollapsed = ref(false)
  const currentSite = ref<Site | null>(null)
  const breadcrumbs = ref<Array<{ title: string; path?: string }>>([])
  const loading = ref(false)

  // 方法
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  const setSidebarCollapsed = (collapsed: boolean) => {
    sidebarCollapsed.value = collapsed
  }

  const setCurrentSite = (site: Site | null) => {
    currentSite.value = site
    if (site) {
      localStorage.setItem('currentSite', JSON.stringify(site))
    } else {
      localStorage.removeItem('currentSite')
    }
  }

  const setBreadcrumbs = (items: Array<{ title: string; path?: string }>) => {
    breadcrumbs.value = items
  }

  const setLoading = (isLoading: boolean) => {
    loading.value = isLoading
  }

  // 初始化应用状态
  const initializeApp = () => {
    const storedSite = localStorage.getItem('currentSite')
    if (storedSite) {
      try {
        currentSite.value = JSON.parse(storedSite)
      } catch (error) {
        console.error('Parse stored site error:', error)
        localStorage.removeItem('currentSite')
      }
    }
  }

  return {
    // 状态
    sidebarCollapsed,
    currentSite,
    breadcrumbs,
    loading,
    
    // 方法
    toggleSidebar,
    setSidebarCollapsed,
    setCurrentSite,
    setBreadcrumbs,
    setLoading,
    initializeApp
  }
})

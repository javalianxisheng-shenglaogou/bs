/**
 * Vue I18n 配置
 */
import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'

// 从localStorage获取用户选择的语言，默认中文
const savedLocale = localStorage.getItem('locale') || 'zh-CN'

// 创建i18n实例
const i18n = createI18n({
  legacy: false,              // 使用Composition API模式
  locale: savedLocale,        // 默认语言
  fallbackLocale: 'zh-CN',    // 回退语言
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  },
  globalInjection: true       // 全局注入$t方法
})

export default i18n

// 导出切换语言的辅助函数
export function setLocale(locale: string) {
  i18n.global.locale.value = locale as any
  localStorage.setItem('locale', locale)
  
  // 如果需要，可以在这里同步到服务器
  // 例如：updateUserPreference({ language: locale })
}

// 导出获取当前语言的辅助函数
export function getLocale() {
  return i18n.global.locale.value
}

// 导出支持的语言列表
export const supportedLocales = [
  { code: 'zh-CN', name: '简体中文', shortName: '中文' },
  { code: 'en-US', name: 'English', shortName: 'EN' }
]


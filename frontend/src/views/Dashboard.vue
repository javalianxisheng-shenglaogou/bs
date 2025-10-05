<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userStore.userInfo?.nickname || '用户' }}！</h1>
          <p>{{ getCurrentGreeting() }} 今天是 {{ getCurrentDate() }}</p>
        </div>
        <div class="welcome-icon">
          <el-icon :size="80" color="#667eea">
            <Sunny v-if="getTimeOfDay() === 'morning'" />
            <Sunset v-else-if="getTimeOfDay() === 'afternoon'" />
            <Moon v-else />
          </el-icon>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="(stat, index) in stats" :key="index">
        <el-card shadow="hover" class="stat-card" :class="`stat-card-${index}`">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="32">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-trend" :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
                <el-icon>
                  <CaretTop v-if="stat.trend > 0" />
                  <CaretBottom v-else />
                </el-icon>
                {{ Math.abs(stat.trend) }}%
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 内容区域 -->
    <el-row :gutter="20" class="content-row">
      <!-- 快速操作 -->
      <el-col :span="16">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <el-icon><Operation /></el-icon>
              <span>快速操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <div class="action-item" @click="createContent">
              <div class="action-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <el-icon :size="24"><DocumentAdd /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">创建内容</div>
                <div class="action-desc">发布新的文章或页面</div>
              </div>
            </div>
            <div class="action-item" @click="createSite">
              <div class="action-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
                <el-icon :size="24"><Plus /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">创建站点</div>
                <div class="action-desc">添加新的站点配置</div>
              </div>
            </div>
            <div class="action-item" @click="viewLogs">
              <div class="action-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
                <el-icon :size="24"><Document /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">查看日志</div>
                <div class="action-desc">系统操作记录</div>
              </div>
            </div>
            <div class="action-item" @click="manageUsers">
              <div class="action-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
                <el-icon :size="24"><User /></el-icon>
              </div>
              <div class="action-text">
                <div class="action-title">用户管理</div>
                <div class="action-desc">管理系统用户</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 最近活动 -->
        <el-card class="recent-activity-card">
          <template #header>
            <div class="card-header">
              <el-icon><Clock /></el-icon>
              <span>最近活动</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in recentActivities"
              :key="index"
              :timestamp="activity.time"
              :type="activity.type"
              placement="top"
            >
              <el-card>
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <!-- 右侧信息 -->
      <el-col :span="8">
        <!-- 待办事项 -->
        <el-card class="todo-card">
          <template #header>
            <div class="card-header">
              <el-icon><List /></el-icon>
              <span>待办事项</span>
            </div>
          </template>
          <div class="todo-list">
            <div class="todo-item" v-for="(todo, index) in todos" :key="index">
              <el-checkbox v-model="todo.done">{{ todo.text }}</el-checkbox>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, markRaw } from 'vue'
import {
  User, OfficeBuilding, Document, View,
  Sunny, Sunset, Moon, CaretTop, CaretBottom,
  Operation, DocumentAdd, Plus, Clock, InfoFilled, List
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { healthCheck } from '@/api/test'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 统计数据
const stats = ref([
  {
    title: '用户总数',
    value: 26,
    icon: markRaw(User),
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    trend: 12
  },
  {
    title: '站点数量',
    value: 8,
    icon: markRaw(OfficeBuilding),
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    trend: 5
  },
  {
    title: '内容总数',
    value: 243,
    icon: markRaw(Document),
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    trend: 18
  },
  {
    title: '今日访问',
    value: 1234,
    icon: markRaw(View),
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    trend: -3
  }
])

// 最近活动
const recentActivities = ref([
  {
    title: '创建了新内容',
    description: '发布了文章《系统使用指南》',
    time: '2025-10-04 14:30',
    type: 'primary'
  },
  {
    title: '更新了站点配置',
    description: '修改了主站点的域名设置',
    time: '2025-10-04 12:15',
    type: 'success'
  },
  {
    title: '审批了工作流',
    description: '通过了内容审核申请',
    time: '2025-10-04 10:20',
    type: 'warning'
  },
  {
    title: '添加了新用户',
    description: '创建了编辑账号 editor2',
    time: '2025-10-04 09:00',
    type: 'info'
  }
])

// 待办事项
const todos = ref([
  { text: '审核待发布的内容', done: false },
  { text: '更新系统配置', done: false },
  { text: '检查服务器状态', done: true },
  { text: '备份数据库', done: false }
])

// 获取当前时间段
const getTimeOfDay = () => {
  const hour = new Date().getHours()
  if (hour < 12) return 'morning'
  if (hour < 18) return 'afternoon'
  return 'evening'
}

// 获取问候语
const getCurrentGreeting = () => {
  const timeOfDay = getTimeOfDay()
  const greetings = {
    morning: '早上好',
    afternoon: '下午好',
    evening: '晚上好'
  }
  return greetings[timeOfDay]
}

// 获取当前日期
const getCurrentDate = () => {
  const date = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  return date.toLocaleDateString('zh-CN', options)
}

const createContent = () => {
  router.push('/contents')
}

const createSite = () => {
  router.push('/sites')
}

const viewLogs = () => {
  router.push('/logs')
}

const manageUsers = () => {
  router.push('/users')
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  overflow: hidden;
}

.welcome-card :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.welcome-text h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.welcome-icon {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  animation: slideInUp 0.5s ease-out;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-card-0 {
  animation-delay: 0.1s;
}

.stat-card-1 {
  animation-delay: 0.2s;
}

.stat-card-2 {
  animation-delay: 0.3s;
}

.stat-card-3 {
  animation-delay: 0.4s;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.trend-up {
  color: #67c23a;
  background: #f0f9ff;
}

.trend-down {
  color: #f56c6c;
  background: #fef0f0;
}

/* 内容区域 */
.content-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

/* 快速操作 */
.quick-actions-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.action-text {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 13px;
  color: #909399;
}

/* 最近活动 */
.recent-activity-card {
  border-radius: 12px;
}

.recent-activity-card :deep(.el-timeline-item__timestamp) {
  color: #909399;
  font-size: 13px;
}

.recent-activity-card :deep(.el-card) {
  margin-bottom: 0;
}

.recent-activity-card h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #303133;
}

.recent-activity-card p {
  margin: 0;
  font-size: 13px;
  color: #606266;
}

/* 待办事项 */
.todo-card {
  border-radius: 12px;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.todo-item:hover {
  background: #e8eaf0;
}

.todo-item :deep(.el-checkbox) {
  width: 100%;
}

.todo-item :deep(.el-checkbox__label) {
  font-size: 14px;
  color: #606266;
}
</style>


<template>
  <router-view v-slot="{ Component }">
    <transition name="fade-slide" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

/* 全局卡片样式优化 */
.el-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

/* 全局按钮样式优化 */
.el-button {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.el-button--success {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  border: none;
}

.el-button--success:hover {
  background: linear-gradient(135deg, #38f9d7 0%, #43e97b 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.4);
}

.el-button--warning {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  border: none;
}

.el-button--warning:hover {
  background: linear-gradient(135deg, #fee140 0%, #fa709a 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(250, 112, 154, 0.4);
}

.el-button--danger {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
}

.el-button--danger:hover {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.4);
}

/* 全局输入框样式优化 */
.el-input__wrapper {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #667eea inset;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px #667eea inset;
}

/* 全局表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th.el-table__cell {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

/* 全局标签样式优化 */
.el-tag {
  border-radius: 4px;
  font-weight: 500;
}

/* 全局对话框样式优化 */
.el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  margin: 0;
}

.el-dialog__title {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.el-dialog__headerbtn .el-dialog__close:hover {
  color: #f0f0f0;
}
</style>

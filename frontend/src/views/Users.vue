<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary">新增用户</el-button>
        </div>
      </template>
      <el-alert
        title="功能开发中"
        type="info"
        description="用户管理功能正在开发中，敬请期待..."
        show-icon
        :closable="false"
      />
    </el-card>
  </div>
</template>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>


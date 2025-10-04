<template>
  <div class="workflow-designer-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button type="text" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <span>工作流设计器 - {{ workflowName }}</span>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </template>

      <div class="designer-content">
        <!-- 左侧工具栏 -->
        <div class="toolbar">
          <h3>节点类型</h3>
          <div class="node-types">
            <div class="node-type" draggable="true" @dragstart="handleDragStart('START')">
              <el-icon><VideoPlay /></el-icon>
              <span>开始节点</span>
            </div>
            <div class="node-type" draggable="true" @dragstart="handleDragStart('APPROVAL')">
              <el-icon><Checked /></el-icon>
              <span>审批节点</span>
            </div>
            <div class="node-type" draggable="true" @dragstart="handleDragStart('CONDITION')">
              <el-icon><Connection /></el-icon>
              <span>条件节点</span>
            </div>
            <div class="node-type" draggable="true" @dragstart="handleDragStart('END')">
              <el-icon><CircleClose /></el-icon>
              <span>结束节点</span>
            </div>
          </div>
        </div>

        <!-- 中间画布 -->
        <div 
          class="canvas" 
          @drop="handleDrop" 
          @dragover.prevent
          @click="handleCanvasClick"
        >
          <div 
            v-for="node in nodes" 
            :key="node.id"
            class="workflow-node"
            :class="{ 'selected': selectedNode?.id === node.id }"
            :style="{ left: node.positionX + 'px', top: node.positionY + 'px' }"
            @click.stop="handleNodeClick(node)"
            draggable="true"
            @dragstart="handleNodeDragStart($event, node)"
          >
            <div class="node-icon">
              <el-icon v-if="node.nodeType === 'START'"><VideoPlay /></el-icon>
              <el-icon v-else-if="node.nodeType === 'APPROVAL'"><Checked /></el-icon>
              <el-icon v-else-if="node.nodeType === 'CONDITION'"><Connection /></el-icon>
              <el-icon v-else-if="node.nodeType === 'END'"><CircleClose /></el-icon>
            </div>
            <div class="node-name">{{ node.name }}</div>
            <el-button 
              type="danger" 
              size="small" 
              circle 
              class="delete-btn"
              @click.stop="handleDeleteNode(node)"
            >
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 右侧属性面板 -->
        <div class="properties-panel" v-if="selectedNode">
          <h3>节点属性</h3>
          <el-form :model="selectedNode" label-width="100px">
            <el-form-item label="节点名称">
              <el-input v-model="selectedNode.name" placeholder="请输入节点名称" />
            </el-form-item>
            <el-form-item label="节点类型">
              <el-select v-model="selectedNode.nodeType" disabled>
                <el-option label="开始节点" value="START" />
                <el-option label="审批节点" value="APPROVAL" />
                <el-option label="条件节点" value="CONDITION" />
                <el-option label="结束节点" value="END" />
              </el-select>
            </el-form-item>

            <template v-if="selectedNode.nodeType === 'APPROVAL'">
              <el-form-item label="审批人类型">
                <el-select v-model="selectedNode.approverType" placeholder="请选择审批人类型">
                  <el-option label="指定用户" value="USER" />
                  <el-option label="指定角色" value="ROLE" />
                  <el-option label="自定义" value="CUSTOM" />
                </el-select>
              </el-form-item>

              <el-form-item label="审批人" v-if="selectedNode.approverType === 'USER'">
                <el-select 
                  v-model="selectedNode.approverIds" 
                  multiple 
                  placeholder="请选择审批人"
                >
                  <el-option 
                    v-for="user in userList" 
                    :key="user.id" 
                    :label="user.nickname" 
                    :value="user.id" 
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="审批模式">
                <el-select v-model="selectedNode.approvalMode" placeholder="请选择审批模式">
                  <el-option label="任意一人审批" value="ANY" />
                  <el-option label="所有人审批" value="ALL" />
                  <el-option label="按顺序审批" value="SEQUENTIAL" />
                </el-select>
              </el-form-item>
            </template>

            <template v-if="selectedNode.nodeType === 'CONDITION'">
              <el-form-item label="条件表达式">
                <el-input 
                  v-model="selectedNode.conditionExpression" 
                  type="textarea"
                  :rows="4"
                  placeholder="请输入条件表达式"
                />
              </el-form-item>
            </template>

            <el-form-item label="排序">
              <el-input-number v-model="selectedNode.sortOrder" :min="0" />
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, VideoPlay, Checked, Connection, CircleClose, Close } from '@element-plus/icons-vue'
import { getWorkflowApi, updateWorkflowApi } from '@/api/workflow'
import { getAllUsersApi } from '@/api/user'

const route = useRoute()
const router = useRouter()

// 工作流信息
const workflowId = ref<number>(Number(route.params.id))
const workflowName = ref('')
const workflow = ref<any>(null)

// 节点列表
const nodes = ref<any[]>([])
const selectedNode = ref<any>(null)

// 用户列表
const userList = ref<any[]>([])

// 节点ID计数器
let nodeIdCounter = 1

// 加载工作流
const loadWorkflow = async () => {
  try {
    const data = await getWorkflowApi(workflowId.value)
    workflow.value = data
    workflowName.value = data.name
    
    if (data.nodes && data.nodes.length > 0) {
      nodes.value = data.nodes.map((node: any) => ({
        ...node,
        positionX: node.positionX || 100,
        positionY: node.positionY || 100
      }))
      nodeIdCounter = Math.max(...nodes.value.map(n => n.id || 0)) + 1
    }
  } catch (error: any) {
    console.error('加载工作流失败:', error)
    ElMessage.error(error.message || '加载工作流失败')
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const data = await getAllUsersApi()
    userList.value = data
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
  }
}

// 拖拽开始 - 从工具栏
const handleDragStart = (nodeType: string) => {
  const event = window.event as DragEvent
  if (event.dataTransfer) {
    event.dataTransfer.setData('nodeType', nodeType)
  }
}

// 拖拽开始 - 移动节点
const handleNodeDragStart = (event: DragEvent, node: any) => {
  if (event.dataTransfer) {
    event.dataTransfer.setData('nodeId', node.id.toString())
    event.dataTransfer.effectAllowed = 'move'
  }
}

// 放置节点
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  
  const nodeType = event.dataTransfer?.getData('nodeType')
  const nodeId = event.dataTransfer?.getData('nodeId')
  
  const canvas = event.currentTarget as HTMLElement
  const rect = canvas.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top
  
  if (nodeType) {
    // 创建新节点
    const newNode = {
      id: nodeIdCounter++,
      workflowId: workflowId.value,
      name: getNodeTypeName(nodeType),
      nodeType: nodeType,
      approverType: nodeType === 'APPROVAL' ? 'USER' : null,
      approverIds: [],
      approvalMode: nodeType === 'APPROVAL' ? 'ANY' : null,
      conditionExpression: null,
      positionX: x - 50,
      positionY: y - 25,
      sortOrder: nodes.value.length
    }
    nodes.value.push(newNode)
    selectedNode.value = newNode
  } else if (nodeId) {
    // 移动现有节点
    const node = nodes.value.find(n => n.id.toString() === nodeId)
    if (node) {
      node.positionX = x - 50
      node.positionY = y - 25
    }
  }
}

// 获取节点类型名称
const getNodeTypeName = (nodeType: string) => {
  const names: Record<string, string> = {
    'START': '开始',
    'APPROVAL': '审批节点',
    'CONDITION': '条件节点',
    'END': '结束'
  }
  return names[nodeType] || nodeType
}

// 点击节点
const handleNodeClick = (node: any) => {
  selectedNode.value = node
}

// 点击画布
const handleCanvasClick = () => {
  selectedNode.value = null
}

// 删除节点
const handleDeleteNode = (node: any) => {
  const index = nodes.value.findIndex(n => n.id === node.id)
  if (index > -1) {
    nodes.value.splice(index, 1)
    if (selectedNode.value?.id === node.id) {
      selectedNode.value = null
    }
  }
}

// 保存工作流
const handleSave = async () => {
  try {
    await updateWorkflowApi(workflowId.value, {
      ...workflow.value,
      nodes: nodes.value
    })
    ElMessage.success('保存成功')
  } catch (error: any) {
    console.error('保存失败:', error)
    ElMessage.error(error.message || '保存失败')
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 初始化
onMounted(() => {
  loadWorkflow()
  loadUsers()
})
</script>

<style scoped>
.workflow-designer-container {
  padding: 20px;
  height: calc(100vh - 40px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.designer-content {
  display: flex;
  height: calc(100vh - 200px);
  gap: 20px;
}

.toolbar {
  width: 200px;
  border-right: 1px solid #dcdfe6;
  padding-right: 20px;
}

.toolbar h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
}

.node-types {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.node-type {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: move;
  background: #fff;
  transition: all 0.3s;
}

.node-type:hover {
  background: #f5f7fa;
  border-color: #409eff;
}

.canvas {
  flex: 1;
  position: relative;
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: auto;
  min-height: 500px;
}

.workflow-node {
  position: absolute;
  width: 100px;
  height: 80px;
  background: #fff;
  border: 2px solid #409eff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: move;
  transition: all 0.3s;
}

.workflow-node:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.workflow-node.selected {
  border-color: #67c23a;
  box-shadow: 0 0 10px rgba(103, 194, 58, 0.5);
}

.node-icon {
  font-size: 24px;
  color: #409eff;
  margin-bottom: 5px;
}

.node-name {
  font-size: 12px;
  text-align: center;
  color: #606266;
}

.delete-btn {
  position: absolute;
  top: -10px;
  right: -10px;
  display: none;
}

.workflow-node:hover .delete-btn {
  display: block;
}

.properties-panel {
  width: 300px;
  border-left: 1px solid #dcdfe6;
  padding-left: 20px;
  overflow-y: auto;
}

.properties-panel h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 16px;
}
</style>


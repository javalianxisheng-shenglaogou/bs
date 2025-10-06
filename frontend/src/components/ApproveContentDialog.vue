<template>
  <el-dialog
    v-model="visible"
    title="审批内容"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="formData" label-width="100px">
      <el-form-item label="内容标题">
        <el-input v-model="contentTitle" disabled />
      </el-form-item>

      <el-form-item label="审批意见" required>
        <el-input
          v-model="formData.comment"
          type="textarea"
          :rows="4"
          placeholder="请输入审批意见..."
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="danger" @click="handleReject">拒绝</el-button>
        <el-button type="success" @click="handleApprove">通过</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyPendingTasksApi, approveTaskApi, rejectTaskApi } from '@/api/workflow'

const props = defineProps<{
  visible: boolean
  contentId: number
  contentTitle: string
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const visible = ref(false)
const contentTitle = ref('')
const taskId = ref<number | null>(null)

const formData = reactive({
  comment: ''
})

// 监听visible变化
watch(() => props.visible, async (newVal) => {
  visible.value = newVal
  if (newVal) {
    contentTitle.value = props.contentTitle
    formData.comment = ''
    // 查找该内容的待办任务
    await findPendingTask()
  }
})

// 查找待办任务
const findPendingTask = async () => {
  try {
    const response = await getMyPendingTasksApi({
      page: 1,
      size: 100
    })
    
    // 查找与当前内容相关的任务
    const tasks = response.data?.content || response.data?.list || []
    const task = tasks.find((t: any) => {
      // 假设任务的businessKey是内容ID
      return t.businessKey === String(props.contentId) || 
             t.businessKey === props.contentId ||
             (t.variables && t.variables.contentId === props.contentId)
    })
    
    if (task) {
      taskId.value = task.id
    } else {
      ElMessage.warning('未找到该内容的待办任务')
      taskId.value = null
    }
  } catch (error: any) {
    console.error('查找待办任务失败:', error)
    ElMessage.error(error.message || '查找待办任务失败')
  }
}

// 通过审批
const handleApprove = async () => {
  if (!taskId.value) {
    ElMessage.error('未找到待办任务')
    return
  }

  if (!formData.comment.trim()) {
    ElMessage.warning('请输入审批意见')
    return
  }

  try {
    await approveTaskApi(taskId.value, {
      comment: formData.comment,
      approved: true
    })
    
    ElMessage.success('审批通过')
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('审批失败:', error)
    ElMessage.error(error.message || '审批失败')
  }
}

// 拒绝审批
const handleReject = async () => {
  if (!taskId.value) {
    ElMessage.error('未找到待办任务')
    return
  }

  if (!formData.comment.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  try {
    await rejectTaskApi(taskId.value, {
      comment: formData.comment,
      approved: false
    })
    
    ElMessage.success('已拒绝')
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('拒绝失败:', error)
    ElMessage.error(error.message || '拒绝失败')
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>


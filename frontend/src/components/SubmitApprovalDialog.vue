<template>
  <el-dialog
    v-model="dialogVisible"
    title="提交审批"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-form-item label="选择工作流" prop="workflowCode">
        <el-select
          v-model="formData.workflowCode"
          placeholder="请选择工作流"
          style="width: 100%"
          @change="handleWorkflowChange"
        >
          <el-option
            v-for="workflow in workflowList"
            :key="workflow.code"
            :label="workflow.name"
            :value="workflow.code"
          >
            <span>{{ workflow.name }}</span>
            <span style="float: right; color: #8492a6; font-size: 13px">
              {{ workflow.description }}
            </span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="审批模式" prop="approvalMode">
        <el-radio-group v-model="formData.approvalMode">
          <el-radio label="SERIAL">串行审批（依次审批）</el-radio>
          <el-radio label="PARALLEL">并行审批（同时审批）</el-radio>
        </el-radio-group>
        <div style="color: #909399; font-size: 12px; margin-top: 5px">
          <span v-if="formData.approvalMode === 'SERIAL'">
            审批人将按照选择的顺序依次审批
          </span>
          <span v-else>
            所有审批人将同时收到审批任务，任意一人通过即可
          </span>
        </div>
      </el-form-item>

      <el-form-item label="选择审批人" prop="approverIds">
        <el-select
          v-model="formData.approverIds"
          multiple
          filterable
          placeholder="请选择审批人"
          style="width: 100%"
        >
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="`${user.nickname || user.username} (${user.username})`"
            :value="user.id"
          >
            <div style="display: flex; align-items: center">
              <el-avatar :size="24" :src="user.avatarUrl" style="margin-right: 10px">
                {{ (user.nickname || user.username).charAt(0) }}
              </el-avatar>
              <span>{{ user.nickname || user.username }}</span>
              <el-tag
                v-for="role in user.roles"
                :key="role"
                size="small"
                style="margin-left: 10px"
              >
                {{ getRoleName(role) }}
              </el-tag>
            </div>
          </el-option>
        </el-select>
        <div style="color: #909399; font-size: 12px; margin-top: 5px">
          已选择 {{ formData.approverIds.length }} 人
        </div>
      </el-form-item>

      <el-form-item label="审批说明">
        <el-input
          v-model="formData.comment"
          type="textarea"
          :rows="3"
          placeholder="请输入审批说明（可选）"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        提交审批
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { getAllWorkflowsApi } from '@/api/workflow'
import { getAllUsersApi } from '@/api/user'
import { submitApprovalWithOptionsApi } from '@/api/content'

interface Workflow {
  id: number
  name: string
  code: string
  description: string
  status: string
}

interface User {
  id: number
  username: string
  nickname: string
  email: string
  avatarUrl?: string
  roles: string[]
}

interface SubmitApprovalData {
  workflowCode: string
  approvalMode: 'SERIAL' | 'PARALLEL'
  approverIds: number[]
  comment: string
}

const props = defineProps<{
  visible: boolean
  contentId: number
  contentTitle: string
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const loading = ref(false)
const workflowList = ref<Workflow[]>([])
const userList = ref<User[]>([])

const formData = reactive<SubmitApprovalData>({
  workflowCode: '',
  approvalMode: 'SERIAL',
  approverIds: [],
  comment: ''
})

const formRules: FormRules = {
  workflowCode: [
    { required: true, message: '请选择工作流', trigger: 'change' }
  ],
  approvalMode: [
    { required: true, message: '请选择审批模式', trigger: 'change' }
  ],
  approverIds: [
    { required: true, message: '请至少选择一个审批人', trigger: 'change' },
    { type: 'array', min: 1, message: '请至少选择一个审批人', trigger: 'change' }
  ]
}

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    loadWorkflows()
    loadUsers()
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

const loadWorkflows = async () => {
  try {
    const response = await getAllWorkflowsApi()
    if (response.code === 200 && response.data) {
      // 只显示激活状态的工作流
      workflowList.value = response.data.filter((w: Workflow) => w.status === 'ACTIVE')
    }
  } catch (error) {
    console.error('加载工作流列表失败:', error)
    ElMessage.error('加载工作流列表失败')
  }
}

const loadUsers = async () => {
  try {
    const response = await getAllUsersApi()
    if (response.code === 200 && response.data) {
      // 只显示审批者和超级管理员
      userList.value = response.data.filter((user: User) => {
        return user.roles && user.roles.some((role: string) =>
          role === 'SUPER_ADMIN' || role === 'ADMIN' || role === 'REVIEWER'
        )
      })

      if (userList.value.length === 0) {
        ElMessage.warning('系统中暂无可用的审批人员')
      }
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  }
}

const handleWorkflowChange = () => {
  // 工作流变更时可以根据工作流配置自动设置审批人
  // 这里暂时不做处理
}

const getRoleName = (roleCode: string): string => {
  const roleNames: Record<string, string> = {
    'SUPER_ADMIN': '超级管理员',
    'ADMIN': '管理员',
    'SITE_ADMIN': '站点管理员',
    'EDITOR': '编辑',
    'USER': '用户'
  }
  return roleNames[roleCode] || roleCode
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const submitData = {
          contentId: props.contentId,
          workflowCode: formData.workflowCode,
          approvalMode: formData.approvalMode,
          approverIds: formData.approverIds,
          comment: formData.comment
        }

        await submitApprovalWithOptionsApi(submitData)

        ElMessage.success('提交审批成功')
        emit('success')
        handleClose()
      } catch (error: any) {
        console.error('提交审批失败:', error)
        ElMessage.error(error.message || '提交审批失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleClose = () => {
  formRef.value?.resetFields()
  formData.workflowCode = ''
  formData.approvalMode = 'SERIAL'
  formData.approverIds = []
  formData.comment = ''
  dialogVisible.value = false
}
</script>

<style scoped>
/* 对话框动画 */
:deep(.el-dialog) {
  animation: fadeInDown 0.3s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表单项优化 */
:deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
}

:deep(.el-input__wrapper) {
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409EFF inset;
}

/* 单选按钮组优化 */
:deep(.el-radio) {
  margin-right: 20px;
  padding: 10px 15px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s;
}

:deep(.el-radio:hover) {
  border-color: #409EFF;
  background: #f0f9ff;
}

:deep(.el-radio.is-checked) {
  border-color: #409EFF;
  background: #e6f4ff;
}

/* 选择器下拉项优化 */
:deep(.el-select-dropdown__item) {
  height: auto;
  padding: 10px 20px;
  transition: all 0.3s;
}

:deep(.el-select-dropdown__item:hover) {
  background: #f5f7fa;
  transform: translateX(5px);
}

/* 标签优化 */
:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
  transition: all 0.3s;
}

:deep(.el-tag:hover) {
  transform: scale(1.05);
}

/* 头像优化 */
:deep(.el-avatar) {
  transition: all 0.3s;
}

:deep(.el-avatar:hover) {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

/* 按钮优化 */
:deep(.el-button) {
  transition: all 0.3s;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

:deep(.el-button:active) {
  transform: translateY(0);
}

/* 提示文本优化 */
.el-form-item div[style*="color: #909399"] {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>


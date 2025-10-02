<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button v-if="!isEditing" type="primary" @click="handleEdit">编辑资料</el-button>
          <div v-else>
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        :disabled="!isEditing"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="formData.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="formData.nickname" placeholder="请输入昵称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="mobile">
              <el-input v-model="formData.mobile" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名">
              <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="MALE" />
                <el-option label="女" value="FEMALE" />
                <el-option label="保密" value="OTHER" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日">
              <el-date-picker
                v-model="formData.birthday"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag :type="getStatusType(formData.status)">
                {{ getStatusText(formData.status) }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="个人简介">
          <el-input
            v-model="formData.bio"
            type="textarea"
            :rows="4"
            placeholder="请输入个人简介"
          />
        </el-form-item>

        <el-form-item label="角色">
          <el-tag
            v-for="role in formData.roles"
            :key="role"
            type="success"
            style="margin-right: 10px"
          >
            {{ role }}
          </el-tag>
        </el-form-item>

        <el-form-item label="注册时间">
          <span>{{ formData.createdAt }}</span>
        </el-form-item>

        <el-form-item label="最后登录">
          <span>{{ formData.lastLoginAt || '暂无记录' }}</span>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 修改密码卡片 -->
    <el-card class="password-card" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>

      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="120px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
            修改密码
          </el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getCurrentUser } from '@/api/auth'
import { updateUser } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 表单引用
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 编辑状态
const isEditing = ref(false)
const saving = ref(false)
const changingPassword = ref(false)

// 表单数据
const formData = reactive({
  id: 0,
  username: '',
  nickname: '',
  email: '',
  mobile: '',
  realName: '',
  gender: '',
  birthday: '',
  bio: '',
  status: '',
  roles: [] as string[],
  createdAt: '',
  lastLoginAt: ''
})

// 原始数据（用于取消编辑）
let originalData: any = {}

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const rules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 密码验证规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const response = await getCurrentUser()
    if (response.code === 200 && response.data) {
      const data = response.data
      Object.assign(formData, {
        id: data.userId,
        username: data.username,
        nickname: data.nickname,
        email: data.email,
        mobile: data.mobile || '',
        realName: data.realName || '',
        gender: data.gender || '',
        birthday: data.birthday || '',
        bio: data.bio || '',
        status: data.status,
        roles: data.roles || [],
        createdAt: data.createdAt || '',
        lastLoginAt: data.lastLoginAt || ''
      })
      // 保存原始数据
      originalData = { ...formData }
    }
  } catch (error: any) {
    console.error('加载用户信息失败:', error)
    ElMessage.error(error.message || '加载用户信息失败')
  }
}

// 编辑
const handleEdit = () => {
  isEditing.value = true
}

// 取消
const handleCancel = () => {
  Object.assign(formData, originalData)
  isEditing.value = false
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true

    const response = await updateUser(formData.id, {
      email: formData.email,
      mobile: formData.mobile,
      nickname: formData.nickname,
      realName: formData.realName,
      gender: formData.gender,
      birthday: formData.birthday,
      bio: formData.bio
    })

    if (response.code === 200) {
      ElMessage.success('保存成功')
      isEditing.value = false
      // 重新加载用户信息
      await loadUserInfo()
      // 更新store中的用户信息
      await userStore.getUserInfo()
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
      ElMessage.error(error.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true

    // TODO: 调用修改密码API
    ElMessage.info('修改密码功能开发中...')

    // 重置表单
    resetPasswordForm()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('修改密码失败:', error)
    }
  } finally {
    changingPassword.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, any> = {
    ACTIVE: 'success',
    INACTIVE: 'info',
    LOCKED: 'danger',
    PENDING: 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '正常',
    INACTIVE: '停用',
    LOCKED: '锁定',
    PENDING: '待激活'
  }
  return textMap[status] || status
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-card :deep(.el-form-item__label) {
  font-weight: 500;
}
</style>


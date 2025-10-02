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

      <!-- 头像上传 -->
      <div class="avatar-section">
        <el-upload
          class="avatar-uploader"
          :action="uploadAction"
          :headers="uploadHeaders"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
        >
          <img v-if="formData.avatarUrl" :src="formData.avatarUrl" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="avatar-tip">
          <p>点击上传头像</p>
          <p class="tip-text">支持JPG、PNG格式，大小不超过5MB</p>
        </div>
      </div>

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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCurrentUser } from '@/api/auth'
import { updateUser, updateAvatar } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 上传配置
const uploadAction = computed(() => 'http://localhost:8080/api/files/avatar')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

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
  avatarUrl: '',
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
        avatarUrl: data.avatarUrl || '',
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

// 头像上传前验证
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'].includes(rawFile.type)
  const isLt5M = rawFile.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只支持JPG、PNG、GIF、WEBP格式的图片')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

// 头像上传成功
const handleAvatarSuccess: UploadProps['onSuccess'] = async (response) => {
  if (response.code === 200 && response.data) {
    const avatarUrl = response.data.url
    formData.avatarUrl = avatarUrl

    // 更新用户头像
    try {
      const updateResponse = await updateAvatar(formData.id, avatarUrl)
      if (updateResponse.code === 200) {
        ElMessage.success('头像上传成功')
        // 更新store中的用户信息
        await userStore.getUserInfo()
      }
    } catch (error: any) {
      console.error('更新头像失败:', error)
      ElMessage.error(error.message || '更新头像失败')
    }
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 头像上传失败
const handleAvatarError: UploadProps['onError'] = () => {
  ElMessage.error('头像上传失败，请重试')
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

.avatar-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.avatar-uploader {
  margin-right: 30px;
}

.avatar-uploader :deep(.el-upload) {
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader-icon {
  font-size: 40px;
  color: #8c939d;
}

.avatar-tip {
  flex: 1;
}

.avatar-tip p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.avatar-tip .tip-text {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>


<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人资料</h1>
      <p class="page-description">管理您的个人信息和账户设置</p>
    </div>

    <el-row :gutter="24">
      <!-- 基本信息 -->
      <el-col :span="16">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button
                v-if="!isEditing"
                type="primary"
                @click="startEdit"
                :loading="loading"
              >
                编辑资料
              </el-button>
              <div v-else>
                <el-button @click="cancelEdit">取消</el-button>
                <el-button type="primary" @click="saveProfile" :loading="saving">
                  保存
                </el-button>
              </div>
            </div>
          </template>

          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
            :disabled="!isEditing"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled />
              <div class="form-tip">用户名不可修改</div>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" />
            </el-form-item>

            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="80" :src="profileForm.avatarUrl" class="avatar-preview">
                  {{ profileForm.nickname?.charAt(0) }}
                </el-avatar>
                <div class="avatar-actions" v-if="isEditing">
                  <el-upload
                    :show-file-list="false"
                    :before-upload="beforeAvatarUpload"
                    :http-request="uploadAvatar"
                    accept="image/*"
                  >
                    <el-button size="small">上传头像</el-button>
                  </el-upload>
                  <el-button
                    v-if="profileForm.avatarUrl"
                    size="small"
                    type="danger"
                    @click="removeAvatar"
                  >
                    移除头像
                  </el-button>
                </div>
              </div>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改密码 -->
        <el-card class="password-card">
          <template #header>
            <span>修改密码</span>
          </template>

          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            @submit.prevent="changePassword"
          >
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                v-model="passwordForm.currentPassword"
                type="password"
                show-password
                placeholder="请输入当前密码"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="passwordChanging">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 账户信息 -->
      <el-col :span="8">
        <el-card class="account-info-card">
          <template #header>
            <span>账户信息</span>
          </template>

          <div class="info-item">
            <label>账户状态</label>
            <el-tag :type="user?.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ user?.status === 'ACTIVE' ? '正常' : '禁用' }}
            </el-tag>
          </div>

          <div class="info-item">
            <label>注册时间</label>
            <span>{{ formatDate(user?.createdAt) }}</span>
          </div>

          <div class="info-item">
            <label>最后登录</label>
            <span>{{ formatDate(user?.lastLoginAt) }}</span>
          </div>

          <div class="info-item">
            <label>用户角色</label>
            <div class="roles">
              <el-tag
                v-for="role in userRoles"
                :key="role.id"
                type="info"
                size="small"
                class="role-tag"
              >
                {{ role.name }}
              </el-tag>
            </div>
          </div>
        </el-card>

        <!-- 安全设置 -->
        <el-card class="security-card">
          <template #header>
            <span>安全设置</span>
          </template>

          <div class="security-item">
            <div class="security-info">
              <h4>登录密码</h4>
              <p>定期更换密码可以提高账户安全性</p>
            </div>
            <el-button size="small" @click="scrollToPasswordForm">修改</el-button>
          </div>

          <div class="security-item">
            <div class="security-info">
              <h4>邮箱验证</h4>
              <p>{{ user?.email ? '已验证' : '未设置邮箱' }}</p>
            </div>
            <el-button size="small" disabled>验证</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadRequestOptions } from 'element-plus'
import { useAppStore } from '../stores/app'
import { useAuthStore } from '../stores/auth'
import { updateProfile, changePassword as changeUserPassword, uploadFile } from '../api/users'

const appStore = useAppStore()
const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const passwordChanging = ref(false)
const isEditing = ref(false)

const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 用户信息
const user = computed(() => authStore.user)
const userRoles = computed(() => user.value?.userRoles?.map(ur => ur.role) || [])

// 个人资料表单
const profileForm = reactive({
  username: '',
  email: '',
  nickname: '',
  phone: '',
  avatarUrl: ''
})

// 密码修改表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
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

// 方法
const initProfileForm = () => {
  if (user.value) {
    profileForm.username = user.value.username
    profileForm.email = user.value.email || ''
    profileForm.nickname = user.value.nickname || ''
    profileForm.phone = user.value.phone || ''
    profileForm.avatarUrl = user.value.avatarUrl || ''
  }
}

const startEdit = () => {
  isEditing.value = true
  initProfileForm()
}

const cancelEdit = () => {
  isEditing.value = false
  initProfileForm()
}

const saveProfile = async () => {
  if (!profileFormRef.value) return

  try {
    await profileFormRef.value.validate()
    saving.value = true

    await updateProfile({
      email: profileForm.email,
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      avatarUrl: profileForm.avatarUrl
    })

    // 更新本地用户信息
    await authStore.fetchUserInfo()

    ElMessage.success('个人资料更新成功')
    isEditing.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败')
  } finally {
    saving.value = false
  }
}

const changePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    passwordChanging.value = true

    await changeUserPassword({
      currentPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    })

    ElMessage.success('密码修改成功')
    resetPasswordForm()
  } catch (error: any) {
    ElMessage.error(error.message || '密码修改失败')
  } finally {
    passwordChanging.value = false
  }
}

const resetPasswordForm = () => {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadAvatar = async (options: UploadRequestOptions) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)

    const response = await uploadFile(formData)
    profileForm.avatarUrl = response.data.url

    ElMessage.success('头像上传成功')
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  }
}

const removeAvatar = () => {
  ElMessageBox.confirm('确定要移除头像吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    profileForm.avatarUrl = ''
    ElMessage.success('头像已移除')
  })
}

const scrollToPasswordForm = () => {
  const passwordCard = document.querySelector('.password-card')
  if (passwordCard) {
    passwordCard.scrollIntoView({ behavior: 'smooth' })
  }
}

const formatDate = (dateString?: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  appStore.setBreadcrumbs([{ title: '个人资料' }])
  initProfileForm()
})
</script>

<style scoped>
.profile-page {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-description {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.profile-card,
.password-card,
.account-info-card,
.security-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  flex-shrink: 0;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  font-weight: 500;
  color: #606266;
}

.roles {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.role-tag {
  margin: 0;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.security-item:last-child {
  border-bottom: none;
}

.security-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.security-info p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }

  .el-col {
    margin-bottom: 16px;
  }

  .avatar-upload {
    flex-direction: column;
    align-items: flex-start;
  }

  .security-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>

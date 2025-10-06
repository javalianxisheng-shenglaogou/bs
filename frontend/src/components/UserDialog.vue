<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑用户' : '新增用户'"
    width="600px"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="请输入用户名"
          :disabled="isEdit"
        />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="formData.mobile" placeholder="请输入手机号" />
      </el-form-item>

      <el-form-item label="密码" prop="password" v-if="!isEdit">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
          show-password
        />
      </el-form-item>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入昵称" />
      </el-form-item>

      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="formData.gender">
          <el-radio label="MALE">男</el-radio>
          <el-radio label="FEMALE">女</el-radio>
          <el-radio label="OTHER">其他</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="生日" prop="birthday">
        <el-date-picker
          v-model="formData.birthday"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>

      <el-form-item label="个人简介" prop="bio">
        <el-input
          v-model="formData.bio"
          type="textarea"
          :rows="3"
          placeholder="请输入个人简介"
        />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态">
          <el-option label="正常" value="ACTIVE" />
          <el-option label="停用" value="INACTIVE" />
          <el-option label="锁定" value="LOCKED" />
          <el-option label="待激活" value="PENDING" />
        </el-select>
      </el-form-item>

      <el-form-item label="角色" prop="roleIds">
        <el-select
          v-model="formData.roleIds"
          multiple
          placeholder="请选择角色"
          style="width: 100%"
          :disabled="isEdit"
        >
          <el-option label="超级管理员" :value="1" />
          <el-option label="站点管理员" :value="2" />
          <el-option label="编辑者" :value="3" />
          <el-option label="审核者" :value="4" />
          <el-option label="查看者" :value="5" />
        </el-select>
        <div v-if="isEdit" style="color: #909399; font-size: 12px; margin-top: 5px;">
          注意：编辑用户时不能修改角色
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { createUser, updateUser, type UserCreateRequest, type UserUpdateRequest } from '@/api/user'

// Props
const props = defineProps<{
  visible: boolean
  userId?: number
  userData?: any
}>()

// Emits
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

// 响应式数据
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitting = ref(false)
const isEdit = ref(false)

// 表单数据
const formData = reactive<UserCreateRequest & { id?: number }>({
  username: '',
  email: '',
  mobile: '',
  password: '',
  nickname: '',
  realName: '',
  gender: '',
  birthday: '',
  bio: '',
  status: 'ACTIVE',
  roleIds: []
})

// 表单验证规则
const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20之间', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 监听visible变化
watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    isEdit.value = !!props.userId
    if (props.userData) {
      Object.assign(formData, props.userData)
    } else {
      resetForm()
    }
  }
})

// 监听dialogVisible变化
watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

// 重置表单
const resetForm = () => {
  formData.username = ''
  formData.email = ''
  formData.mobile = ''
  formData.password = ''
  formData.nickname = ''
  formData.realName = ''
  formData.gender = ''
  formData.birthday = ''
  formData.bio = ''
  formData.status = 'ACTIVE'
  formData.roleIds = []
  formRef.value?.clearValidate()
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (isEdit.value && props.userId) {
      // 更新用户
      const updateData: UserUpdateRequest = {
        email: formData.email,
        mobile: formData.mobile,
        nickname: formData.nickname,
        realName: formData.realName,
        gender: formData.gender,
        birthday: formData.birthday,
        bio: formData.bio,
        status: formData.status,
        roleIds: formData.roleIds
      }
      const response = await updateUser(props.userId, updateData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        emit('success')
        handleClose()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      // 创建用户
      const response = await createUser(formData)
      if (response.code === 200) {
        ElMessage.success('创建成功')
        emit('success')
        handleClose()
      } else {
        ElMessage.error(response.message || '创建失败')
      }
    }
  } catch (error: any) {
    if (error !== false) {
      console.error('提交失败:', error)
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}
</script>


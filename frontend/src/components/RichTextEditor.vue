<template>
  <div class="rich-text-editor">
    <QuillEditor
      ref="editorRef"
      v-model:content="content"
      :options="editorOptions"
      :style="{ height: height }"
      content-type="html"
      @update:content="handleUpdate"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import axios from 'axios'

interface Props {
  modelValue: string
  height?: string
  placeholder?: string
}

interface Emits {
  (e: 'update:modelValue', value: string): void
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  height: '400px',
  placeholder: '请输入内容...'
})

const emit = defineEmits<Emits>()
const userStore = useUserStore()

const content = ref(props.modelValue)
const editorRef = ref<any>(null)

// 编辑器配置
const editorOptions = {
  theme: 'snow',
  placeholder: props.placeholder,
  modules: {
    toolbar: {
      container: [
        ['bold', 'italic', 'underline', 'strike'],        // 加粗、斜体、下划线、删除线
        ['blockquote', 'code-block'],                     // 引用、代码块
        [{ 'header': 1 }, { 'header': 2 }],               // 标题
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],     // 列表
        [{ 'indent': '-1'}, { 'indent': '+1' }],          // 缩进
        [{ 'size': ['small', false, 'large', 'huge'] }],  // 字体大小
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],        // 标题
        [{ 'color': [] }, { 'background': [] }],          // 字体颜色、背景颜色
        [{ 'align': [] }],                                // 对齐方式
        ['clean'],                                        // 清除格式
        ['link', 'image']                                 // 链接、图片
      ],
      handlers: {
        image: imageHandler
      }
    }
  }
}

// 自定义图片上传处理
function imageHandler() {
  const input = document.createElement('input')
  input.setAttribute('type', 'file')
  input.setAttribute('accept', 'image/*')
  input.click()

  input.onchange = async () => {
    const file = input.files?.[0]
    if (!file) return

    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      ElMessage.error('只能上传图片文件!')
      return
    }

    // 验证文件大小(5MB)
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.error('图片大小不能超过5MB!')
      return
    }

    // 上传图片
    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await axios.post(
        import.meta.env.VITE_API_BASE_URL + '/files/upload',
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${userStore.token}`
          }
        }
      )

      if (response.data.code === 200) {
        const imageUrl = response.data.data.url
        const quill = editorRef.value?.getQuill()
        if (quill) {
          const range = quill.getSelection()
          quill.insertEmbed(range?.index || 0, 'image', imageUrl)
        }
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error(response.data.message || '图片上传失败')
      }
    } catch (error) {
      console.error('图片上传失败:', error)
      ElMessage.error('图片上传失败')
    }
  }
}

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  if (newValue !== content.value) {
    content.value = newValue
  }
})

// 处理内容更新
const handleUpdate = (value: string) => {
  emit('update:modelValue', value)
}

// 组件挂载后保存编辑器引用
onMounted(() => {
  // 编辑器引用会在QuillEditor组件内部设置
})
</script>

<style scoped>
.rich-text-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.rich-text-editor :deep(.ql-container) {
  font-size: 14px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.rich-text-editor :deep(.ql-editor) {
  min-height: 300px;
}

.rich-text-editor :deep(.ql-toolbar) {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  background-color: #f5f7fa;
}

.rich-text-editor :deep(.ql-container) {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}
</style>


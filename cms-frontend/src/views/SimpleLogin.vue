<template>
  <div class="simple-login">
    <h1>简单登录测试</h1>
    <form @submit.prevent="handleLogin">
      <div>
        <label>用户名:</label>
        <input v-model="username" type="text" required />
      </div>
      <div>
        <label>密码:</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>
    <div v-if="result" class="result">
      <h3>结果:</h3>
      <pre>{{ result }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const username = ref('superadmin')
const password = ref('admin123')
const loading = ref(false)
const result = ref('')

const handleLogin = async () => {
  loading.value = true
  result.value = ''
  
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        usernameOrEmail: username.value,
        password: password.value
      })
    })
    
    const data = await response.json()
    result.value = JSON.stringify(data, null, 2)
  } catch (error) {
    result.value = `错误: ${error.message}`
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.simple-login {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
}

.simple-login div {
  margin-bottom: 15px;
}

.simple-login label {
  display: block;
  margin-bottom: 5px;
}

.simple-login input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.simple-login button {
  width: 100%;
  padding: 10px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.simple-login button:disabled {
  background: #ccc;
}

.result {
  margin-top: 20px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
}

.result pre {
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>

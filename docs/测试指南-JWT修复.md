# JWT认证修复 - 测试指南

## 📋 修复内容

### 问题描述
JWT认证失败时，后端只记录错误日志，但没有返回401状态码，导致前端无法知道Token已失效，用户无法自动跳转到登录页。

### 修复内容
修改了 `JwtAuthenticationFilter.java`，添加了以下功能：

1. **Token过期检测**
   - 捕获 `ExpiredJwtException` 异常
   - 返回401状态码和友好的错误消息："Token已过期，请重新登录"

2. **Token签名无效检测**
   - 捕获 `SignatureException` 异常
   - 返回401状态码和友好的错误消息："Token签名无效，请重新登录"

3. **Token验证失败检测**
   - 验证Token时如果失败，返回401状态码
   - 错误消息："Token验证失败"

4. **统一的401响应格式**
   ```json
   {
     "code": 401,
     "message": "Token已过期，请重新登录",
     "data": null
   }
   ```

### 前端自动处理
前端的 `request.ts` 已经配置了401错误拦截器：
- 自动清除localStorage中的token
- 自动跳转到登录页
- 显示友好的错误提示

## 🧪 测试步骤

### 步骤1：清除旧Token

**方法1：使用清除工具（推荐）**
1. 在浏览器中打开：http://localhost:3000/clear-token.html
2. 点击"清除Token"按钮
3. 点击"前往登录"按钮

**方法2：手动清除**
1. 打开浏览器开发者工具（F12）
2. 切换到 **Console** 标签页
3. 输入以下命令：
   ```javascript
   localStorage.clear()
   sessionStorage.clear()
   location.reload()
   ```

### 步骤2：重新登录

1. 访问：http://localhost:3000
2. 输入用户名：`admin`
3. 输入密码：`password`
4. 点击"登录"按钮

### 步骤3：测试所有页面

打开开发者工具（F12），切换到 **Console** 标签页，然后依次测试以下页面：

#### 1. Dashboard（首页）
- 应该显示统计数据
- 无警告和错误

#### 2. 用户管理
- 应该显示26个用户
- 分页功能正常
- Console显示：`✅ 加载成功: 26条数据`

#### 3. 内容管理
- 应该显示243篇内容
- 有"提交审批"按钮
- 分页功能正常
- Console显示：`✅ 加载成功: 243条数据`

#### 4. 站点管理
- 应该显示8个站点
- Console显示：`✅ 加载成功: 8条数据`

#### 5. 分类管理
- 应该显示分类树形结构
- 可以展开/折叠
- Console显示：`✅ 加载成功: X条数据`

#### 6. 工作流管理
- 应该显示2个工作流
- 分页功能正常
- Console显示：`✅ 加载成功: 2条数据`

#### 7. 我的任务
- 切换"待办任务"和"已办任务"标签页
- 应该显示任务列表
- Console显示：`✅ 加载成功: X条数据`

#### 8. 工作流实例
- 应该显示工作流实例列表
- 分页功能正常
- Console显示：`✅ 加载成功: X条数据`

#### 9. 系统日志 ⭐（重点测试）
- 应该显示日志列表
- 可以按级别筛选（INFO/WARN/ERROR）
- 可以按模块筛选
- 可以搜索
- 分页功能正常
- Console显示：`✅ 加载成功: X条数据`

### 步骤4：测试Token过期处理

**模拟Token过期：**

1. 打开开发者工具（F12）
2. 切换到 **Console** 标签页
3. 输入以下命令修改Token为无效值：
   ```javascript
   localStorage.setItem('token', 'invalid-token-12345')
   ```
4. 刷新页面或点击任意菜单

**预期结果：**
- ✅ 浏览器Console显示：`响应错误: Request failed with status code 401`
- ✅ 显示错误提示：`未授权，请重新登录`
- ✅ 自动跳转到登录页
- ✅ localStorage中的token被清除

## ✅ 测试检查清单

### 基础功能
- [ ] 可以正常登录
- [ ] 所有页面都能正常显示数据
- [ ] 分页功能正常工作
- [ ] 搜索和筛选功能正常
- [ ] Console无红色错误

### JWT认证功能
- [ ] Token过期时自动跳转到登录页
- [ ] Token无效时自动跳转到登录页
- [ ] 显示友好的错误提示
- [ ] localStorage中的token被自动清除

### 系统日志功能
- [ ] 日志列表正常显示
- [ ] 可以按级别筛选
- [ ] 可以按模块筛选
- [ ] 可以搜索
- [ ] 分页功能正常
- [ ] 日志内容格式正确

## 🐛 常见问题

### 问题1：系统日志页面仍然显示"No Data"

**原因**：Token过期或无效

**解决方案**：
1. 清除浏览器缓存和localStorage
2. 重新登录
3. 再次访问系统日志页面

### 问题2：登录后立即提示"未授权"

**原因**：后端JWT密钥可能改变了

**解决方案**：
1. 重启后端服务
2. 清除浏览器localStorage
3. 重新登录

### 问题3：Console显示"Network Error"

**原因**：后端服务未启动或端口被占用

**解决方案**：
1. 检查后端是否正在运行：http://localhost:8080/api/swagger-ui.html
2. 如果无法访问，重启后端服务

## 📝 后端日志检查

如果遇到问题，可以查看后端日志：

### 查看INFO日志
```powershell
Get-Content backend/logs/multi-site-cms-info.log -Tail 50
```

### 查看ERROR日志
```powershell
Get-Content backend/logs/multi-site-cms-error.log -Tail 50
```

### 查看JWT认证日志
```powershell
Get-Content backend/logs/multi-site-cms-info.log | Select-String "JWT|Token|认证"
```

## 🎉 测试成功标准

所有以下条件都满足，则测试成功：

1. ✅ 可以正常登录
2. ✅ 所有页面都能正常显示数据
3. ✅ 系统日志页面显示日志列表
4. ✅ Token过期时自动跳转到登录页
5. ✅ Console无红色错误
6. ✅ Network请求都返回200状态码（除了401测试）

---

**测试完成后，请告诉我结果！** 🚀


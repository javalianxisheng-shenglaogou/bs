# 测试个人资料相关API

# 1. 登录获取token
Write-Host "=== 登录获取Token ===" -ForegroundColor Green
$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.data.accessToken
    Write-Host "登录成功，Token: $($token.Substring(0, 50))..." -ForegroundColor Green
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. 获取当前用户资料
Write-Host "`n=== 获取当前用户资料 ===" -ForegroundColor Green
try {
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
    Write-Host "当前用户资料:" -ForegroundColor Yellow
    Write-Host "用户名: $($profileResponse.data.username)"
    Write-Host "邮箱: $($profileResponse.data.email)"
    Write-Host "昵称: $($profileResponse.data.nickname)"
    Write-Host "手机: $($profileResponse.data.phone)"
    Write-Host "头像: $($profileResponse.data.avatarUrl)"
} catch {
    Write-Host "获取用户资料失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 更新个人资料
Write-Host "`n=== 更新个人资料 ===" -ForegroundColor Green
$updateProfileBody = @{
    email = "superadmin@newdomain.com"
    nickname = "Super Admin"
    phone = "13800138000"
    avatarUrl = "https://example.com/avatar.jpg"
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method PUT -Headers $headers -Body $updateProfileBody
    Write-Host "个人资料更新成功:" -ForegroundColor Green
    Write-Host "新邮箱: $($updateResponse.data.email)"
    Write-Host "新昵称: $($updateResponse.data.nickname)"
    Write-Host "新手机: $($updateResponse.data.phone)"
} catch {
    Write-Host "更新个人资料失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 测试修改密码
Write-Host "`n=== 测试修改密码 ===" -ForegroundColor Green
$changePasswordBody = @{
    currentPassword = "admin123"
    newPassword = "newpassword123"
} | ConvertTo-Json

try {
    $passwordResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $changePasswordBody
    Write-Host "密码修改成功: $($passwordResponse.message)" -ForegroundColor Green
    
    # 用新密码重新登录测试
    Write-Host "`n=== 用新密码重新登录测试 ===" -ForegroundColor Green
    $newLoginBody = @{
        usernameOrEmail = "superadmin"
        password = "newpassword123"
    } | ConvertTo-Json
    
    $newLoginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $newLoginBody
    Write-Host "新密码登录成功!" -ForegroundColor Green
    
    # 改回原密码
    $newToken = $newLoginResponse.data.accessToken
    $newHeaders = @{
        "Authorization" = "Bearer $newToken"
        "Content-Type" = "application/json"
    }
    
    $revertPasswordBody = @{
        currentPassword = "newpassword123"
        newPassword = "admin123"
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $newHeaders -Body $revertPasswordBody
    Write-Host "密码已改回原密码" -ForegroundColor Green
    
} catch {
    Write-Host "修改密码失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Profile API Test Complete ===" -ForegroundColor Green

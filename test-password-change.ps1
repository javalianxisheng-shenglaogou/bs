# 测试密码修改功能

# 1. 登录获取token
Write-Host "=== 登录获取Token ===" -ForegroundColor Green
$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.data.accessToken
    Write-Host "登录成功" -ForegroundColor Green
} catch {
    Write-Host "登录失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. 测试修改密码 - 使用正确的当前密码
Write-Host "`n=== 测试修改密码 ===" -ForegroundColor Green
$changePasswordBody = @{
    currentPassword = "admin123"
    newPassword = "newpass123"
} | ConvertTo-Json

try {
    $passwordResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $changePasswordBody
    Write-Host "密码修改成功: $($passwordResponse.message)" -ForegroundColor Green
    
    # 3. 用新密码登录测试
    Write-Host "`n=== 用新密码登录测试 ===" -ForegroundColor Green
    $newLoginBody = @{
        usernameOrEmail = "superadmin"
        password = "newpass123"
    } | ConvertTo-Json
    
    try {
        $newLoginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $newLoginBody
        Write-Host "新密码登录成功!" -ForegroundColor Green
        
        # 4. 改回原密码
        Write-Host "`n=== 改回原密码 ===" -ForegroundColor Green
        $newToken = $newLoginResponse.data.accessToken
        $newHeaders = @{
            "Authorization" = "Bearer $newToken"
            "Content-Type" = "application/json"
        }
        
        $revertPasswordBody = @{
            currentPassword = "newpass123"
            newPassword = "admin123"
        } | ConvertTo-Json
        
        $revertResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $newHeaders -Body $revertPasswordBody
        Write-Host "密码已改回原密码: $($revertResponse.message)" -ForegroundColor Green
        
    } catch {
        Write-Host "新密码登录失败: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "修改密码失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "错误详情: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`n=== 密码修改测试完成 ===" -ForegroundColor Green

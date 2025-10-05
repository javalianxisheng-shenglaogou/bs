# 多站点CMS系统 - 自动化测试脚本
# PowerShell脚本，用于测试系统各项功能

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  多站点CMS系统 - 自动化测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 测试配置
$baseUrl = "http://localhost:8080/api"
$frontendUrl = "http://localhost:3000"

# 测试结果统计
$totalTests = 0
$passedTests = 0
$failedTests = 0

# 测试函数
function Test-API {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Body = $null,
        [hashtable]$Headers = $null,
        [int]$ExpectedCode = 200
    )
    
    $global:totalTests++
    Write-Host "测试 $global:totalTests : $Name" -NoNewline
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            ContentType = 'application/json'
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json)
        }
        
        if ($Headers) {
            $params.Headers = $Headers
        }
        
        $response = Invoke-RestMethod @params
        
        if ($response.code -eq $ExpectedCode) {
            Write-Host " ✅ 通过" -ForegroundColor Green
            $global:passedTests++
            return $response
        } else {
            Write-Host " ❌ 失败 (返回码: $($response.code))" -ForegroundColor Red
            $global:failedTests++
            return $null
        }
    } catch {
        Write-Host " ❌ 失败 (异常: $($_.Exception.Message))" -ForegroundColor Red
        $global:failedTests++
        return $null
    }
}

# 1. 测试后端服务
Write-Host "`n【1】测试后端服务" -ForegroundColor Yellow
Write-Host "-----------------------------------"

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/test/health" -Method Get -TimeoutSec 5
    Write-Host "✅ 后端服务运行正常 (端口 8080)" -ForegroundColor Green
} catch {
    Write-Host "❌ 后端服务未启动或无法访问" -ForegroundColor Red
    Write-Host "   请先启动后端: cd backend && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# 2. 测试前端服务
Write-Host "`n【2】测试前端服务" -ForegroundColor Yellow
Write-Host "-----------------------------------"

try {
    $response = Invoke-WebRequest -Uri $frontendUrl -Method Get -TimeoutSec 5
    Write-Host "✅ 前端服务运行正常 (端口 3000)" -ForegroundColor Green
} catch {
    Write-Host "❌ 前端服务未启动或无法访问" -ForegroundColor Red
    Write-Host "   请先启动前端: cd frontend && npm run dev" -ForegroundColor Yellow
    exit 1
}

# 3. 测试用户认证
Write-Host "`n【3】测试用户认证" -ForegroundColor Yellow
Write-Host "-----------------------------------"

# 3.1 测试登录
$loginBody = @{
    username = "admin"
    password = "password"
}
$loginResponse = Test-API -Name "管理员登录" -Method "POST" -Url "$baseUrl/auth/login" -Body $loginBody

if ($loginResponse) {
    $token = $loginResponse.data.token
    $headers = @{
        Authorization = "Bearer $token"
    }
    Write-Host "   Token: $($token.Substring(0, 50))..." -ForegroundColor Gray
} else {
    Write-Host "❌ 登录失败，无法继续测试" -ForegroundColor Red
    exit 1
}

# 3.2 测试获取当前用户信息
Test-API -Name "获取当前用户信息" -Method "GET" -Url "$baseUrl/auth/me" -Headers $headers | Out-Null

# 3.3 测试错误密码登录
$wrongLoginBody = @{
    username = "admin"
    password = "wrongpassword"
}
$wrongLoginResponse = Test-API -Name "错误密码登录（应失败）" -Method "POST" -Url "$baseUrl/auth/login" -Body $wrongLoginBody -ExpectedCode 3003

# 4. 测试用户管理
Write-Host "`n【4】测试用户管理" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取用户列表" -Method "GET" -Url "$baseUrl/users?page=1&size=10" -Headers $headers | Out-Null
Test-API -Name "获取所有用户" -Method "GET" -Url "$baseUrl/users/all" -Headers $headers | Out-Null

# 5. 测试站点管理
Write-Host "`n【5】测试站点管理" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取站点列表" -Method "GET" -Url "$baseUrl/sites?page=1&size=10" -Headers $headers | Out-Null
Test-API -Name "获取所有站点" -Method "GET" -Url "$baseUrl/sites/all" -Headers $headers | Out-Null

# 6. 测试内容管理
Write-Host "`n【6】测试内容管理" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取内容列表" -Method "GET" -Url "$baseUrl/contents?page=1&size=10" -Headers $headers | Out-Null

# 7. 测试分类管理
Write-Host "`n【7】测试分类管理" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取分类列表" -Method "GET" -Url "$baseUrl/categories?page=1&size=10" -Headers $headers | Out-Null

# 8. 测试工作流管理
Write-Host "`n【8】测试工作流管理" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取工作流列表" -Method "GET" -Url "$baseUrl/workflows?page=1&size=10" -Headers $headers | Out-Null
Test-API -Name "获取所有工作流" -Method "GET" -Url "$baseUrl/workflows/all" -Headers $headers | Out-Null

# 9. 测试角色和权限
Write-Host "`n【9】测试角色和权限" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取角色列表" -Method "GET" -Url "$baseUrl/roles?page=1&size=10" -Headers $headers | Out-Null
Test-API -Name "获取所有角色" -Method "GET" -Url "$baseUrl/roles/all" -Headers $headers | Out-Null
Test-API -Name "获取权限列表" -Method "GET" -Url "$baseUrl/permissions?page=1&size=10" -Headers $headers | Out-Null
Test-API -Name "获取所有权限" -Method "GET" -Url "$baseUrl/permissions/all" -Headers $headers | Out-Null

# 10. 测试系统日志
Write-Host "`n【10】测试系统日志" -ForegroundColor Yellow
Write-Host "-----------------------------------"

Test-API -Name "获取系统日志" -Method "GET" -Url "$baseUrl/logs?page=1&size=10" -Headers $headers | Out-Null

# 测试结果汇总
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  测试结果汇总" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "总测试数: $totalTests" -ForegroundColor White
Write-Host "通过: $passedTests" -ForegroundColor Green
Write-Host "失败: $failedTests" -ForegroundColor Red
Write-Host "通过率: $([math]::Round($passedTests / $totalTests * 100, 2))%" -ForegroundColor $(if ($failedTests -eq 0) { "Green" } else { "Yellow" })
Write-Host ""

if ($failedTests -eq 0) {
    Write-Host "🎉 所有测试通过！系统运行正常！" -ForegroundColor Green
} else {
    Write-Host "⚠️  有 $failedTests 个测试失败，请检查系统配置" -ForegroundColor Yellow
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  快速访问链接" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "前端地址: $frontendUrl" -ForegroundColor Cyan
Write-Host "后端API: $baseUrl" -ForegroundColor Cyan
Write-Host "Swagger文档: http://localhost:8080/api/swagger-ui.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "测试账号:" -ForegroundColor Yellow
Write-Host "  超级管理员: admin / password" -ForegroundColor White
Write-Host "  站点管理员: siteadmin / password" -ForegroundColor White
Write-Host "  编辑者: editor1 / password" -ForegroundColor White
Write-Host ""

# 询问是否打开浏览器
$openBrowser = Read-Host "是否打开浏览器测试前端？(Y/N)"
if ($openBrowser -eq "Y" -or $openBrowser -eq "y") {
    Start-Process $frontendUrl
    Write-Host "✅ 已打开浏览器" -ForegroundColor Green
}

Write-Host "`n测试完成！" -ForegroundColor Green


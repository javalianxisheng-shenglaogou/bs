# Verify current login credentials
Write-Host "=== Verifying Login Credentials ===" -ForegroundColor Green

# Test admin user
Write-Host "`n1. Testing admin user..." -ForegroundColor Yellow
$adminLoginBody = @{
    usernameOrEmail = "admin"
    password = "password"
} | ConvertTo-Json

try {
    $adminResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $adminLoginBody
    if ($adminResponse.success) {
        Write-Host "✅ Admin login successful!" -ForegroundColor Green
        Write-Host "Username: admin" -ForegroundColor Cyan
        Write-Host "Password: password" -ForegroundColor Cyan
        Write-Host "User Info: $($adminResponse.data.user.username) - $($adminResponse.data.user.email)" -ForegroundColor White
        Write-Host "Roles: $($adminResponse.data.user.roles | ForEach-Object { $_.name } | Join-String -Separator ', ')" -ForegroundColor White
    } else {
        Write-Host "❌ Admin login failed: $($adminResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Admin login error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test superadmin user
Write-Host "`n2. Testing superadmin user..." -ForegroundColor Yellow
$superadminLoginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $superadminResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $superadminLoginBody
    if ($superadminResponse.success) {
        Write-Host "✅ Superadmin login successful!" -ForegroundColor Green
        Write-Host "Username: superadmin" -ForegroundColor Cyan
        Write-Host "Password: admin123" -ForegroundColor Cyan
        Write-Host "User Info: $($superadminResponse.data.user.username) - $($superadminResponse.data.user.email)" -ForegroundColor White
        Write-Host "Roles: $($superadminResponse.data.user.roles | ForEach-Object { $_.name } | Join-String -Separator ', ')" -ForegroundColor White
    } else {
        Write-Host "❌ Superadmin login failed: $($superadminResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Superadmin login error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n📋 Login Instructions:" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor White
Write-Host "Frontend URL: http://localhost:3001/login" -ForegroundColor Green
Write-Host "Valid Credentials:" -ForegroundColor Yellow
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Password: password" -ForegroundColor White
Write-Host "" -ForegroundColor White
Write-Host "After login, you can access:" -ForegroundColor Yellow
Write-Host "  - Dashboard: http://localhost:3001/dashboard" -ForegroundColor White
Write-Host "  - Content Management: http://localhost:3001/content" -ForegroundColor White
Write-Host "  - Profile: http://localhost:3001/profile" -ForegroundColor White

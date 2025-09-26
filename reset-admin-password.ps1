# Reset admin password in database
Write-Host "=== Resetting admin password ===" -ForegroundColor Green

# BCrypt hash for "admin123" (generated with strength 10)
$newPasswordHash = '$2a$10$GsJg5GZR1A6OtvGWu/5td..WiXFtE2RdxYchCnFySkiD1ufLg.4sy'

$updateQuery = "UPDATE users SET password_hash = '$newPasswordHash' WHERE username = 'admin';"

Write-Host "Updating admin password in database..." -ForegroundColor Yellow

try {
    & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -h localhost -P 3306 -u root -p123456 multi_site_cms -e $updateQuery
    Write-Host "Admin password updated successfully" -ForegroundColor Green
    
    # Test login
    Write-Host "`nTesting login with new password..." -ForegroundColor Yellow
    
    $loginBody = @{
        usernameOrEmail = "admin"
        password = "admin123"
    } | ConvertTo-Json
    
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    
    if ($loginResponse.success) {
        Write-Host "Login test successful!" -ForegroundColor Green
        Write-Host "Token: $($loginResponse.data.accessToken.Substring(0, 50))..."
    } else {
        Write-Host "Login test failed: $($loginResponse.message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

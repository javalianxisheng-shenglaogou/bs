# Fix user passwords with known BCrypt hashes
Write-Host "=== Fixing user passwords ===" -ForegroundColor Green

# Known BCrypt hashes:
# "password" -> $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
# "admin123" -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl.k5uKw4Oa

$passwordHash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
$admin123Hash = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl.k5uKw4Oa'

Write-Host "Updating admin password to 'password'..." -ForegroundColor Yellow
$updateAdmin = "UPDATE users SET password_hash = '$passwordHash' WHERE username = 'admin';"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -h localhost -P 3306 -u root -p123456 multi_site_cms -e $updateAdmin

Write-Host "Updating superadmin password to 'admin123'..." -ForegroundColor Yellow  
$updateSuperAdmin = "UPDATE users SET password_hash = '$admin123Hash' WHERE username = 'superadmin';"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -h localhost -P 3306 -u root -p123456 multi_site_cms -e $updateSuperAdmin

Write-Host "Password updates completed!" -ForegroundColor Green

# Test admin login with "password"
Write-Host "`nTesting admin login with password 'password'..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "admin"
    password = "password"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    
    if ($loginResponse.success) {
        Write-Host "✅ Admin login successful!" -ForegroundColor Green
        $token = $loginResponse.data.accessToken
        Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Cyan
        
        # Test profile access
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        
        $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
        Write-Host "✅ Profile access successful!" -ForegroundColor Green
        Write-Host "User: $($profileResponse.data.username) ($($profileResponse.data.email))" -ForegroundColor Cyan
        
    } else {
        Write-Host "❌ Admin login failed: $($loginResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Admin login error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test superadmin login with "admin123"
Write-Host "`nTesting superadmin login with password 'admin123'..." -ForegroundColor Yellow
$loginBody2 = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse2 = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody2
    
    if ($loginResponse2.success) {
        Write-Host "✅ Superadmin login successful!" -ForegroundColor Green
        Write-Host "Token: $($loginResponse2.data.accessToken.Substring(0, 50))..." -ForegroundColor Cyan
    } else {
        Write-Host "❌ Superadmin login failed: $($loginResponse2.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Superadmin login error: $($_.Exception.Message)" -ForegroundColor Red
}

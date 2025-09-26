# Test admin login
Write-Host "=== Testing admin login ===" -ForegroundColor Green

$loginBody = @{
    usernameOrEmail = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "Admin login successful!" -ForegroundColor Green

    if ($loginResponse.data -and $loginResponse.data.accessToken) {
        $token = $loginResponse.data.accessToken
        Write-Host "Token received: $($token.Substring(0, 50))..." -ForegroundColor Yellow
    } else {
        Write-Host "No token in response" -ForegroundColor Red
        $loginResponse | ConvertTo-Json -Depth 5
        return
    }
    
    # Test profile access
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
    Write-Host "Profile access successful" -ForegroundColor Green
    Write-Host "User: $($profileResponse.data.username)"
    Write-Host "Email: $($profileResponse.data.email)"
    
    # Test password change
    $changePasswordBody = @{
        currentPassword = "admin123"
        newPassword = "newpass123"
    } | ConvertTo-Json
    
    try {
        $passwordResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $changePasswordBody
        Write-Host "Password change successful: $($passwordResponse.message)" -ForegroundColor Green
        
        # Change back
        $revertPasswordBody = @{
            currentPassword = "newpass123"
            newPassword = "admin123"
        } | ConvertTo-Json
        
        $revertResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $revertPasswordBody
        Write-Host "Password reverted successfully" -ForegroundColor Green
        
    } catch {
        Write-Host "Password change failed: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} catch {
    Write-Host "Admin login failed: $($_.Exception.Message)" -ForegroundColor Red
}

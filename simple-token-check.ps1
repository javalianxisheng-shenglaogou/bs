# Simple JWT Token Check
$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$token = $loginResponse.data.accessToken

Write-Host "Token: $token"
Write-Host ""

# Try to access profile endpoint
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
    Write-Host "Profile access successful"
    Write-Host "User: $($profileResponse.data.username)"
    Write-Host "Roles: $($profileResponse.data.userRoles | ForEach-Object { $_.role.name } | Join-String -Separator ', ')"
} catch {
    Write-Host "Profile access failed: $($_.Exception.Message)"
}

# Try to change password
$changePasswordBody = @{
    currentPassword = "admin123"
    newPassword = "newpass123"
} | ConvertTo-Json

try {
    $passwordResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $changePasswordBody
    Write-Host "Password change successful: $($passwordResponse.message)"
} catch {
    Write-Host "Password change failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "Status Code: $statusCode"
    }
}

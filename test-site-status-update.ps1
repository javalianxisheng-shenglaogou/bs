# Test Site Status Update Function
Write-Host "Testing Site Status Update Function" -ForegroundColor Cyan

# 1. Login to get Token
Write-Host "`n1. Login to get Token..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "admin"
    password = "password"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $token = $loginResponse.data.accessToken
    Write-Host "Login successful, Token: $($token.Substring(0, 20))..." -ForegroundColor Green
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. Get Sites List
Write-Host "`n2. Get Sites List..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $sitesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites?page=1&size=10" -Method GET -Headers $headers
    $sites = $sitesResponse.data.content
    Write-Host "Found $($sites.Count) sites" -ForegroundColor Green

    if ($sites.Count -gt 0) {
        $testSite = $sites[0]
        Write-Host "Test Site Info:" -ForegroundColor Cyan
        Write-Host "   ID: $($testSite.id)" -ForegroundColor White
        Write-Host "   Name: $($testSite.name)" -ForegroundColor White
        Write-Host "   Current Status: $($testSite.status)" -ForegroundColor White
    } else {
        Write-Host "No sites found" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Failed to get sites list: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. Test Status Toggle
Write-Host "`n3. Test Site Status Toggle..." -ForegroundColor Yellow

# Determine new status
$currentStatus = $testSite.status
$newStatus = if ($currentStatus -eq "ACTIVE") { "INACTIVE" } else { "ACTIVE" }

Write-Host "Switching site status from $currentStatus to $newStatus" -ForegroundColor Cyan

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites/$($testSite.id)/status?status=$newStatus" -Method PATCH -Headers $headers
    Write-Host "Status update successful: $($updateResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "Status update failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorStream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorStream)
        $errorBody = $reader.ReadToEnd()
        Write-Host "Error details: $errorBody" -ForegroundColor Red
    }
    exit 1
}

# 4. Verify Status Update
Write-Host "`n4. Verify Status Update..." -ForegroundColor Yellow

try {
    $verifyResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites/$($testSite.id)" -Method GET -Headers $headers
    $updatedSite = $verifyResponse.data

    Write-Host "Updated Site Info:" -ForegroundColor Cyan
    Write-Host "   ID: $($updatedSite.id)" -ForegroundColor White
    Write-Host "   Name: $($updatedSite.name)" -ForegroundColor White
    Write-Host "   New Status: $($updatedSite.status)" -ForegroundColor White

    if ($updatedSite.status -eq $newStatus) {
        Write-Host "Status update verification successful!" -ForegroundColor Green
    } else {
        Write-Host "Status update verification failed! Expected: $newStatus, Actual: $($updatedSite.status)" -ForegroundColor Red
    }
} catch {
    Write-Host "Failed to verify status update: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. Restore Original Status
Write-Host "`n5. Restore Original Status..." -ForegroundColor Yellow

try {
    $restoreResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites/$($testSite.id)/status?status=$currentStatus" -Method PATCH -Headers $headers
    Write-Host "Status restore successful: $($restoreResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "Status restore failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nSite Status Update Function Test Complete!" -ForegroundColor Green

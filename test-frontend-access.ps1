# Test frontend access
Write-Host "=== Testing Frontend Access ===" -ForegroundColor Green

# Test 1: Frontend homepage
Write-Host "`n1. Testing frontend homepage..." -ForegroundColor Yellow
try {
    $frontendResponse = Invoke-WebRequest -Uri "http://localhost:3001/" -Method GET -TimeoutSec 10
    if ($frontendResponse.StatusCode -eq 200) {
        Write-Host "✅ Frontend homepage accessible" -ForegroundColor Green
        Write-Host "Status: $($frontendResponse.StatusCode)" -ForegroundColor Cyan
    } else {
        Write-Host "⚠️ Frontend returned status: $($frontendResponse.StatusCode)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Frontend homepage failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Backend API health
Write-Host "`n2. Testing backend API..." -ForegroundColor Yellow
try {
    $backendResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/actuator/health" -Method GET
    if ($backendResponse.status -eq "UP") {
        Write-Host "✅ Backend API healthy" -ForegroundColor Green
        Write-Host "Status: $($backendResponse.status)" -ForegroundColor Cyan
    } else {
        Write-Host "⚠️ Backend status: $($backendResponse.status)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Backend API failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Login functionality
Write-Host "`n3. Testing login functionality..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "admin"
    password = "password"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    if ($loginResponse.success) {
        Write-Host "✅ Login successful" -ForegroundColor Green
        Write-Host "User: $($loginResponse.data.user.username)" -ForegroundColor Cyan
        $token = $loginResponse.data.accessToken
        
        # Test 4: Protected API with token
        Write-Host "`n4. Testing protected API..." -ForegroundColor Yellow
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        
        try {
            $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
            Write-Host "✅ Protected API accessible" -ForegroundColor Green
            Write-Host "Profile: $($profileResponse.data.username) - $($profileResponse.data.email)" -ForegroundColor Cyan
        } catch {
            Write-Host "❌ Protected API failed: $($_.Exception.Message)" -ForegroundColor Red
        }
        
    } else {
        Write-Host "❌ Login failed: $($loginResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Login request failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Frontend access test completed!" -ForegroundColor Cyan

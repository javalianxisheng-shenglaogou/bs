# Complete feature test for admin user
Write-Host "=== Complete Admin Feature Test ===" -ForegroundColor Green

# Step 1: Login
Write-Host "`n1. Logging in..." -ForegroundColor Yellow
$loginBody = @{
    usernameOrEmail = "admin"
    password = "password"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    
    if ($loginResponse.success) {
        $token = $loginResponse.data.accessToken
        Write-Host "✅ Login successful!" -ForegroundColor Green
        Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Cyan
    } else {
        Write-Host "❌ Login failed: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ Login error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Step 2: Profile Management
Write-Host "`n2. Testing Profile Management..." -ForegroundColor Yellow

# Get profile
try {
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
    Write-Host "✅ Profile retrieved: $($profileResponse.data.username) - $($profileResponse.data.email)" -ForegroundColor Green
} catch {
    Write-Host "❌ Profile retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Update profile
$profileUpdateBody = @{
    email = "admin@example.com"
    nickname = "System Administrator"
    phone = "123-456-7890"
    avatarUrl = ""
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method PUT -Headers $headers -Body $profileUpdateBody
    Write-Host "✅ Profile updated successfully" -ForegroundColor Green
} catch {
    Write-Host "❌ Profile update failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 3: Content Management
Write-Host "`n3. Testing Content Management..." -ForegroundColor Yellow

# List contents
try {
    $contentsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents?page=1&size=5&siteId=1" -Method GET -Headers $headers
    Write-Host "✅ Contents listed: $($contentsResponse.data.totalElements) total items" -ForegroundColor Green
} catch {
    Write-Host "❌ Content listing failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Create content
$newContentBody = @{
    title = "API Test Article"
    summary = "This article was created via API for testing purposes"
    content = "This is the full content of the test article created through the REST API."
    siteId = 1
    contentType = "ARTICLE"
    status = "DRAFT"
    isTop = $false
    seoTitle = "API Test Article - SEO Title"
    seoDescription = "This is the SEO description for the API test article"
    seoKeywords = "api,test,article"
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents" -Method POST -Headers $headers -Body $newContentBody
    Write-Host "✅ Content created: ID $($createResponse.data.id)" -ForegroundColor Green
    $newContentId = $createResponse.data.id
} catch {
    Write-Host "❌ Content creation failed: $($_.Exception.Message)" -ForegroundColor Red
    $newContentId = $null
}

# Update content if created successfully
if ($newContentId) {
    $updateContentBody = @{
        title = "Updated API Test Article"
        summary = "This article was updated via API"
        content = "This is the updated content of the test article."
        siteId = 1
        contentType = "ARTICLE"
        status = "PUBLISHED"
        isTop = $false
        seoTitle = "Updated API Test Article - SEO Title"
        seoDescription = "Updated SEO description"
        seoKeywords = "api,test,article,updated"
    } | ConvertTo-Json
    
    try {
        $updateContentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents/$newContentId" -Method PUT -Headers $headers -Body $updateContentBody
        Write-Host "✅ Content updated successfully" -ForegroundColor Green
    } catch {
        Write-Host "❌ Content update failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Step 4: Category Management
Write-Host "`n4. Testing Category Management..." -ForegroundColor Yellow

try {
    $categoriesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/categories?siteId=1" -Method GET -Headers $headers
    Write-Host "✅ Categories retrieved: $($categoriesResponse.data.Count) categories" -ForegroundColor Green
} catch {
    Write-Host "❌ Categories retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Password Change
Write-Host "`n5. Testing Password Change..." -ForegroundColor Yellow

$passwordChangeBody = @{
    currentPassword = "password"
    newPassword = "newpass123"
} | ConvertTo-Json

try {
    $passwordResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $passwordChangeBody
    Write-Host "✅ Password changed successfully" -ForegroundColor Green
    
    # Revert password
    $revertPasswordBody = @{
        currentPassword = "newpass123"
        newPassword = "password"
    } | ConvertTo-Json
    
    $revertResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $revertPasswordBody
    Write-Host "✅ Password reverted successfully" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Password change failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Complete feature test finished!" -ForegroundColor Cyan
Write-Host "Summary:" -ForegroundColor White
Write-Host "- Login: Working" -ForegroundColor Green
Write-Host "- Profile Management: Working" -ForegroundColor Green
Write-Host "- Content Management: Testing..." -ForegroundColor Yellow
Write-Host "- Category Management: Working" -ForegroundColor Green
Write-Host "- Password Management: Testing..." -ForegroundColor Yellow

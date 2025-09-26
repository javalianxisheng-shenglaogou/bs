# Test admin user features
Write-Host "=== Testing Admin User Features ===" -ForegroundColor Green

# Login as admin
$loginBody = @{
    usernameOrEmail = "admin"
    password = "password"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$token = $loginResponse.data.accessToken

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "✅ Logged in as admin" -ForegroundColor Green

# Test 1: Get user profile
Write-Host "`n1. Testing profile access..." -ForegroundColor Yellow
try {
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method GET -Headers $headers
    Write-Host "✅ Profile: $($profileResponse.data.username) - $($profileResponse.data.email)" -ForegroundColor Green
} catch {
    Write-Host "❌ Profile access failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Update profile
Write-Host "`n2. Testing profile update..." -ForegroundColor Yellow
$profileUpdateBody = @{
    email = "admin@example.com"
    nickname = "Admin User"
    phone = "123-456-7890"
    avatarUrl = ""
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/profile" -Method PUT -Headers $headers -Body $profileUpdateBody
    Write-Host "✅ Profile updated: $($updateResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "❌ Profile update failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3: Change password
Write-Host "`n3. Testing password change..." -ForegroundColor Yellow
$passwordChangeBody = @{
    currentPassword = "password"
    newPassword = "newpass123"
} | ConvertTo-Json

try {
    $passwordResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $passwordChangeBody
    Write-Host "✅ Password changed: $($passwordResponse.message)" -ForegroundColor Green
    
    # Change back to original password
    $revertPasswordBody = @{
        currentPassword = "newpass123"
        newPassword = "password"
    } | ConvertTo-Json
    
    $revertResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users/password" -Method PUT -Headers $headers -Body $revertPasswordBody
    Write-Host "✅ Password reverted to original" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Password change failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4: Content management
Write-Host "`n4. Testing content management..." -ForegroundColor Yellow

# Get contents
try {
    $contentsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents?page=1&size=10&siteId=1" -Method GET -Headers $headers
    Write-Host "✅ Contents retrieved: $($contentsResponse.data.totalElements) items" -ForegroundColor Green
} catch {
    Write-Host "❌ Contents retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Create new content
$newContentBody = @{
    title = "Test Content from API"
    summary = "This is a test content created via API"
    content = "This is the full content body created through the REST API for testing purposes."
    categoryId = 1
    siteId = 1
    status = "PUBLISHED"
    contentType = "ARTICLE"
    isTop = $false
    seoTitle = "Test Content from API"
    seoDescription = "This is a test content created via API"
    seoKeywords = "api,test"
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents" -Method POST -Headers $headers -Body $newContentBody
    Write-Host "✅ Content created: ID $($createResponse.data.id)" -ForegroundColor Green
    $newContentId = $createResponse.data.id
    
    # Update the content
    $updateContentBody = @{
        title = "Updated Test Content from API"
        summary = "This content has been updated via API"
        content = "This is the updated content body."
        categoryId = 1
        siteId = 1
        status = "PUBLISHED"
        contentType = "ARTICLE"
        isTop = $false
        seoTitle = "Updated Test Content from API"
        seoDescription = "This content has been updated via API"
        seoKeywords = "api,test,updated"
    } | ConvertTo-Json
    
    $updateContentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents/$newContentId" -Method PUT -Headers $headers -Body $updateContentBody
    Write-Host "✅ Content updated: $($updateContentResponse.message)" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Content creation/update failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Categories
Write-Host "`n5. Testing categories..." -ForegroundColor Yellow
try {
    $categoriesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/categories?siteId=1" -Method GET -Headers $headers
    Write-Host "✅ Categories retrieved: $($categoriesResponse.data.Count) categories" -ForegroundColor Green
} catch {
    Write-Host "❌ Categories retrieval failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 Admin features testing completed!" -ForegroundColor Cyan

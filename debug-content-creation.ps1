# Debug content creation issue
Write-Host "=== Debugging Content Creation ===" -ForegroundColor Green

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

# Test different content creation approaches
Write-Host "`n1. Testing minimal content creation..." -ForegroundColor Yellow
$minimalContentBody = @{
    title = "Minimal Test Content"
    siteId = 1
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents" -Method POST -Headers $headers -Body $minimalContentBody
    Write-Host "✅ Minimal content created: ID $($createResponse.data.id)" -ForegroundColor Green
} catch {
    Write-Host "❌ Minimal content creation failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "Status Code: $statusCode" -ForegroundColor Red
        
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Response Body: $responseBody" -ForegroundColor Red
        } catch {
            Write-Host "Could not read response body" -ForegroundColor Red
        }
    }
}

# Test with full content body
Write-Host "`n2. Testing full content creation..." -ForegroundColor Yellow
$fullContentBody = @{
    title = "Full Test Content"
    summary = "This is a test content created via API"
    content = "This is the full content body."
    siteId = 1
    contentType = "ARTICLE"
    status = "DRAFT"
} | ConvertTo-Json

try {
    $createResponse2 = Invoke-RestMethod -Uri "http://localhost:8080/api/contents" -Method POST -Headers $headers -Body $fullContentBody
    Write-Host "✅ Full content created: ID $($createResponse2.data.id)" -ForegroundColor Green
} catch {
    Write-Host "❌ Full content creation failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "Status Code: $statusCode" -ForegroundColor Red
    }
}

# Check if the endpoint exists by testing other endpoints
Write-Host "`n3. Testing other endpoints..." -ForegroundColor Yellow

try {
    $contentsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents?page=1&size=5&siteId=1" -Method GET -Headers $headers
    Write-Host "✅ Contents GET works: $($contentsResponse.data.totalElements) items" -ForegroundColor Green
} catch {
    Write-Host "❌ Contents GET failed: $($_.Exception.Message)" -ForegroundColor Red
}

try {
    $categoriesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/categories?siteId=1" -Method GET -Headers $headers
    Write-Host "✅ Categories GET works: $($categoriesResponse.data.Count) categories" -ForegroundColor Green
} catch {
    Write-Host "❌ Categories GET failed: $($_.Exception.Message)" -ForegroundColor Red
}

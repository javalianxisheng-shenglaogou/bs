# Test Independent Site Creation (without parent site)
Write-Host "Testing Independent Site Creation" -ForegroundColor Cyan

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

# 2. Create Independent Site (without parent)
Write-Host "`n2. Create Independent Site..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$siteData = @{
    name = "Independent Test Site"
    code = "independent_test"
    domain = "independent.test.com"
    description = "This is an independent site without parent"
    # parentSiteId is intentionally omitted
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites" -Method POST -Body $siteData -Headers $headers
    $newSite = $createResponse.data
    Write-Host "Site created successfully!" -ForegroundColor Green
    Write-Host "   ID: $($newSite.id)" -ForegroundColor White
    Write-Host "   Name: $($newSite.name)" -ForegroundColor White
    Write-Host "   Code: $($newSite.code)" -ForegroundColor White
    Write-Host "   Domain: $($newSite.domain)" -ForegroundColor White
    Write-Host "   Parent Site: $($newSite.parentSite)" -ForegroundColor White
} catch {
    Write-Host "Site creation failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorStream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorStream)
        $errorBody = $reader.ReadToEnd()
        Write-Host "Error details: $errorBody" -ForegroundColor Red
    }
    exit 1
}

# 3. Verify Site Creation
Write-Host "`n3. Verify Site Creation..." -ForegroundColor Yellow

try {
    $verifyResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites/$($newSite.id)" -Method GET -Headers $headers
    $verifiedSite = $verifyResponse.data
    
    Write-Host "Site verification successful:" -ForegroundColor Green
    Write-Host "   ID: $($verifiedSite.id)" -ForegroundColor White
    Write-Host "   Name: $($verifiedSite.name)" -ForegroundColor White
    Write-Host "   Code: $($verifiedSite.code)" -ForegroundColor White
    Write-Host "   Domain: $($verifiedSite.domain)" -ForegroundColor White
    Write-Host "   Description: $($verifiedSite.description)" -ForegroundColor White
    Write-Host "   Parent Site: $($verifiedSite.parentSite)" -ForegroundColor White
    Write-Host "   Status: $($verifiedSite.status)" -ForegroundColor White
    
    if ($verifiedSite.parentSite -eq $null) {
        Write-Host "Confirmed: This is an independent site (no parent)" -ForegroundColor Green
    } else {
        Write-Host "Warning: Site has a parent site: $($verifiedSite.parentSite.name)" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Site verification failed: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Clean up - Delete the test site
Write-Host "`n4. Clean up - Delete test site..." -ForegroundColor Yellow

try {
    $deleteResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites/$($newSite.id)" -Method DELETE -Headers $headers
    Write-Host "Test site deleted successfully: $($deleteResponse.message)" -ForegroundColor Green
} catch {
    Write-Host "Failed to delete test site: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please manually delete site ID: $($newSite.id)" -ForegroundColor Yellow
}

Write-Host "`nIndependent Site Creation Test Complete!" -ForegroundColor Green

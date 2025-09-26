# PowerShell script to debug category fetching

# API Base URL
$apiBaseUrl = "http://localhost:8080/api"

# Admin credentials
$adminUsername = "admin"
$adminPassword = "password"

# Login to get JWT token
$loginBody = @{
    usernameOrEmail = $adminUsername
    password = $adminPassword
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "$apiBaseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $loginResponseContent = $loginResponse.Content | ConvertFrom-Json
    $token = $loginResponseContent.data.accessToken
    Write-Host "Successfully logged in as ${adminUsername}"
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "Failed to log in: ${errorMessage}"
    if ($_.Exception.Response) {
        $responseBody = $_.Exception.Response.Content.ReadAsStringAsync().Result
        Write-Host "Response body: $responseBody"
    }
    exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "Attempting to get category list..."
try {
    $categoriesResponse = Invoke-WebRequest -Uri "$apiBaseUrl/categories?page=1&size=100" -Method GET -Headers $headers -SkipHttpErrorCheck
    if ($categoriesResponse.StatusCode -ne 200) {
        Write-Host "Failed to get category list. Status code: $($categoriesResponse.StatusCode)"
        Write-Host "Response body: $($categoriesResponse.Content)"
    } else {
        $categoriesResponseContent = $categoriesResponse.Content | ConvertFrom-Json
        Write-Host "Successfully retrieved category list."
        Write-Host ($categoriesResponseContent | ConvertTo-Json -Depth 10)
    }
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "An unexpected error occurred: ${errorMessage}"
}
# PowerShell script to debug site ID retrieval

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
    $loginResponse = Invoke-WebRequest -Uri "$apiBaseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -SkipHttpErrorCheck
    if ($loginResponse.StatusCode -ne 200) {
        Write-Host "Failed to log in. Status code: $($loginResponse.StatusCode)"
        Write-Host "Response body: $($loginResponse.Content)"
        exit
    }
    $loginResponseContent = $loginResponse.Content | ConvertFrom-Json
    $token = $loginResponseContent.data.accessToken
    Write-Host "Successfully logged in as ${adminUsername}"
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "An unexpected error occurred during login: ${errorMessage}"
    exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# Add a new site and capture it
$siteToAdd = @{ name = "调试站点"; code = "debug_site"; domain = "debug.example.com" }
$createdSites = @()
$siteBody = $siteToAdd | ConvertTo-Json

try {
    $newSiteResponse = Invoke-WebRequest -Uri "$apiBaseUrl/sites" -Method POST -Headers $headers -Body $siteBody -SkipHttpErrorCheck
    if ($newSiteResponse.StatusCode -ne 200) {
        Write-Host "Failed to create site $($siteToAdd.name). Status code: $($newSiteResponse.StatusCode)"
        Write-Host "Response body: $($newSiteResponse.Content)"
    } else {
        $newSite = $newSiteResponse.Content | ConvertFrom-Json
        Write-Host "Site creation response: $($newSiteResponse.Content)"
        
        $createdSites += $newSite.data
        Write-Host "Added site data to array."

        $retrievedSite = $createdSites[0]
        Write-Host "Retrieved site object from array: $($retrievedSite | ConvertTo-Json -Depth 5)"
        Write-Host "Retrieved site ID from array: $($retrievedSite.id)"

        if ($null -eq $retrievedSite.id) {
            Write-Host "Error: Site ID is null after retrieval from array."
        } else {
            Write-Host "Success: Site ID is not null."
        }
    }
} catch {
    $errorMessage = $_.Exception.Message
    Write-Host "An unexpected error occurred while creating site $($site.name): ${errorMessage}"
}
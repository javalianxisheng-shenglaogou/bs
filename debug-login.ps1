# Debug login response
$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "Login request body: $loginBody"

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "Login response:"
    $loginResponse | ConvertTo-Json -Depth 10
    
    if ($loginResponse.data) {
        Write-Host "Data object exists"
        if ($loginResponse.data.accessToken) {
            Write-Host "Access token: $($loginResponse.data.accessToken)"
        } else {
            Write-Host "No accessToken in data"
            Write-Host "Data properties: $($loginResponse.data | Get-Member -MemberType Properties | Select-Object Name)"
        }
    } else {
        Write-Host "No data object in response"
    }
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody"
    }
}

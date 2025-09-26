# Check JWT Token
Write-Host "=== JWT Token Analysis ===" -ForegroundColor Green

$loginBody = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.data.accessToken
    Write-Host "Login successful" -ForegroundColor Green

    $tokenParts = $token.Split('.')
    $payload = $tokenParts[1]

    $padding = 4 - ($payload.Length % 4)
    if ($padding -ne 4) {
        $payload += "=" * $padding
    }

    $decodedBytes = [System.Convert]::FromBase64String($payload)
    $decodedString = [System.Text.Encoding]::UTF8.GetString($decodedBytes)

    Write-Host "`nJWT Payload:" -ForegroundColor Yellow
    Write-Host $decodedString -ForegroundColor White

    $payloadJson = $decodedString | ConvertFrom-Json
    Write-Host "`nUser Info:" -ForegroundColor Yellow
    Write-Host "User ID: $($payloadJson.userId)"
    Write-Host "Username: $($payloadJson.username)"
    Write-Host "Email: $($payloadJson.email)"
    Write-Host "Authorities: $($payloadJson.authorities -join ', ')"

} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

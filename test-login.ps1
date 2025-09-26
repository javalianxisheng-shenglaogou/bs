$body = @{
    usernameOrEmail = "superadmin"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $body
    Write-Host "登录成功:"
    $token = $response.data.accessToken
    Write-Host "Token: $token"

    # 测试获取站点列表
    Write-Host "`n测试获取站点列表:"
    try {
        $headers = @{
            "Authorization" = "Bearer $token"
            "Content-Type" = "application/json"
        }
        $sitesResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/sites?page=1&size=10" -Method GET -Headers $headers
        Write-Host "站点数量: $($sitesResponse.data.totalElements)"

        if ($sitesResponse.data.totalElements -gt 0) {
            $siteId = $sitesResponse.data.content[0].id
            Write-Host "测试站点ID: $siteId"

            # 测试获取内容统计
            Write-Host "`n测试获取内容统计:"
            try {
                $contentStatsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents/stats?siteId=$siteId" -Method GET -Headers $headers
                Write-Host "内容统计:"
                $contentStatsResponse | ConvertTo-Json -Depth 10
            } catch {
                Write-Host "获取内容统计失败:"
                Write-Host $_.Exception.Message
            }

            # 测试获取内容列表
            Write-Host "`n测试获取内容列表:"
            try {
                $contentsResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/contents?page=1&size=20&siteId=$siteId" -Method GET -Headers $headers
                Write-Host "内容列表获取成功，共 $($contentsResponse.data.totalElements) 条内容:"
                foreach ($content in $contentsResponse.data.content) {
                    Write-Host "- $($content.title) (ID: $($content.id), 状态: $($content.status))"
                }
            } catch {
                Write-Host "获取内容列表失败: $($_.Exception.Message)"
            }
        }
    } catch {
        Write-Host "获取站点列表失败:"
        Write-Host $_.Exception.Message
    }

} catch {
    Write-Host "登录失败:"
    Write-Host $_.Exception.Message
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "响应内容: $responseBody"
    }
}

# 检查数据库中的密码哈希
Write-Host "=== 检查数据库中的用户密码 ===" -ForegroundColor Green

# 尝试不同的MySQL路径
$possiblePaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\wamp64\bin\mysql\mysql8.0.31\bin\mysql.exe"
)

$found = $false
foreach ($path in $possiblePaths) {
    if (Test-Path $path) {
        Write-Host "找到MySQL: $path" -ForegroundColor Green
        try {
            & $path -h localhost -P 3306 -u root -p123456 multi_site_cms -e "SELECT id, username, password_hash FROM users WHERE username = 'superadmin';"
            $found = $true
            break
        } catch {
            Write-Host "连接失败: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
}

if (-not $found) {
    Write-Host "未找到MySQL命令行工具或连接失败" -ForegroundColor Red
    Write-Host "请手动检查数据库中superadmin用户的password_hash字段" -ForegroundColor Yellow
}

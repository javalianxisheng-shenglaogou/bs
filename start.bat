@echo off
echo ========================================
echo 基于Spring Boot的多站点内容管理系统
echo ========================================
echo.

echo 正在启动应用...
echo.

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请确保已安装JDK 17或更高版本
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请确保已安装Maven 3.8+
    pause
    exit /b 1
)

echo Java和Maven环境检查通过
echo.

echo 正在编译和启动应用...
echo 请稍候，首次启动可能需要下载依赖包...
echo.

REM 启动Spring Boot应用
mvn spring-boot:run

echo.
echo 应用已停止
pause

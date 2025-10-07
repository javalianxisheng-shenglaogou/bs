# 多站点CMS系统 - 部署指南

## 📋 环境要求

### 必需软件
- **JDK**: 11 或更高版本
- **Node.js**: 18 或更高版本
- **MySQL**: 8.0 或更高版本
- **Maven**: 3.6 或更高版本

### 推荐配置
- **内存**: 4GB 或更高
- **磁盘空间**: 10GB 或更高
- **操作系统**: Windows 10/11, macOS, Linux

---

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://gitee.com/jiuxias-da/multi-site-hub.git
cd multi-site-hub
```

### 2. 数据库配置

#### 2.1 创建数据库

```bash
# 使用提供的脚本
mysql -uroot -p < database-setup.sql
```

或手动创建：

```sql
CREATE DATABASE multi_site_cms 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

#### 2.2 配置数据库连接

复制配置文件示例：

```bash
cp backend/src/main/resources/application-dev.yml.example backend/src/main/resources/application-dev.yml
```

编辑 `backend/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/multi_site_cms?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password_here  # 修改为你的MySQL密码
```

**重要**：请修改以下配置：
- `password`: 你的MySQL密码
- `jwt.secret`: 生产环境请使用强密钥（至少256位）

### 3. 启动后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

#### 验证后端服务

访问以下URL验证服务是否正常：
- 健康检查：http://localhost:8080/api/test/hello
- API文档：http://localhost:8080/api/swagger-ui.html

### 4. 启动前端服务

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:3000` 启动。

### 5. 登录系统

打开浏览器访问：http://localhost:3000

使用默认管理员账号登录：
- **用户名**: `admin`
- **密码**: `password`

---

## 🔧 配置说明

### 环境变量配置

项目支持使用环境变量配置敏感信息。

#### 创建 .env 文件

```bash
cp .env.example .env
```

#### 编辑 .env 文件

```bash
# 数据库配置
DB_USERNAME=root
DB_PASSWORD=your_password_here

# JWT配置
JWT_SECRET=your-secret-key-change-this-in-production-environment-must-be-at-least-256-bits

# 文件上传配置
UPLOAD_PATH=../uploads
UPLOAD_BASE_URL=http://localhost:8080/api/files

# 前端配置
VITE_API_BASE_URL=http://localhost:8080/api
```

### 后端配置文件

#### application.yml（主配置）
- 通用配置
- JPA配置
- Jackson配置
- 文件上传配置

#### application-dev.yml（开发环境）
- 数据库连接
- Flyway配置
- JWT配置
- 文件上传路径

### 前端配置文件

#### vite.config.ts
- 开发服务器配置
- 代理配置
- 构建配置

#### .env.development（开发环境）
```
VITE_API_BASE_URL=http://localhost:8080/api
```

#### .env.production（生产环境）
```
VITE_API_BASE_URL=https://your-domain.com/api
```

---

## 📦 生产环境部署

### 后端部署

#### 1. 打包应用

```bash
cd backend
mvn clean package -DskipTests
```

生成的JAR文件位于：`backend/target/cms-backend-1.0.0.jar`

#### 2. 运行应用

```bash
java -jar backend/target/cms-backend-1.0.0.jar
```

#### 3. 使用环境变量

```bash
java -jar \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=your_password \
  -Djwt.secret=your_secret_key \
  backend/target/cms-backend-1.0.0.jar
```

#### 4. 使用systemd（Linux）

创建服务文件 `/etc/systemd/system/cms-backend.service`：

```ini
[Unit]
Description=Multi-Site CMS Backend
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/cms/backend
ExecStart=/usr/bin/java -jar /opt/cms/backend/cms-backend-1.0.0.jar
Restart=on-failure
RestartSec=10

Environment="SPRING_DATASOURCE_USERNAME=root"
Environment="SPRING_DATASOURCE_PASSWORD=your_password"
Environment="JWT_SECRET=your_secret_key"

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start cms-backend
sudo systemctl enable cms-backend
sudo systemctl status cms-backend
```

### 前端部署

#### 1. 构建应用

```bash
cd frontend
npm run build
```

生成的文件位于：`frontend/dist/`

#### 2. 使用Nginx部署

安装Nginx：

```bash
# Ubuntu/Debian
sudo apt install nginx

# CentOS/RHEL
sudo yum install nginx
```

配置Nginx `/etc/nginx/sites-available/cms-frontend`：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/cms/frontend/dist;
    index index.html;
    
    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 静态文件缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

启用站点：

```bash
sudo ln -s /etc/nginx/sites-available/cms-frontend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### 3. 使用HTTPS（推荐）

安装Let's Encrypt证书：

```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

---

## 🔒 安全建议

### 1. 修改默认密码

**数据库密码**：
- 不要使用 `123456` 等弱密码
- 使用强密码（至少12位，包含大小写字母、数字、特殊字符）

**JWT密钥**：
- 生成随机密钥：
  ```bash
  openssl rand -base64 64
  ```
- 至少256位

**管理员密码**：
- 首次登录后立即修改默认密码
- 使用强密码

### 2. 数据库安全

- 不要使用root用户连接数据库
- 创建专用数据库用户：
  ```sql
  CREATE USER 'cms_user'@'localhost' IDENTIFIED BY 'strong_password';
  GRANT ALL PRIVILEGES ON multi_site_cms.* TO 'cms_user'@'localhost';
  FLUSH PRIVILEGES;
  ```

### 3. 防火墙配置

只开放必要的端口：
- 80 (HTTP)
- 443 (HTTPS)
- 不要直接暴露8080端口（后端）
- 不要直接暴露3306端口（MySQL）

### 4. 文件上传安全

- 限制文件类型
- 限制文件大小
- 扫描上传文件
- 使用独立的文件存储服务

---

## 📊 监控和日志

### 后端日志

日志文件位置：`backend/logs/`

- `info.log` - 信息日志（保留30天）
- `error.log` - 错误日志（保留30天）
- `debug.log` - 调试日志（保留7天）
- `sql.log` - SQL日志（保留7天）

### 查看日志

```bash
# 实时查看日志
tail -f backend/logs/info.log

# 查看错误日志
tail -f backend/logs/error.log

# 搜索日志
grep "ERROR" backend/logs/info.log
```

### 系统监控

推荐使用以下工具：
- **Spring Boot Actuator** - 应用监控
- **Prometheus + Grafana** - 指标监控
- **ELK Stack** - 日志分析

---

## 🔄 数据备份

### 数据库备份

#### 手动备份

```bash
mysqldump -u root -p multi_site_cms > backup_$(date +%Y%m%d_%H%M%S).sql
```

#### 自动备份（cron）

创建备份脚本 `/opt/cms/backup.sh`：

```bash
#!/bin/bash
BACKUP_DIR="/opt/cms/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p'your_password' multi_site_cms > $BACKUP_DIR/backup_$DATE.sql
# 删除30天前的备份
find $BACKUP_DIR -name "backup_*.sql" -mtime +30 -delete
```

添加到crontab：

```bash
# 每天凌晨2点备份
0 2 * * * /opt/cms/backup.sh
```

### 文件备份

备份上传文件目录：

```bash
tar -czf uploads_backup_$(date +%Y%m%d).tar.gz uploads/
```

---

## 🐛 故障排查

### 后端无法启动

1. 检查Java版本：`java -version`
2. 检查MySQL是否运行：`systemctl status mysql`
3. 检查数据库连接配置
4. 查看日志：`tail -f backend/logs/error.log`

### 前端无法访问

1. 检查Nginx状态：`systemctl status nginx`
2. 检查Nginx配置：`nginx -t`
3. 检查前端构建是否成功
4. 查看Nginx日志：`tail -f /var/log/nginx/error.log`

### 数据库连接失败

1. 检查MySQL是否运行
2. 检查用户名和密码
3. 检查数据库是否存在
4. 检查防火墙规则

---

## 📞 获取帮助

如果遇到问题，请：
1. 查看项目文档
2. 查看日志文件
3. 搜索已知问题
4. 提交Issue

---

**祝您部署顺利！** 🎉


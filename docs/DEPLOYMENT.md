# 部署文档

## 环境准备

### 服务器要求

- **操作系统**: Linux (推荐 Ubuntu 20.04+ 或 CentOS 7+)
- **CPU**: 2核心以上
- **内存**: 4GB以上
- **硬盘**: 20GB以上

### 软件要求

- JDK 11+
- MySQL 8.0+
- Nginx (可选,用于反向代理)

## 数据库部署

### 1. 安装MySQL

```bash
# Ubuntu
sudo apt update
sudo apt install mysql-server

# CentOS
sudo yum install mysql-server
```

### 2. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/database/init_database.sql
```

### 3. 创建数据库用户

```sql
CREATE USER 'cms_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON multi_site_cms.* TO 'cms_user'@'localhost';
FLUSH PRIVILEGES;
```

## 后端部署

### 1. 构建JAR包

```bash
cd backend
mvn clean package -DskipTests
```

生成的JAR包位于: `backend/target/multi-site-cms-1.0.0.jar`

### 2. 配置文件

创建生产环境配置文件 `application-prod.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/multi_site_cms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: cms_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
  
  flyway:
    enabled: true
    baseline-on-migrate: true

jwt:
  secret: your_jwt_secret_key_change_this_in_production
  expiration: 86400000

logging:
  level:
    root: INFO
    com.cms: INFO
  file:
    name: logs/multi-site-cms.log
```

### 3. 创建systemd服务

创建文件 `/etc/systemd/system/cms.service`:

```ini
[Unit]
Description=Multi-Site CMS Application
After=syslog.target network.target

[Service]
User=www-data
Group=www-data
WorkingDirectory=/opt/cms
ExecStart=/usr/bin/java -jar /opt/cms/multi-site-cms-1.0.0.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 4. 启动服务

```bash
# 重新加载systemd配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start cms

# 设置开机自启
sudo systemctl enable cms

# 查看服务状态
sudo systemctl status cms

# 查看日志
sudo journalctl -u cms -f
```

## 前端部署

### 1. 构建前端

```bash
cd frontend
npm install
npm run build
```

构建产物位于: `frontend/dist/`

### 2. 配置Nginx

创建Nginx配置文件 `/etc/nginx/sites-available/cms`:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/cms/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 文件上传
    location /files {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 日志
    access_log /var/log/nginx/cms_access.log;
    error_log /var/log/nginx/cms_error.log;
}
```

### 3. 启用站点

```bash
# 创建软链接
sudo ln -s /etc/nginx/sites-available/cms /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
```

## SSL证书配置 (可选)

### 使用Let's Encrypt

```bash
# 安装certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

## 监控和维护

### 1. 日志管理

```bash
# 查看应用日志
tail -f /opt/cms/logs/multi-site-cms.log

# 查看Nginx访问日志
tail -f /var/log/nginx/cms_access.log

# 查看Nginx错误日志
tail -f /var/log/nginx/cms_error.log
```

### 2. 数据库备份

创建备份脚本 `/opt/cms/backup.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/opt/cms/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/cms_backup_$DATE.sql"

mkdir -p $BACKUP_DIR

mysqldump -u cms_user -p'your_password' multi_site_cms > $BACKUP_FILE

# 压缩备份文件
gzip $BACKUP_FILE

# 删除7天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_FILE.gz"
```

添加到crontab:

```bash
# 每天凌晨2点执行备份
0 2 * * * /opt/cms/backup.sh
```

### 3. 性能监控

使用工具:
- **应用监控**: Spring Boot Actuator
- **服务器监控**: htop, iotop
- **数据库监控**: MySQL Workbench, Percona Monitoring

## 故障排查

### 1. 应用无法启动

```bash
# 检查Java版本
java -version

# 检查端口占用
sudo netstat -tulpn | grep 8080

# 查看详细日志
sudo journalctl -u cms -n 100
```

### 2. 数据库连接失败

```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 测试数据库连接
mysql -u cms_user -p -h localhost multi_site_cms
```

### 3. Nginx 502错误

```bash
# 检查后端服务是否运行
sudo systemctl status cms

# 检查Nginx配置
sudo nginx -t

# 查看Nginx错误日志
sudo tail -f /var/log/nginx/error.log
```

## 更新部署

### 1. 备份数据

```bash
# 备份数据库
mysqldump -u cms_user -p multi_site_cms > backup_before_update.sql

# 备份应用文件
cp /opt/cms/multi-site-cms-1.0.0.jar /opt/cms/backup/
```

### 2. 更新应用

```bash
# 停止服务
sudo systemctl stop cms

# 替换JAR包
sudo cp new-version.jar /opt/cms/multi-site-cms-1.0.0.jar

# 启动服务
sudo systemctl start cms

# 检查状态
sudo systemctl status cms
```

### 3. 更新前端

```bash
# 备份旧版本
sudo mv /var/www/cms/dist /var/www/cms/dist.backup

# 部署新版本
sudo cp -r frontend/dist /var/www/cms/

# 重启Nginx
sudo systemctl restart nginx
```

## 安全建议

1. **修改默认密码**: 首次部署后立即修改admin密码
2. **使用HTTPS**: 配置SSL证书,强制使用HTTPS
3. **防火墙配置**: 只开放必要的端口(80, 443)
4. **定期更新**: 及时更新系统和依赖包
5. **备份策略**: 定期备份数据库和文件
6. **日志审计**: 定期检查系统日志
7. **限制访问**: 使用IP白名单限制管理后台访问

## 性能优化

1. **数据库优化**:
   - 添加适当的索引
   - 定期优化表
   - 配置查询缓存

2. **应用优化**:
   - 启用JVM参数优化
   - 配置连接池
   - 使用Redis缓存

3. **Nginx优化**:
   - 启用gzip压缩
   - 配置静态文件缓存
   - 调整worker进程数

## 联系支持

如有问题,请联系:
- 邮箱: support@example.com
- 问题反馈: https://github.com/yourusername/multi-site-cms/issues


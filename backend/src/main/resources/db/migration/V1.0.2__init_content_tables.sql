-- =============================================
-- 内容管理模块表结构
-- =============================================

-- 1. 分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    site_id BIGINT NOT NULL COMMENT '站点ID',
    
    -- 基本信息
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    slug VARCHAR(100) NOT NULL COMMENT 'URL别名',
    description TEXT COMMENT '分类描述',
    
    -- 树形结构
    parent_id BIGINT COMMENT '父分类ID',
    level INT DEFAULT 0 COMMENT '层级',
    path VARCHAR(500) COMMENT '路径（如：/1/2/3）',
    
    -- 分类属性
    icon VARCHAR(100) COMMENT '图标',
    cover_image VARCHAR(500) COMMENT '封面图',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    -- SEO
    seo_title VARCHAR(200) COMMENT 'SEO标题',
    seo_keywords VARCHAR(500) COMMENT 'SEO关键词',
    seo_description TEXT COMMENT 'SEO描述',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_site_slug (site_id, slug),
    INDEX idx_site_id (site_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_path (path(255)),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 2. 标签表
CREATE TABLE tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    site_id BIGINT NOT NULL COMMENT '站点ID',
    
    -- 基本信息
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    slug VARCHAR(50) NOT NULL COMMENT 'URL别名',
    description VARCHAR(500) COMMENT '标签描述',
    
    -- 标签属性
    color VARCHAR(20) COMMENT '标签颜色',
    icon VARCHAR(100) COMMENT '图标',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_site_slug (site_id, slug),
    INDEX idx_site_id (site_id),
    INDEX idx_name (name),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 3. 内容表
CREATE TABLE contents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '内容ID',
    site_id BIGINT NOT NULL COMMENT '站点ID',
    category_id BIGINT COMMENT '分类ID',
    
    -- 基本信息
    title VARCHAR(200) NOT NULL COMMENT '标题',
    slug VARCHAR(200) NOT NULL COMMENT 'URL别名',
    summary TEXT COMMENT '摘要',
    content LONGTEXT COMMENT '内容',
    
    -- 内容类型
    content_type ENUM('ARTICLE', 'PAGE', 'NEWS', 'PRODUCT', 'CUSTOM') DEFAULT 'ARTICLE' COMMENT '内容类型',
    template VARCHAR(100) COMMENT '模板名称',
    
    -- 媒体
    cover_image VARCHAR(500) COMMENT '封面图',
    thumbnail VARCHAR(500) COMMENT '缩略图',
    video_url VARCHAR(500) COMMENT '视频URL',
    audio_url VARCHAR(500) COMMENT '音频URL',
    attachments JSON COMMENT '附件列表',
    
    -- 作者信息
    author_id BIGINT NOT NULL COMMENT '作者ID',
    author_name VARCHAR(50) COMMENT '作者名称',
    
    -- 发布信息
    status ENUM('DRAFT', 'PENDING', 'PUBLISHED', 'SCHEDULED', 'ARCHIVED') DEFAULT 'DRAFT' COMMENT '状态',
    published_at DATETIME COMMENT '发布时间',
    scheduled_at DATETIME COMMENT '定时发布时间',
    
    -- 统计信息
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    comment_count INT DEFAULT 0 COMMENT '评论次数',
    share_count INT DEFAULT 0 COMMENT '分享次数',
    
    -- 内容属性
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_featured TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    is_original TINYINT(1) DEFAULT 1 COMMENT '是否原创',
    source_name VARCHAR(100) COMMENT '来源名称',
    source_url VARCHAR(500) COMMENT '来源URL',
    
    -- SEO
    seo_title VARCHAR(200) COMMENT 'SEO标题',
    seo_keywords VARCHAR(500) COMMENT 'SEO关键词',
    seo_description TEXT COMMENT 'SEO描述',
    
    -- 扩展字段
    extra_data JSON COMMENT '扩展数据',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_site_slug (site_id, slug),
    INDEX idx_site_id (site_id),
    INDEX idx_category_id (category_id),
    INDEX idx_author_id (author_id),
    INDEX idx_status (status),
    INDEX idx_published_at (published_at),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted),
    FULLTEXT INDEX ft_title_content (title, content)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';

-- 4. 内容标签关联表
CREATE TABLE content_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_content_tag (content_id, tag_id),
    INDEX idx_content_id (content_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容标签关联表';

-- 5. 内容国际化表
CREATE TABLE content_i18n (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    language VARCHAR(10) NOT NULL COMMENT '语言代码',
    
    -- 翻译内容
    title VARCHAR(200) NOT NULL COMMENT '标题',
    slug VARCHAR(200) NOT NULL COMMENT 'URL别名',
    summary TEXT COMMENT '摘要',
    content LONGTEXT COMMENT '内容',
    
    -- 翻译信息
    translator_id BIGINT COMMENT '翻译者ID',
    translation_status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'REVIEWED') DEFAULT 'PENDING' COMMENT '翻译状态',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_content_language (content_id, language),
    INDEX idx_content_id (content_id),
    INDEX idx_language (language),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容国际化表';

-- 6. 内容版本表
CREATE TABLE content_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '版本ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    
    -- 版本信息
    version_number INT NOT NULL COMMENT '版本号',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content LONGTEXT COMMENT '内容',
    
    -- 变更信息
    change_summary VARCHAR(500) COMMENT '变更摘要',
    changed_by BIGINT NOT NULL COMMENT '变更人ID',
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    
    -- 完整快照
    snapshot JSON COMMENT '完整数据快照',
    
    INDEX idx_content_id (content_id),
    INDEX idx_version_number (version_number),
    INDEX idx_changed_at (changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容版本表';

-- 7. 内容共享表
CREATE TABLE content_shares (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    source_site_id BIGINT NOT NULL COMMENT '源站点ID',
    target_site_id BIGINT NOT NULL COMMENT '目标站点ID',
    
    -- 共享配置
    share_type ENUM('REFERENCE', 'COPY', 'SYNC') DEFAULT 'REFERENCE' COMMENT '共享类型',
    is_auto_sync TINYINT(1) DEFAULT 0 COMMENT '是否自动同步',
    
    -- 共享信息
    shared_by BIGINT NOT NULL COMMENT '共享人ID',
    shared_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '共享时间',
    last_synced_at DATETIME COMMENT '最后同步时间',
    
    -- 审计字段
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    
    UNIQUE KEY uk_content_target_site (content_id, target_site_id),
    INDEX idx_content_id (content_id),
    INDEX idx_source_site_id (source_site_id),
    INDEX idx_target_site_id (target_site_id),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容共享表';


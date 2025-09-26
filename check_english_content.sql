-- 查看当前数据库中的英文内容
USE multi_site_cms;

-- 查看所有内容的标题和摘要
SELECT id, title, summary, content_type, status, site_id 
FROM contents 
WHERE title LIKE '%Contact%' 
   OR title LIKE '%About%' 
   OR title LIKE '%React%' 
   OR title LIKE '%Node%' 
   OR title LIKE '%Docker%' 
   OR title LIKE '%TypeScript%'
ORDER BY id;

-- 查看具体的英文内容
SELECT id, title, LEFT(content, 100) as content_preview
FROM contents 
WHERE title IN (
    'Contact Us', 
    'About Us', 
    'React Hooks Deep Dive', 
    'Node.js Performance Optimization', 
    'Docker Containerization Guide', 
    'TypeScript Advanced Type System'
)
ORDER BY id;
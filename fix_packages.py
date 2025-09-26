#!/usr/bin/env python3
import os
import re

def fix_jakarta_to_javax(file_path):
    """修复Jakarta包名为Javax包名"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 替换包名
    replacements = [
        ('jakarta.persistence', 'javax.persistence'),
        ('jakarta.validation', 'javax.validation'),
        ('jakarta.servlet', 'javax.servlet'),
    ]
    
    original_content = content
    for old, new in replacements:
        content = content.replace(old, new)
    
    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Fixed: {file_path}")
        return True
    return False

def process_directory(directory):
    """处理目录中的所有Java文件"""
    fixed_count = 0
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                if fix_jakarta_to_javax(file_path):
                    fixed_count += 1
    return fixed_count

# 修复所有Java文件
src_dir = "src/main/java"
if os.path.exists(src_dir):
    fixed_count = process_directory(src_dir)
    print(f"Total files fixed: {fixed_count}")
else:
    print(f"Directory {src_dir} not found")

#!/usr/bin/env python3
import os
import re

def fix_switch_expressions(file_path):
    """修复Java 11不支持的switch表达式"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 查找switch表达式模式
    pattern = r'return switch \(([^)]+)\) \{([^}]+)\};'
    
    def replace_switch(match):
        var = match.group(1)
        cases = match.group(2)
        
        # 解析case语句
        case_lines = []
        for line in cases.strip().split('\n'):
            line = line.strip()
            if line.startswith('case ') and ' -> ' in line:
                parts = line.split(' -> ')
                case_part = parts[0].replace('case ', '').strip()
                value_part = parts[1].rstrip(';').strip()
                case_lines.append(f'            case {case_part}:\n                return {value_part};')
        
        # 构建新的switch语句
        result = f'switch ({var}) {{\n'
        result += '\n'.join(case_lines)
        result += '\n            default:\n                return "unknown";\n        }'
        
        return result
    
    # 替换所有switch表达式
    new_content = re.sub(pattern, replace_switch, content, flags=re.DOTALL)
    
    if new_content != content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed: {file_path}")
        return True
    return False

# 修复所有枚举文件
enum_dir = "src/main/java/com/multisite/cms/enums"
for filename in os.listdir(enum_dir):
    if filename.endswith('.java'):
        file_path = os.path.join(enum_dir, filename)
        fix_switch_expressions(file_path)

print("Switch expression fixes completed!")

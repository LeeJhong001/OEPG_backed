-- 数据库表更新脚本
-- 为现有的表添加缺失的字段

-- 为 question_categories 表添加 enabled 和 updated_at 字段
ALTER TABLE question_categories 
ADD COLUMN IF NOT EXISTS enabled BOOLEAN DEFAULT TRUE COMMENT '分类是否启用',
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '分类更新时间';

-- 为 users 表添加 phone 字段（如果还没有的话）
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20) COMMENT '用户手机号码';

-- 更新现有分类的 enabled 字段为 true
UPDATE question_categories SET enabled = TRUE WHERE enabled IS NULL;

-- 更新现有分类的 updated_at 字段
UPDATE question_categories SET updated_at = created_at WHERE updated_at IS NULL; 
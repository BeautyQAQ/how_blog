-- 已有数据库执行一次；先检查 information_schema.columns，已有新字段时不要重复执行。
-- 保留正文、状态、计数和创建/更新时间，仅新增格式与摘要，并扩大正文容量。
USE `how-blog`;
ALTER TABLE `tb_article`
  MODIFY COLUMN `content` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章正文',
  ADD COLUMN `contentformat` varchar(16) NOT NULL DEFAULT 'text' COMMENT 'text或markdown' AFTER `content`,
  ADD COLUMN `summary` varchar(300) NULL DEFAULT NULL COMMENT '手工摘要，空则自动生成' AFTER `contentformat`,
  ADD COLUMN `excerpt` varchar(300) NULL DEFAULT NULL COMMENT '服务端生成摘要' AFTER `summary`;

-- 历史文章均保留为纯文本；只回填新摘要字段。
UPDATE `tb_article` SET `excerpt` = LEFT(TRIM(REPLACE(REPLACE(REPLACE(`content`, CHAR(13), ' '), CHAR(10), ' '), CHAR(9), ' ')), 180)
WHERE `excerpt` IS NULL;

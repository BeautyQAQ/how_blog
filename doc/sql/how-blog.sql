-- how-blog 数据库初始化脚本
-- 用于新建数据库并初始化表结构，不会修改或重建已有表。
-- 仅包含建库建表语句，不包含测试数据；已有表的结构调整需另行迁移。
-- 数据库名含连字符，SQL 中使用反引号引用。
-- 共 16 张表；文章评论与吐槽使用 tb_comment、tb_spit 存储。

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS `how-blog`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `how-blog`;

-- tb_article
CREATE TABLE IF NOT EXISTS `tb_article`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `columnid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专栏ID',
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章正文',
  `image` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文章封面',
  `createtime` datetime NULL DEFAULT NULL COMMENT '发表日期',
  `updatetime` datetime NULL DEFAULT NULL COMMENT '修改日期',
  `ispublic` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否公开',
  `istop` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否置顶',
  `visits` int NULL DEFAULT NULL COMMENT '浏览量',
  `thumbup` int NULL DEFAULT NULL COMMENT '点赞数',
  `comment` int NULL DEFAULT NULL COMMENT '评论数',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核状态',
  `channelid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属频道',
  `url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'URL',
  `type` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章' ROW_FORMAT = DYNAMIC;

-- tb_channel
CREATE TABLE IF NOT EXISTS `tb_channel`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '频道名称',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '频道' ROW_FORMAT = DYNAMIC;

-- tb_city
CREATE TABLE IF NOT EXISTS `tb_city`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '城市名称',
  `ishot` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否热门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '城市' ROW_FORMAT = DYNAMIC;

-- tb_column
CREATE TABLE IF NOT EXISTS `tb_column`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专栏名称',
  `summary` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专栏简介',
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ID',
  `createtime` datetime NULL DEFAULT NULL COMMENT '申请日期',
  `checktime` datetime NULL DEFAULT NULL COMMENT '审核日期',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '专栏' ROW_FORMAT = DYNAMIC;

-- tb_label
CREATE TABLE IF NOT EXISTS `tb_label`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签ID',
  `labelname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标签名称',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  `count` bigint NULL DEFAULT NULL COMMENT '使用数量',
  `recommend` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否推荐',
  `fans` bigint NULL DEFAULT NULL COMMENT '粉丝数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签' ROW_FORMAT = DYNAMIC;

-- tb_pl
CREATE TABLE IF NOT EXISTS `tb_pl`  (
  `problemid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '问题ID',
  `labelid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`problemid`, `labelid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- tb_problem
CREATE TABLE IF NOT EXISTS `tb_problem`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `createtime` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `updatetime` datetime NULL DEFAULT NULL COMMENT '修改日期',
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户ID',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `visits` bigint NULL DEFAULT NULL COMMENT '浏览量',
  `thumbup` bigint NULL DEFAULT NULL COMMENT '点赞数',
  `reply` bigint NULL DEFAULT NULL COMMENT '回复数',
  `solve` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否解决',
  `replyname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复人昵称',
  `replytime` datetime NULL DEFAULT NULL COMMENT '回复日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '问题' ROW_FORMAT = DYNAMIC;

-- tb_reply
CREATE TABLE IF NOT EXISTS `tb_reply`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
  `problemid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '问题ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回答内容',
  `createtime` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `updatetime` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回答人ID',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回答人昵称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '回答' ROW_FORMAT = DYNAMIC;

-- tb_ul
CREATE TABLE IF NOT EXISTS `tb_ul`  (
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `labelid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`userid`, `labelid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- tb_admin
CREATE TABLE IF NOT EXISTS `tb_admin`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `loginname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登陆名称',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `state` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- tb_follow
CREATE TABLE IF NOT EXISTS `tb_follow`  (
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `targetuser` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '被关注用户ID',
  PRIMARY KEY (`userid`, `targetuser`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- tb_friend
CREATE TABLE IF NOT EXISTS `tb_friend`  (
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `friendid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '好友ID',
  `islike` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否互相喜欢',
  PRIMARY KEY (`userid`, `friendid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- tb_nofriend
CREATE TABLE IF NOT EXISTS `tb_nofriend`  (
  `userid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `friendid` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '非好友ID',
  PRIMARY KEY (`userid`, `friendid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- tb_user
CREATE TABLE IF NOT EXISTS `tb_user`  (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ID',
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `sex` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` datetime NULL DEFAULT NULL COMMENT '出生年月日',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'E-Mail',
  `regdate` datetime NULL DEFAULT NULL COMMENT '注册日期',
  `updatedate` datetime NULL DEFAULT NULL COMMENT '修改日期',
  `lastdate` datetime NULL DEFAULT NULL COMMENT '最后登陆日期',
  `online` bigint NULL DEFAULT NULL COMMENT '在线时长（分钟）',
  `interest` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '兴趣',
  `personality` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '个性',
  `fanscount` int NULL DEFAULT NULL COMMENT '粉丝数',
  `followcount` int NULL DEFAULT NULL COMMENT '关注数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- 文章评论；接口中的 _id 对应此表的 id 列。
CREATE TABLE IF NOT EXISTS `tb_comment` (
  `id` varchar(20) NOT NULL COMMENT '评论ID',
  `articleid` varchar(20) NULL DEFAULT NULL COMMENT '文章ID',
  `content` text NULL COMMENT '评论内容',
  `userid` varchar(20) NULL DEFAULT NULL COMMENT '用户ID',
  `parentid` varchar(20) NULL DEFAULT NULL COMMENT '父评论ID',
  `publishdate` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `checktime` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `state` varchar(1) NULL DEFAULT '0' COMMENT '审核状态：0未审核，1已审核',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_comment_articleid` (`articleid`),
  KEY `idx_comment_parentid` (`parentid`),
  KEY `idx_comment_userid` (`userid`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章评论' ROW_FORMAT = DYNAMIC;

-- 吐槽及其回复；接口中的 _id 对应此表的 id 列。
CREATE TABLE IF NOT EXISTS `tb_spit` (
  `id` varchar(20) NOT NULL COMMENT '吐槽ID',
  `content` text NULL COMMENT '内容',
  `publishtime` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `userid` varchar(20) NULL DEFAULT NULL COMMENT '用户ID',
  `nickname` varchar(100) NULL DEFAULT NULL COMMENT '用户昵称',
  `visits` int NOT NULL DEFAULT 0 COMMENT '浏览量',
  `thumbup` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `share` int NOT NULL DEFAULT 0 COMMENT '分享数',
  `comment` int NOT NULL DEFAULT 0 COMMENT '回复数',
  `state` varchar(1) NULL DEFAULT '1' COMMENT '状态',
  `parentid` varchar(20) NULL DEFAULT NULL COMMENT '父吐槽ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_spit_parentid` (`parentid`),
  KEY `idx_spit_userid` (`userid`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '吐槽' ROW_FORMAT = DYNAMIC;

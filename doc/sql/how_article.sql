/*
 Navicat Premium Data Transfer

 Source Server         : 阿段云
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 121.196.168.101:3306
 Source Schema         : how_article

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 12/06/2021 15:35:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `columnid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专栏ID',
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '文章正文',
  `image` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文章封面',
  `createtime` datetime NULL DEFAULT NULL COMMENT '发表日期',
  `updatetime` datetime NULL DEFAULT NULL COMMENT '修改日期',
  `ispublic` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否公开',
  `istop` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否置顶',
  `visits` int NULL DEFAULT NULL COMMENT '浏览量',
  `thumbup` int NULL DEFAULT NULL COMMENT '点赞数',
  `comment` int NULL DEFAULT NULL COMMENT '评论数',
  `state` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核状态',
  `channelid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属频道',
  `url` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'URL',
  `type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '文章' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_article
-- ----------------------------
INSERT INTO `tb_article` VALUES ('1', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('10', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('11', '1', '123', 'test', '房管局规划局法国海军高房价', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 100, 10, 10, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('2', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('3', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('4', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('5', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('6', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('7', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('8', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');
INSERT INTO `tb_article` VALUES ('9', '1', '123', 'test', 'test', 'test', '2020-08-13 15:20:37', '2020-08-13 15:20:40', '1', '1', 1, 1, 1, '1', '1', '1', '1');

-- ----------------------------
-- Table structure for tb_channel
-- ----------------------------
DROP TABLE IF EXISTS `tb_channel`;
CREATE TABLE `tb_channel`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '频道名称',
  `state` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '频道' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_channel
-- ----------------------------
INSERT INTO `tb_channel` VALUES ('1', '测试', '1');

-- ----------------------------
-- Table structure for tb_city
-- ----------------------------
DROP TABLE IF EXISTS `tb_city`;
CREATE TABLE `tb_city`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市名称',
  `ishot` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否热门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '城市' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_city
-- ----------------------------
INSERT INTO `tb_city` VALUES ('1', '北京', '1');
INSERT INTO `tb_city` VALUES ('2', '上海', '1');
INSERT INTO `tb_city` VALUES ('3', '广州', '1');
INSERT INTO `tb_city` VALUES ('4', '深圳', '1');
INSERT INTO `tb_city` VALUES ('5', '天津', '0');
INSERT INTO `tb_city` VALUES ('6', '重庆', '0');
INSERT INTO `tb_city` VALUES ('7', '西安', '0');

-- ----------------------------
-- Table structure for tb_column
-- ----------------------------
DROP TABLE IF EXISTS `tb_column`;
CREATE TABLE `tb_column`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专栏名称',
  `summary` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专栏简介',
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `createtime` datetime NULL DEFAULT NULL COMMENT '申请日期',
  `checktime` datetime NULL DEFAULT NULL COMMENT '审核日期',
  `state` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '专栏' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_column
-- ----------------------------
INSERT INTO `tb_column` VALUES ('1', 'test', 'test', '123', '2020-08-13 14:46:39', '2020-08-13 14:46:41', '1');

-- ----------------------------
-- Table structure for tb_label
-- ----------------------------
DROP TABLE IF EXISTS `tb_label`;
CREATE TABLE `tb_label`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标签ID',
  `labelname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签名称',
  `state` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  `count` bigint NULL DEFAULT NULL COMMENT '使用数量',
  `recommend` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否推荐',
  `fans` bigint NULL DEFAULT NULL COMMENT '粉丝数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_label
-- ----------------------------
INSERT INTO `tb_label` VALUES ('1', 'java', '1', NULL, NULL, NULL);
INSERT INTO `tb_label` VALUES ('2', 'PHP', '1', NULL, NULL, NULL);
INSERT INTO `tb_label` VALUES ('3', 'C++', '1', NULL, NULL, NULL);
INSERT INTO `tb_label` VALUES ('4', 'python', '1', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for tb_pl
-- ----------------------------
DROP TABLE IF EXISTS `tb_pl`;
CREATE TABLE `tb_pl`  (
  `problemid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '问题ID',
  `labelid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`problemid`, `labelid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_pl
-- ----------------------------
INSERT INTO `tb_pl` VALUES ('1', '1');

-- ----------------------------
-- Table structure for tb_problem
-- ----------------------------
DROP TABLE IF EXISTS `tb_problem`;
CREATE TABLE `tb_problem`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容',
  `createtime` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `updatetime` datetime NULL DEFAULT NULL COMMENT '修改日期',
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `nickname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `visits` bigint NULL DEFAULT NULL COMMENT '浏览量',
  `thumbup` bigint NULL DEFAULT NULL COMMENT '点赞数',
  `reply` bigint NULL DEFAULT NULL COMMENT '回复数',
  `solve` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否解决',
  `replyname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回复人昵称',
  `replytime` datetime NULL DEFAULT NULL COMMENT '回复日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '问题' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_problem
-- ----------------------------
INSERT INTO `tb_problem` VALUES ('1', '这是个问题', '代码调试不通咋办？', '2018-01-08 11:50:50', '2018-01-09 11:50:54', '2', NULL, 101, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for tb_reply
-- ----------------------------
DROP TABLE IF EXISTS `tb_reply`;
CREATE TABLE `tb_reply`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编号',
  `problemid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '问题ID',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '回答内容',
  `createtime` datetime NULL DEFAULT NULL COMMENT '创建日期',
  `updatetime` datetime NULL DEFAULT NULL COMMENT '更新日期',
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回答人ID',
  `nickname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回答人昵称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '回答' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_reply
-- ----------------------------
INSERT INTO `tb_reply` VALUES ('', '1', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `tb_reply` VALUES ('2', '1', '问老师呗', '2018-01-10 14:14:06', NULL, '1', NULL);
INSERT INTO `tb_reply` VALUES ('3', '2', '明天再调', '2018-01-07 14:14:13', NULL, '1', NULL);

-- ----------------------------
-- Table structure for tb_ul
-- ----------------------------
DROP TABLE IF EXISTS `tb_ul`;
CREATE TABLE `tb_ul`  (
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `labelid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`userid`, `labelid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_ul
-- ----------------------------
INSERT INTO `tb_ul` VALUES ('1', '1');
INSERT INTO `tb_ul` VALUES ('1', '2');
INSERT INTO `tb_ul` VALUES ('1', '3');

SET FOREIGN_KEY_CHECKS = 1;

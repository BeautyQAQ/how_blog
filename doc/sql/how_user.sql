/*
 Navicat Premium Data Transfer

 Source Server         : 阿段云
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 121.196.168.101:3306
 Source Schema         : how_user

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 12/06/2021 15:35:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_admin
-- ----------------------------
DROP TABLE IF EXISTS `tb_admin`;
CREATE TABLE `tb_admin`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `loginname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登陆名称',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `state` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '管理员' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_admin
-- ----------------------------
INSERT INTO `tb_admin` VALUES ('1367291950604095488', 'admin', '$2a$10$cOFsYQFDPBCFmXT9GQqCf.Oy2UgahSaY9MlhIgkhKQpTMEOGVmSeC', '1');

-- ----------------------------
-- Table structure for tb_follow
-- ----------------------------
DROP TABLE IF EXISTS `tb_follow`;
CREATE TABLE `tb_follow`  (
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
  `targetuser` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '被关注用户ID',
  PRIMARY KEY (`userid`, `targetuser`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_follow
-- ----------------------------
INSERT INTO `tb_follow` VALUES ('1', '1');
INSERT INTO `tb_follow` VALUES ('1', '10');

-- ----------------------------
-- Table structure for tb_friend
-- ----------------------------
DROP TABLE IF EXISTS `tb_friend`;
CREATE TABLE `tb_friend`  (
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
  `friendid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '好友ID',
  `islike` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否互相喜欢',
  PRIMARY KEY (`userid`, `friendid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_friend
-- ----------------------------

-- ----------------------------
-- Table structure for tb_nofriend
-- ----------------------------
DROP TABLE IF EXISTS `tb_nofriend`;
CREATE TABLE `tb_nofriend`  (
  `userid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
  `friendid` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '非好友ID',
  PRIMARY KEY (`userid`, `friendid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_nofriend
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'ID',
  `mobile` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `sex` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` datetime NULL DEFAULT NULL COMMENT '出生年月日',
  `avatar` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'E-Mail',
  `regdate` datetime NULL DEFAULT NULL COMMENT '注册日期',
  `updatedate` datetime NULL DEFAULT NULL COMMENT '修改日期',
  `lastdate` datetime NULL DEFAULT NULL COMMENT '最后登陆日期',
  `online` bigint NULL DEFAULT NULL COMMENT '在线时长（分钟）',
  `interest` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '兴趣',
  `personality` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个性',
  `fanscount` int NULL DEFAULT NULL COMMENT '粉丝数',
  `followcount` int NULL DEFAULT NULL COMMENT '关注数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1', '111', '111111', '小白', '男', '2018-01-08 15:39:19', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0);
INSERT INTO `tb_user` VALUES ('1306457489587441664', '10086', '$2a$10$ei7pUnvZ5ik6BvB6GLdC7evJ/gzjimPRmDOfIqoN8AUc9Ttk3cWIa', '刚刚', '男', '2020-09-17 04:56:11', NULL, NULL, '2020-09-17 04:58:33', '2020-09-17 04:58:33', '2020-09-17 04:58:33', 0, NULL, NULL, 0, 0);

SET FOREIGN_KEY_CHECKS = 1;

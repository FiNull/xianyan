/*
 Navicat Premium Data Transfer

 Source Server         : ThisPC
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : xianyan

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 24/12/2018 16:53:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for articles
-- ----------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文本内容，包含html标签',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '去掉html标签的内容',
  `main_pic` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'item.png' COMMENT '封面图',
  `read_num` int(11) NOT NULL DEFAULT 0 COMMENT '阅读量',
  `star_num` int(11) NOT NULL DEFAULT 0 COMMENT '获赞数量',
  `author_id` int(11) NOT NULL COMMENT '作者id',
  `del_status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：可浏览状态；0：删除状态',
  `save_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collects
-- ----------------------------
DROP TABLE IF EXISTS `collects`;
CREATE TABLE `collects`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `article_id` int(11) NOT NULL COMMENT '用户ID',
  `author_id` int(11) NOT NULL COMMENT '文章id',
  `collect_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '文章id',
  `author_id` int(11) NOT NULL COMMENT '评论者id',
  `content` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '评论内容',
  `del_status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：可见状态；0：删除状态',
  `star_num` int(11) NOT NULL DEFAULT 0 COMMENT '获赞数量',
  `save_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_article_star
-- ----------------------------
DROP TABLE IF EXISTS `user_article_star`;
CREATE TABLE `user_article_star`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `article_id` int(11) NOT NULL COMMENT '用户id',
  `author_id` int(11) NOT NULL COMMENT '文章id',
  `star_status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：star状态；0：unstar状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_comment_star
-- ----------------------------
DROP TABLE IF EXISTS `user_comment_star`;
CREATE TABLE `user_comment_star`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `author_id` int(11) NOT NULL COMMENT '用户id',
  `comment_id` int(11) NOT NULL COMMENT '评论id',
  `star_status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：star状态；0：unstar状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '密码',
  `sex` tinyint(2) NOT NULL DEFAULT 1 COMMENT '1：男；0：女',
  `photo` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'photo.png' COMMENT '头像文件名',
  `role` tinyint(2) NOT NULL DEFAULT 0 COMMENT '0：普通用户；1：管理员',
  `register_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

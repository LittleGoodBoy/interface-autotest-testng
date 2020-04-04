/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : interface

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2018-09-07 15:32:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for autolog
-- ----------------------------
DROP TABLE IF EXISTS `autolog`;
CREATE TABLE `autolog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `testCase` varchar(255) DEFAULT NULL,
  `reqType` varchar(255) DEFAULT NULL,
  `reqUrl` varchar(255) DEFAULT NULL,
  `reqData` varchar(255) DEFAULT NULL,
  `expResult` varchar(255) DEFAULT NULL,
  `actResult` text,
  `result` int(1) DEFAULT NULL,
  `execTime` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of autolog
-- ----------------------------

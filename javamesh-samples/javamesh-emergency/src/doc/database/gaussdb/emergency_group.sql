/*
 Navicat Premium Data Transfer

 Source Server         : 绿区17
 Source Server Type    : PostgreSQL
 Source Server Version : 90204
 Source Host           : 100.94.174.17:5432
 Source Catalog        : hercules
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 90204
 File Encoding         : 65001

 Date: 01/03/2022 15:56:05
*/


-- ----------------------------
-- Table structure for emergency_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_group";
CREATE TABLE "public"."emergency_group"
(
    "group_id"    serial4                                     NOT NULL,
    "group_name"  varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "create_time" timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "create_user" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT
ON COLUMN "public"."emergency_group"."group_name" IS '组名';
COMMENT
ON COLUMN "public"."emergency_group"."create_time" IS '创建时间';
COMMENT
ON COLUMN "public"."emergency_group"."create_user" IS '创建人';

-- ----------------------------
-- Records of emergency_group
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_group
-- ----------------------------
ALTER TABLE "public"."emergency_group"
    ADD CONSTRAINT "emergency_group_group_name_key" UNIQUE ("group_name");
ALTER TABLE "public"."emergency_group"
    ADD CONSTRAINT "emergency_group_pkey" PRIMARY KEY ("group_id");

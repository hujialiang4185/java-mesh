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

 Date: 01/03/2022 15:56:46
*/


-- ----------------------------
-- Table structure for emergency_resource
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_resource";
CREATE TABLE "public"."emergency_resource"
(
    "resource_id"   serial4                                   NOT NULL,
    "resource_name" varchar(255) COLLATE "pg_catalog"."default",
    "resource_path" varchar(255) COLLATE "pg_catalog"."default",
    "script_id"     int4,
    "is_valid"      varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1',
    "create_user"   varchar(40) COLLATE "pg_catalog"."default",
    "create_time"   timestamp(6)                              NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT
ON COLUMN "public"."emergency_resource"."resource_id" IS '资源ID';
COMMENT
ON COLUMN "public"."emergency_resource"."resource_name" IS '资源名称';
COMMENT
ON COLUMN "public"."emergency_resource"."resource_path" IS '资源路径';
COMMENT
ON COLUMN "public"."emergency_resource"."script_id" IS '脚本ID';
COMMENT
ON COLUMN "public"."emergency_resource"."is_valid" IS '是否生效';
COMMENT
ON COLUMN "public"."emergency_resource"."create_user" IS '创建人';
COMMENT
ON COLUMN "public"."emergency_resource"."create_time" IS '创建时间';

-- ----------------------------
-- Records of emergency_resource
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_resource
-- ----------------------------
ALTER TABLE "public"."emergency_resource"
    ADD CONSTRAINT "emergency_resource_pkey" PRIMARY KEY ("resource_id");

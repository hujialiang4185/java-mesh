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

 Date: 01/03/2022 15:55:27
*/


-- ----------------------------
-- Table structure for emergency_element
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_element";
CREATE TABLE "public"."emergency_element" (
  "element_id" serial4 NOT NULL,
  "element_no" varchar(40) COLLATE "pg_catalog"."default" NOT NULL,
  "element_title" varchar(255) COLLATE "pg_catalog"."default",
  "element_type" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "element_params" text COLLATE "pg_catalog"."default",
  "parent_id" int4,
  "script_id" int4,
  "argus_path" varchar(255) COLLATE "pg_catalog"."default",
  "is_valid" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1',
  "create_user" varchar(40) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "seq" int4
)
;
COMMENT ON COLUMN "public"."emergency_element"."element_id" IS '组件ID';
COMMENT ON COLUMN "public"."emergency_element"."element_no" IS '组件编号';
COMMENT ON COLUMN "public"."emergency_element"."element_title" IS '组件名称';
COMMENT ON COLUMN "public"."emergency_element"."element_type" IS '组件类型';
COMMENT ON COLUMN "public"."emergency_element"."element_params" IS '组件参数列表json字符串';
COMMENT ON COLUMN "public"."emergency_element"."parent_id" IS '父组件ID';
COMMENT ON COLUMN "public"."emergency_element"."script_id" IS '脚本ID';
COMMENT ON COLUMN "public"."emergency_element"."argus_path" IS '压测脚本路径';
COMMENT ON COLUMN "public"."emergency_element"."is_valid" IS '是否有效 0 无效 1有效';
COMMENT ON COLUMN "public"."emergency_element"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."emergency_element"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."emergency_element"."seq" IS '显示顺序';

-- ----------------------------
-- Records of emergency_element
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_element
-- ----------------------------
ALTER TABLE "public"."emergency_element" ADD CONSTRAINT "emergency_element_pkey" PRIMARY KEY ("element_id");

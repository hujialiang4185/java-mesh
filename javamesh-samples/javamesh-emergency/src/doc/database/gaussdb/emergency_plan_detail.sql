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

 Date: 01/03/2022 15:56:36
*/


-- ----------------------------
-- Table structure for emergency_plan_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_plan_detail";
CREATE TABLE "public"."emergency_plan_detail" (
  "detail_id" serial4 NOT NULL,
  "plan_id" int4 NOT NULL,
  "scene_id" int4 NOT NULL,
  "task_id" int4,
  "pre_scene_id" int4,
  "pre_task_id" int4,
  "parent_task_id" int4,
  "create_user" varchar(40) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  "is_valid" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1',
  "sync" varchar(255) COLLATE "pg_catalog"."default" DEFAULT '同步'
)
;
COMMENT ON COLUMN "public"."emergency_plan_detail"."detail_id" IS '主键ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."plan_id" IS '预案ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."scene_id" IS '场景ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."task_id" IS '任务ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."pre_scene_id" IS '所依赖的场景ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."pre_task_id" IS '所依赖的任务ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."parent_task_id" IS '父任务ID';
COMMENT ON COLUMN "public"."emergency_plan_detail"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."emergency_plan_detail"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."emergency_plan_detail"."is_valid" IS '是否生效';
COMMENT ON COLUMN "public"."emergency_plan_detail"."sync" IS '执行标志位 异步执行async 同步执行sync';

-- ----------------------------
-- Records of emergency_plan_detail
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_plan_detail
-- ----------------------------
ALTER TABLE "public"."emergency_plan_detail" ADD CONSTRAINT "emergency_plan_detail_pkey" PRIMARY KEY ("detail_id");

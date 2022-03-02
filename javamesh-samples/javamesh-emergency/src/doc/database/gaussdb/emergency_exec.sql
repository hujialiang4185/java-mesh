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

 Date: 01/03/2022 15:55:39
*/


-- ----------------------------
-- Table structure for emergency_exec
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_exec";
CREATE TABLE "public"."emergency_exec" (
  "exec_id" serial4 NOT NULL,
  "plan_id" int4,
  "scene_id" int4,
  "task_id" int4,
  "task_detail_id" int4,
  "script_id" int4,
  "create_user" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  "start_time" timestamp(6),
  "end_time" timestamp(6)
)
;
COMMENT ON COLUMN "public"."emergency_exec"."exec_id" IS '主键ID';
COMMENT ON COLUMN "public"."emergency_exec"."plan_id" IS '预案ID';
COMMENT ON COLUMN "public"."emergency_exec"."scene_id" IS '场景ID';
COMMENT ON COLUMN "public"."emergency_exec"."task_id" IS '任务ID';
COMMENT ON COLUMN "public"."emergency_exec"."task_detail_id" IS '子任务ID';
COMMENT ON COLUMN "public"."emergency_exec"."script_id" IS '脚本ID';
COMMENT ON COLUMN "public"."emergency_exec"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."emergency_exec"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."emergency_exec"."start_time" IS '开始执行时间';
COMMENT ON COLUMN "public"."emergency_exec"."end_time" IS '结束执行时间';

-- ----------------------------
-- Records of emergency_exec
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_exec
-- ----------------------------
ALTER TABLE "public"."emergency_exec" ADD CONSTRAINT "emergency_exec_pkey" PRIMARY KEY ("exec_id");

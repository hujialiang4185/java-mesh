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

 Date: 01/03/2022 15:55:49
*/


-- ----------------------------
-- Table structure for emergency_exec_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_exec_record";
CREATE TABLE "public"."emergency_exec_record" (
  "record_id" serial4 NOT NULL,
  "exec_id" int4 NOT NULL,
  "plan_id" int4 NOT NULL,
  "plan_detail_id" int4,
  "scene_id" int4 NOT NULL,
  "task_id" int4,
  "pre_scene_id" int4,
  "pre_task_id" int4,
  "parent_task_id" int4,
  "status" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0',
  "script_id" int4,
  "script_name" varchar(255) COLLATE "pg_catalog"."default",
  "script_content" text COLLATE "pg_catalog"."default",
  "script_type" varchar(1) COLLATE "pg_catalog"."default",
  "script_params" varchar(255) COLLATE "pg_catalog"."default",
  "server_id" varchar(255) COLLATE "pg_catalog"."default",
  "server_ip" varchar(255) COLLATE "pg_catalog"."default",
  "server_user" varchar(255) COLLATE "pg_catalog"."default",
  "have_password" varchar(1) COLLATE "pg_catalog"."default",
  "password_mode" varchar(1) COLLATE "pg_catalog"."default",
  "password" varchar(255) COLLATE "pg_catalog"."default",
  "log" text COLLATE "pg_catalog"."default",
  "create_user" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  "start_time" timestamp(6),
  "end_time" timestamp(6),
  "ensure_user" varchar(255) COLLATE "pg_catalog"."default",
  "is_valid" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1',
  "sync" varchar(255) COLLATE "pg_catalog"."default",
  "perf_test_id" int4
)
;
COMMENT ON COLUMN "public"."emergency_exec_record"."record_id" IS '主键ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."exec_id" IS '执行ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."plan_id" IS '预案ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."plan_detail_id" IS '预案明细ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."scene_id" IS '场景ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."task_id" IS '任务ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."pre_scene_id" IS '所依赖的场景ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."pre_task_id" IS '所依赖的任务ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."parent_task_id" IS '父任务ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."status" IS '执行状态（0 待执行 1执行中 2执行成功 3执行失败 4执行取消 5人工确认成功 6人工确认失败）';
COMMENT ON COLUMN "public"."emergency_exec_record"."script_id" IS '脚本ID';
COMMENT ON COLUMN "public"."emergency_exec_record"."script_name" IS '脚本名';
COMMENT ON COLUMN "public"."emergency_exec_record"."script_content" IS '脚本内容';
COMMENT ON COLUMN "public"."emergency_exec_record"."script_type" IS '脚本类型 0 shell 1 jython 2 gr0ovy';
COMMENT ON COLUMN "public"."emergency_exec_record"."script_params" IS '脚本参数';
COMMENT ON COLUMN "public"."emergency_exec_record"."server_id" IS '服务器ID集合';
COMMENT ON COLUMN "public"."emergency_exec_record"."server_ip" IS '远程服务器IP';
COMMENT ON COLUMN "public"."emergency_exec_record"."server_user" IS '服务器用户';
COMMENT ON COLUMN "public"."emergency_exec_record"."have_password" IS '有无密码 0:无密码,1:有密码';
COMMENT ON COLUMN "public"."emergency_exec_record"."password_mode" IS '密码获取方式 0:本地,1:平台';
COMMENT ON COLUMN "public"."emergency_exec_record"."password" IS '密码';
COMMENT ON COLUMN "public"."emergency_exec_record"."log" IS '运行日志';
COMMENT ON COLUMN "public"."emergency_exec_record"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."emergency_exec_record"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."emergency_exec_record"."start_time" IS '开始执行时间';
COMMENT ON COLUMN "public"."emergency_exec_record"."end_time" IS '结束执行时间';
COMMENT ON COLUMN "public"."emergency_exec_record"."ensure_user" IS '确认人';
COMMENT ON COLUMN "public"."emergency_exec_record"."is_valid" IS '有效标志';
COMMENT ON COLUMN "public"."emergency_exec_record"."sync" IS '执行方式';
COMMENT ON COLUMN "public"."emergency_exec_record"."perf_test_id" IS '性能测试ID';

-- ----------------------------
-- Records of emergency_exec_record
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_exec_record
-- ----------------------------
ALTER TABLE "public"."emergency_exec_record" ADD CONSTRAINT "emergency_exec_record_pkey" PRIMARY KEY ("record_id");

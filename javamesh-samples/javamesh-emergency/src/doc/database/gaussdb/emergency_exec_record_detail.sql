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

 Date: 01/03/2022 15:55:56
*/


-- ----------------------------
-- Table structure for emergency_exec_record_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_exec_record_detail";
CREATE TABLE "public"."emergency_exec_record_detail" (
  "detail_id" serial4 NOT NULL,
  "exec_id" int4 NOT NULL,
  "record_id" int4 NOT NULL,
  "status" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0',
  "server_id" int4,
  "server_ip" varchar(255) COLLATE "pg_catalog"."default",
  "log" text COLLATE "pg_catalog"."default",
  "create_user" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  "start_time" timestamp(6),
  "end_time" timestamp(6),
  "ensure_user" varchar(255) COLLATE "pg_catalog"."default",
  "ensure_time" timestamp(6),
  "is_valid" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1',
  "pid" int4
)
;
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."detail_id" IS '主键ID';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."exec_id" IS '执行ID';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."record_id" IS '执行记录ID';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."status" IS '执行状态（0 待执行 1执行中 2执行成功 3执行失败 4执行取消 5人工确认成功 6人工确认失败）';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."server_id" IS '服务器ID';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."server_ip" IS '远程服务器IP';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."log" IS '运行日志';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."start_time" IS '开始执行时间';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."end_time" IS '结束执行时间';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."ensure_user" IS '确认人';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."ensure_time" IS '确认时间';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."is_valid" IS '有效标志';
COMMENT ON COLUMN "public"."emergency_exec_record_detail"."pid" IS '进程号';

-- ----------------------------
-- Records of emergency_exec_record_detail
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_exec_record_detail
-- ----------------------------
ALTER TABLE "public"."emergency_exec_record_detail" ADD CONSTRAINT "emergency_exec_record_detail_pkey" PRIMARY KEY ("detail_id");

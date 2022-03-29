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

 Date: 01/03/2022 15:57:12
*/


-- ----------------------------
-- Table structure for emergency_task
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_task";
CREATE TABLE "public"."emergency_task"
(
    "task_id"      serial4                                     NOT NULL,
    "task_no"      varchar(30) COLLATE "pg_catalog"."default",
    "task_name"    varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "task_type"    varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT '3',
    "task_desc"    varchar(255) COLLATE "pg_catalog"."default",
    "scene_id"     int4,
    "script_id"    int4,
    "server_id"    varchar(255) COLLATE "pg_catalog"."default",
    "pre_task_id"  int4,
    "create_user"  varchar(40) COLLATE "pg_catalog"."default",
    "create_time"  timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "is_valid"     varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT '1',
    "channel_type" varchar(255) COLLATE "pg_catalog"."default",
    "script_name"  varchar(255) COLLATE "pg_catalog"."default",
    "submit_info"  varchar(255) COLLATE "pg_catalog"."default",
    "perf_test_id" int4
)
;
COMMENT
ON COLUMN "public"."emergency_task"."task_id" IS '主键ID';
COMMENT
ON COLUMN "public"."emergency_task"."task_no" IS '任务编号';
COMMENT
ON COLUMN "public"."emergency_task"."task_name" IS '任务名称';
COMMENT
ON COLUMN "public"."emergency_task"."task_type" IS '任务类型 1自定义压测 2 引流压测 3命令行';
COMMENT
ON COLUMN "public"."emergency_task"."task_desc" IS '任务描述';
COMMENT
ON COLUMN "public"."emergency_task"."scene_id" IS '场景ID';
COMMENT
ON COLUMN "public"."emergency_task"."script_id" IS '脚本ID';
COMMENT
ON COLUMN "public"."emergency_task"."server_id" IS '服务器ID集合';
COMMENT
ON COLUMN "public"."emergency_task"."pre_task_id" IS '所依赖的任务ID';
COMMENT
ON COLUMN "public"."emergency_task"."create_user" IS '创建人';
COMMENT
ON COLUMN "public"."emergency_task"."create_time" IS '创建时间';
COMMENT
ON COLUMN "public"."emergency_task"."is_valid" IS '是否生效';
COMMENT
ON COLUMN "public"."emergency_task"."channel_type" IS '通道类型';
COMMENT
ON COLUMN "public"."emergency_task"."script_name" IS '脚本名称';
COMMENT
ON COLUMN "public"."emergency_task"."submit_info" IS '提交信息';
COMMENT
ON COLUMN "public"."emergency_task"."perf_test_id" IS '压测场景ID';

-- ----------------------------
-- Records of emergency_task
-- ----------------------------

-- ----------------------------
-- Indexes structure for table emergency_task
-- ----------------------------
CREATE INDEX "is_valid" ON "public"."emergency_task" USING btree (
    "is_valid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE INDEX "task_name" ON "public"."emergency_task" USING btree (
    "task_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE INDEX "task_no" ON "public"."emergency_task" USING btree (
    "task_no" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table emergency_task
-- ----------------------------
ALTER TABLE "public"."emergency_task"
    ADD CONSTRAINT "emergency_task_pkey" PRIMARY KEY ("task_id");

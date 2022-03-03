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

 Date: 01/03/2022 15:56:25
*/


-- ----------------------------
-- Table structure for emergency_plan
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_plan";
CREATE TABLE "public"."emergency_plan"
(
    "plan_id"           serial4                                     NOT NULL,
    "plan_no"           varchar(30) COLLATE "pg_catalog"."default",
    "plan_name"         varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "plan_group"        varchar(255) COLLATE "pg_catalog"."default",
    "create_user"       varchar(40) COLLATE "pg_catalog"."default",
    "create_time"       timestamp(6)                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "is_valid"          varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT '1',
    "check_user"        varchar(40) COLLATE "pg_catalog"."default",
    "check_time"        timestamp(6),
    "check_remark"      varchar(255) COLLATE "pg_catalog"."default",
    "status"            varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT '0',
    "schedule_type"     varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT '0',
    "schedule_conf"     varchar(255) COLLATE "pg_catalog"."default",
    "schedule_status"   varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT '0',
    "trigger_last_time" int8                                        NOT NULL DEFAULT 0,
    "trigger_next_time" int8                                        NOT NULL DEFAULT 0,
    "update_time"       timestamp(6),
    "update_user"       varchar(40) COLLATE "pg_catalog"."default"
)
;
COMMENT
ON COLUMN "public"."emergency_plan"."plan_id" IS '主键ID';
COMMENT
ON COLUMN "public"."emergency_plan"."plan_no" IS '预案编号';
COMMENT
ON COLUMN "public"."emergency_plan"."plan_name" IS '预案名称';
COMMENT
ON COLUMN "public"."emergency_plan"."plan_group" IS '分组';
COMMENT
ON COLUMN "public"."emergency_plan"."create_user" IS '创建人';
COMMENT
ON COLUMN "public"."emergency_plan"."create_time" IS '创建时间';
COMMENT
ON COLUMN "public"."emergency_plan"."is_valid" IS '是否生效';
COMMENT
ON COLUMN "public"."emergency_plan"."check_user" IS '审核人';
COMMENT
ON COLUMN "public"."emergency_plan"."check_time" IS '审核时间';
COMMENT
ON COLUMN "public"."emergency_plan"."check_remark" IS '审核意见';
COMMENT
ON COLUMN "public"."emergency_plan"."status" IS '状态位 0 新增 1 待审核 2 已审核 3拒绝 4 运行中 5 运行成功 6 运行失败';
COMMENT
ON COLUMN "public"."emergency_plan"."schedule_type" IS '调度类型 0 仅触发一次 1 固定间隔 2 cron表达式';
COMMENT
ON COLUMN "public"."emergency_plan"."schedule_conf" IS '调度配置（cron表达式 固定间隔 时间点）';
COMMENT
ON COLUMN "public"."emergency_plan"."schedule_status" IS '调度状态 0停止调度 1开始调度';
COMMENT
ON COLUMN "public"."emergency_plan"."trigger_last_time" IS '上次触发的时间戳';
COMMENT
ON COLUMN "public"."emergency_plan"."trigger_next_time" IS '下次触发的时间戳';
COMMENT
ON COLUMN "public"."emergency_plan"."update_time" IS '更新时间';
COMMENT
ON COLUMN "public"."emergency_plan"."update_user" IS '更新人';

-- ----------------------------
-- Records of emergency_plan
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_plan
-- ----------------------------
ALTER TABLE "public"."emergency_plan"
    ADD CONSTRAINT "emergency_plan_pkey" PRIMARY KEY ("plan_id");

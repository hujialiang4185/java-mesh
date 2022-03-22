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

 Date: 22/03/2022 10:15:05
*/


-- ----------------------------
-- Table structure for emergency_log_audit
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_log_audit";
CREATE TABLE "public"."emergency_log_audit" (
  "id" serial4 NOT NULL,
  "resource_type" varchar(255) COLLATE "pg_catalog"."default",
  "operation_type" varchar(255) COLLATE "pg_catalog"."default",
  "level" int4,
  "operation_results" varchar(255) COLLATE "pg_catalog"."default",
  "operation_people" varchar(255) COLLATE "pg_catalog"."default",
  "ip_address" varchar(255) COLLATE "pg_catalog"."default",
  "operation_details" varchar(255) COLLATE "pg_catalog"."default",
  "operation_date" timestamp(6)
)
;
COMMENT ON COLUMN "public"."emergency_log_audit"."id" IS '主键ID';
COMMENT ON COLUMN "public"."emergency_log_audit"."resource_type" IS '资源类型(模块名称)';
COMMENT ON COLUMN "public"."emergency_log_audit"."operation_type" IS '操作类型(增删改查)';
COMMENT ON COLUMN "public"."emergency_log_audit"."level" IS '级别，0-提示、1-一般、2-警告、3-危险、4-高危';
COMMENT ON COLUMN "public"."emergency_log_audit"."operation_results" IS '操作结果';
COMMENT ON COLUMN "public"."emergency_log_audit"."operation_people" IS '操作人';
COMMENT ON COLUMN "public"."emergency_log_audit"."ip_address" IS 'ip地址';
COMMENT ON COLUMN "public"."emergency_log_audit"."operation_details" IS '操作详情(具体接口名称)';
COMMENT ON COLUMN "public"."emergency_log_audit"."operation_date" IS '操作时间戳';

-- ----------------------------
-- Primary Key structure for table emergency_log_audit
-- ----------------------------
ALTER TABLE "public"."emergency_log_audit" ADD CONSTRAINT "emergency_log_audit_pkey" PRIMARY KEY ("id");

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

 Date: 01/03/2022 15:56:54
*/


-- ----------------------------
-- Table structure for emergency_script
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_script";
CREATE TABLE "public"."emergency_script" (
  "script_id" serial4 NOT NULL,
  "script_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "is_public" varchar(1) COLLATE "pg_catalog"."default" NOT NULL,
  "script_type" varchar(1) COLLATE "pg_catalog"."default" NOT NULL,
  "submit_info" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "have_password" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0',
  "password_mode" varchar(1) COLLATE "pg_catalog"."default",
  "password" varchar(255) COLLATE "pg_catalog"."default",
  "server_user" varchar(255) COLLATE "pg_catalog"."default",
  "server_ip" varchar(255) COLLATE "pg_catalog"."default",
  "content" text COLLATE "pg_catalog"."default",
  "script_user" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "script_group" varchar(255) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6),
  "param" varchar(255) COLLATE "pg_catalog"."default",
  "script_status" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0',
  "approver" varchar(255) COLLATE "pg_catalog"."default",
  "comment" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."emergency_script"."script_id" IS '脚本ID';
COMMENT ON COLUMN "public"."emergency_script"."script_name" IS '脚本名';
COMMENT ON COLUMN "public"."emergency_script"."is_public" IS '是否公有，0:私有,1:公有';
COMMENT ON COLUMN "public"."emergency_script"."script_type" IS '脚本类型 0:shell 1:jython 2:groovy';
COMMENT ON COLUMN "public"."emergency_script"."submit_info" IS '提交信息';
COMMENT ON COLUMN "public"."emergency_script"."have_password" IS '有无密码 0:无密码,1:有密码';
COMMENT ON COLUMN "public"."emergency_script"."password_mode" IS '密码获取方式 0:本地,1:平台';
COMMENT ON COLUMN "public"."emergency_script"."password" IS '密码';
COMMENT ON COLUMN "public"."emergency_script"."server_user" IS '服务器用户';
COMMENT ON COLUMN "public"."emergency_script"."server_ip" IS '服务器IP';
COMMENT ON COLUMN "public"."emergency_script"."content" IS '脚本内容';
COMMENT ON COLUMN "public"."emergency_script"."script_user" IS '脚本创建人';
COMMENT ON COLUMN "public"."emergency_script"."script_group" IS '脚本分组';
COMMENT ON COLUMN "public"."emergency_script"."update_time" IS '最后修改时间';
COMMENT ON COLUMN "public"."emergency_script"."param" IS '参数列表';
COMMENT ON COLUMN "public"."emergency_script"."script_status" IS '脚本状态 0:新增,1:待审核,2:已审核,3:被驳回';
COMMENT ON COLUMN "public"."emergency_script"."approver" IS '审核人';
COMMENT ON COLUMN "public"."emergency_script"."comment" IS '审核不通过原因';

-- ----------------------------
-- Records of emergency_script
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_script
-- ----------------------------
ALTER TABLE "public"."emergency_script" ADD CONSTRAINT "emergency_script_pkey" PRIMARY KEY ("script_id");

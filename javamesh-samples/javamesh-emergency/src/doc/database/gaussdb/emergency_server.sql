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

 Date: 01/03/2022 15:57:04
*/


-- ----------------------------
-- Table structure for emergency_server
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_server";
CREATE TABLE "public"."emergency_server" (
  "server_id" serial4 NOT NULL,
  "server_name" varchar(255) COLLATE "pg_catalog"."default",
  "server_user" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'root',
  "server_ip" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "server_port" int4 NOT NULL DEFAULT 22,
  "have_password" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '0',
  "password_mode" varchar(1) COLLATE "pg_catalog"."default",
  "password_uri" varchar(255) COLLATE "pg_catalog"."default",
  "password" varchar(255) COLLATE "pg_catalog"."default",
  "licensed" varchar(1) COLLATE "pg_catalog"."default",
  "agent_name" varchar(1) COLLATE "pg_catalog"."default",
  "agent_port" int4,
  "create_user" varchar(40) COLLATE "pg_catalog"."default" NOT NULL,
  "create_time" timestamp(6) NOT NULL  DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6),
  "update_user" varchar(40) COLLATE "pg_catalog"."default",
  "is_valid" varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT '1',
  "status" varchar(40) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 'pending'
)
;
COMMENT ON COLUMN "public"."emergency_server"."server_id" IS '服务器ID';
COMMENT ON COLUMN "public"."emergency_server"."server_name" IS '服务器名称';
COMMENT ON COLUMN "public"."emergency_server"."server_user" IS '服务器用户';
COMMENT ON COLUMN "public"."emergency_server"."server_ip" IS '服务器IP';
COMMENT ON COLUMN "public"."emergency_server"."server_port" IS '服务器端口';
COMMENT ON COLUMN "public"."emergency_server"."have_password" IS '有无密码 0:无密码,1:有密码';
COMMENT ON COLUMN "public"."emergency_server"."password_mode" IS '密码获取方式 0:本地,1:平台';
COMMENT ON COLUMN "public"."emergency_server"."password_uri" IS '密码平台地址';
COMMENT ON COLUMN "public"."emergency_server"."password" IS '密码';
COMMENT ON COLUMN "public"."emergency_server"."licensed" IS 'agent代理 0未许可 1已许可';
COMMENT ON COLUMN "public"."emergency_server"."agent_name" IS 'agent类型 0ngrider 1其它';
COMMENT ON COLUMN "public"."emergency_server"."agent_port" IS 'agent启动端口';
COMMENT ON COLUMN "public"."emergency_server"."create_user" IS '创建人';
COMMENT ON COLUMN "public"."emergency_server"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."emergency_server"."update_time" IS '修改时间';
COMMENT ON COLUMN "public"."emergency_server"."update_user" IS '修改人';
COMMENT ON COLUMN "public"."emergency_server"."is_valid" IS '是否生效';
COMMENT ON COLUMN "public"."emergency_server"."status" IS '状态 pending,running,success,fail';

-- ----------------------------
-- Records of emergency_server
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table emergency_server
-- ----------------------------
ALTER TABLE "public"."emergency_server" ADD CONSTRAINT "emergency_server_pkey" PRIMARY KEY ("server_id");

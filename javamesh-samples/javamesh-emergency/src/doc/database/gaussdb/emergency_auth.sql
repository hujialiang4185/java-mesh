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

 Date: 01/03/2022 15:55:19
*/


-- ----------------------------
-- Table structure for emergency_auth
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_auth";
CREATE TABLE "public"."emergency_auth" (
  "auth_id" serial4 NOT NULL,
  "role_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "auth_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."emergency_auth"."auth_id" IS '权限ID';
COMMENT ON COLUMN "public"."emergency_auth"."role_name" IS '角色名';
COMMENT ON COLUMN "public"."emergency_auth"."auth_name" IS '权限名';

-- ----------------------------
-- Records of emergency_auth
-- ----------------------------
INSERT INTO "public"."emergency_auth" VALUES (1, 'ADMIN', 'admin');
INSERT INTO "public"."emergency_auth" VALUES (2, 'ADMIN', 'operator');
INSERT INTO "public"."emergency_auth" VALUES (3, 'ADMIN', 'approver');
INSERT INTO "public"."emergency_auth" VALUES (4, 'APPROVER', 'approver');
INSERT INTO "public"."emergency_auth" VALUES (5, 'OPERATOR', 'operator');

-- ----------------------------
-- Primary Key structure for table emergency_auth
-- ----------------------------
ALTER TABLE "public"."emergency_auth" ADD CONSTRAINT "emergency_auth_pkey" PRIMARY KEY ("auth_id");

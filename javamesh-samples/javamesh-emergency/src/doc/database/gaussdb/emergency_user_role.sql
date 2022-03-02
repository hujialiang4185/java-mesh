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

 Date: 01/03/2022 15:57:21
*/


-- ----------------------------
-- Table structure for emergency_user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_user_role";
CREATE TABLE "public"."emergency_user_role" (
  "id" serial4 NOT NULL,
  "user_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "role_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "group_name" varchar(255) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of emergency_user_role
-- ----------------------------
INSERT INTO "public"."emergency_user_role" VALUES (1, 'admin', 'ADMIN', NULL);

-- ----------------------------
-- Indexes structure for table emergency_user_role
-- ----------------------------
CREATE INDEX "fk_group_user" ON "public"."emergency_user_role" USING btree (
  "group_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table emergency_user_role
-- ----------------------------
ALTER TABLE "public"."emergency_user_role" ADD CONSTRAINT "emergency_user_role_pkey" PRIMARY KEY ("id");

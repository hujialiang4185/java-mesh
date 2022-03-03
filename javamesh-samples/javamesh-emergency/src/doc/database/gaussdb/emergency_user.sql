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

 Date: 03/03/2022 11:34:05
*/


-- ----------------------------
-- Table structure for emergency_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."emergency_user";
CREATE TABLE "public"."emergency_user"
(
    "id"                 serial4                                     NOT NULL,
    "created_date"       timestamp(6)                                NOT NULL,
    "last_modified_date" timestamp(6)                                NOT NULL,
    "enabled"            char(1) COLLATE "pg_catalog"."default"      NOT NULL,
    "role_name"          varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "password"           varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "user_name"          varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "nick_name"          varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "group_name"         varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT
ON COLUMN "public"."emergency_user"."id" IS '主键ID';
COMMENT
ON COLUMN "public"."emergency_user"."created_date" IS '创建时间';
COMMENT
ON COLUMN "public"."emergency_user"."last_modified_date" IS '最后修改时间';
COMMENT
ON COLUMN "public"."emergency_user"."enabled" IS '是否启用';
COMMENT
ON COLUMN "public"."emergency_user"."role_name" IS '角色名';
COMMENT
ON COLUMN "public"."emergency_user"."password" IS '密码';
COMMENT
ON COLUMN "public"."emergency_user"."user_name" IS '用户名';
COMMENT
ON COLUMN "public"."emergency_user"."nick_name" IS '昵称';

-- ----------------------------
-- Records of emergency_user
-- ----------------------------
INSERT INTO "public"."emergency_user"
VALUES (1, '2022-03-03 11:25:36', '2022-03-03 11:25:36', 'T', 'ADMIN',
        '$2a$10$KE0VERWzRwYJIQn17XoePe0/iokyCqKlnX0t.C90Okg7eDsy0Fi/a', 'admin', 'admin', NULL);

-- ----------------------------
-- Indexes structure for table emergency_user
-- ----------------------------
CREATE INDEX "fk_user_group" ON "public"."emergency_user" USING btree (
    "group_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key structure for table emergency_user
-- ----------------------------
ALTER TABLE "public"."emergency_user"
    ADD CONSTRAINT "emergency_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table emergency_user
-- ----------------------------
ALTER TABLE "public"."emergency_user"
    ADD CONSTRAINT "fk_user_group" FOREIGN KEY ("group_name") REFERENCES "public"."emergency_group" ("group_name") ON DELETE RESTRICT ON UPDATE CASCADE;

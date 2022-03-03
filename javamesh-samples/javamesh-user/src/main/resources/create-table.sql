CREATE TABLE IF NOT EXISTS `emergency_auth`
(
    `auth_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
    `auth_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限名',
    PRIMARY KEY(`auth_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

CREATE TABLE IF NOT EXISTS `emergency_group`
(
    `group_id`    int(11) NOT NULL AUTO_INCREMENT,
    `group_name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组名',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人',
    PRIMARY KEY (`group_id`) USING BTREE,
    INDEX         `group_name`(`group_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

CREATE TABLE IF NOT EXISTS `emergency_user`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `last_modified_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最后修改时间',
                                   `enabled` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否启用',
                                   `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
                                   `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
                                   `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
                                   `nick_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
                                   `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `fk_user_group`(`group_name`) USING BTREE,
                                   CONSTRAINT `fk_user_group` FOREIGN KEY (`group_name`) REFERENCES `emergency_group` (`group_name`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of emergency_auth
-- ----------------------------
insert into `emergency_user` (created_date,last_modified_date,enabled,role_name,password,user_name,nick_name)
select CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'T','ADMIN','$2a$10$KE0VERWzRwYJIQn17XoePe0/iokyCqKlnX0t.C90Okg7eDsy0Fi/a','admin','admin'
FROM DUAL
WHERE NOT EXISTS(SELECT user_name, role_name
                 FROM `emergency_user`
                 WHERE user_name = 'admin');

insert into `emergency_auth` (role_name, auth_name)
select 'ADMIN', 'admin'
FROM DUAL
WHERE NOT EXISTS(SELECT role_name, auth_name FROM `emergency_auth` WHERE role_name = 'ADMIN' AND auth_name = 'admin');
insert into `emergency_auth` (role_name, auth_name)
select 'ADMIN', 'operator'
FROM DUAL
WHERE NOT EXISTS(SELECT role_name, auth_name
                 FROM `emergency_auth`
                 WHERE role_name = 'ADMIN'
                   AND auth_name = 'operator');

insert into `emergency_auth` (role_name, auth_name)
select 'ADMIN', 'approver'
FROM DUAL
WHERE NOT EXISTS(SELECT role_name, auth_name
                 FROM `emergency_auth`
                 WHERE role_name = 'ADMIN'
                   AND auth_name = 'approver');

insert into `emergency_auth` (role_name, auth_name)
select 'APPROVER', 'approver'
FROM DUAL
WHERE NOT EXISTS(SELECT role_name, auth_name
                 FROM `emergency_auth`
                 WHERE role_name = 'APPROVER'
                   AND auth_name = 'approver');

insert into `emergency_auth` (role_name, auth_name)
select 'OPERATOR', 'operator'
FROM DUAL
WHERE NOT EXISTS(SELECT role_name, auth_name
                 FROM `emergency_auth`
                 WHERE role_name = 'OPERATOR'
                   AND auth_name = 'operator');


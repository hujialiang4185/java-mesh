CREATE TABLE IF NOT EXISTS `emergency_auth`  (
    `auth_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
    `auth_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限名',
    PRIMARY KEY (`auth_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_element`  (
    `element_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组件ID',
    `element_no` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组件编号',
    `element_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件名称',
    `element_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组件类型',
    `element_params` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '组件参数列表json字符串',
    `parent_id` int(11) NULL DEFAULT NULL COMMENT '父组件ID',
    `script_id` int(11) NULL DEFAULT NULL COMMENT '脚本ID',
    `argus_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '压测脚本路径',
    `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '是否有效 0 无效 1有效',
    `create_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `seq` int(11) NULL DEFAULT NULL COMMENT '显示顺序',
    PRIMARY KEY (`element_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_exec`  (
    `exec_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `plan_id` int(11) NULL DEFAULT NULL COMMENT '预案ID',
    `scene_id` int(11) NULL DEFAULT NULL COMMENT '场景ID',
    `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
    `task_detail_id` int(11) NULL DEFAULT NULL COMMENT '子任务ID',
    `script_id` int(11) NULL DEFAULT NULL COMMENT '脚本ID',
    `create_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `start_time` timestamp NULL DEFAULT NULL COMMENT '开始执行时间',
    `end_time` timestamp NULL DEFAULT NULL COMMENT '结束执行时间',
    PRIMARY KEY (`exec_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_exec_record`  (
    `record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exec_id` int(11) NOT NULL COMMENT '执行ID',
    `plan_id` int(11) NOT NULL COMMENT '预案ID',
    `scene_id` int(11) NOT NULL COMMENT '场景ID',
    `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
    `pre_scene_id` int(11) NULL DEFAULT NULL COMMENT '所依赖的场景ID',
    `pre_task_id` int(11) NULL DEFAULT NULL COMMENT '所依赖的任务ID',
    `parent_task_id` int(11) NULL DEFAULT NULL COMMENT '父任务ID',
    `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '执行状态（0 待执行 1执行中 2执行成功 3执行失败 4执行取消 5人工确认成功 6人工确认失败）',
    `script_id` int(11) NULL DEFAULT NULL COMMENT '脚本ID',
    `script_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '脚本名',
    `script_content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '脚本内容',
    `script_type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '脚本类型 0 shell 1 jython 2 gr0ovy',
    `script_params` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '脚本参数',
    `server_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器ID集合',
    `server_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '远程服务器IP',
    `server_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器用户',
    `have_password` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '有无密码 0:无密码,1:有密码',
    `password_mode` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码获取方式 0:本地,1:平台',
    `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
    `log` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '运行日志',
    `create_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `start_time` timestamp NULL DEFAULT NULL COMMENT '开始执行时间',
    `end_time` timestamp NULL DEFAULT NULL COMMENT '结束执行时间',
    `ensure_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '确认人',
    `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '有效标志',
    `sync` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行方式',
    PRIMARY KEY (`record_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_exec_record_detail`  (
                                                 `detail_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                 `exec_id` int(11) NOT NULL COMMENT '执行ID',
                                                 `record_id` int(11) NOT NULL COMMENT '执行记录ID',
                                                 `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '执行状态（0 待执行 1执行中 2执行成功 3执行失败 4执行取消 5人工确认成功 6人工确认失败）',
                                                 `server_id` int(11) NULL DEFAULT NULL COMMENT '服务器ID',
                                                 `server_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '远程服务器IP',
                                                 `log` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '运行日志',
                                                 `create_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                                 `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `start_time` timestamp NULL DEFAULT NULL COMMENT '开始执行时间',
                                                 `end_time` timestamp NULL DEFAULT NULL COMMENT '结束执行时间',
                                                 `ensure_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '确认人',
                                                 `ensure_time` timestamp NULL DEFAULT NULL COMMENT '确认时间',
                                                 `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '有效标志',
                                                 `pid` int(5) NULL DEFAULT NULL COMMENT '进程号',
                                                 PRIMARY KEY (`detail_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_plan`  (
                                   `plan_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `plan_no` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预案编号',
                                   `plan_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '预案名称',
                                   `create_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '是否生效',
                                   `check_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核人',
                                   `check_time` timestamp NULL DEFAULT NULL COMMENT '审核时间',
                                   `check_remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核意见',
                                   `status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '状态位 0 新增 1 待审核 2 已审核 3拒绝 4 运行中 5 运行成功 6 运行失败',
                                   `schedule_type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '调度类型 0 仅触发一次 1 固定间隔 2 cron表达式',
                                   `schedule_conf` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '调度配置（cron表达式 固定间隔 时间点）',
                                   `schedule_status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '调度状态 0停止调度 1开始调度',
                                   `trigger_last_time` bigint(13) NOT NULL DEFAULT 0 COMMENT '上次触发的时间戳',
                                   `trigger_next_time` bigint(13) NOT NULL DEFAULT 0 COMMENT '下次触发的时间戳',
                                   `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
                                   `update_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                   PRIMARY KEY (`plan_id`) USING BTREE,
                                   UNIQUE INDEX `plan_no`(`plan_no`) USING BTREE,
                                   INDEX `is_valid`(`is_valid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_plan_detail`  (
                                          `detail_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `plan_id` int(11) NOT NULL COMMENT '预案ID',
                                          `scene_id` int(11) NOT NULL COMMENT '场景ID',
                                          `task_id` int(11) NULL DEFAULT NULL COMMENT '任务ID',
                                          `pre_scene_id` int(11) NULL DEFAULT NULL COMMENT '所依赖的场景ID',
                                          `pre_task_id` int(11) NULL DEFAULT NULL COMMENT '所依赖的任务ID',
                                          `parent_task_id` int(11) NULL DEFAULT NULL COMMENT '父任务ID',
                                          `create_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                          `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '是否生效',
                                          `sync` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '同步' COMMENT '执行标志位 异步执行async 同步执行sync',
                                          PRIMARY KEY (`detail_id`) USING BTREE,
                                          INDEX `plan_id`(`plan_id`) USING BTREE,
                                          INDEX `scene_id`(`scene_id`) USING BTREE,
                                          INDEX `task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_role`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                   `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_script`  (
                                     `script_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '脚本ID',
                                     `script_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本名',
                                     `is_public` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否公有，0:私有,1:公有',
                                     `script_type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本类型 0:shell 1:jython 2:groovy 3 编排',
                                     `submit_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '提交信息',
                                     `have_password` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '有无密码 0:无密码,1:有密码',
                                     `password_mode` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码获取方式 0:本地,1:平台',
                                     `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                                     `server_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器用户',
                                     `server_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器IP',
                                     `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本内容',
                                     `script_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本创建人',
                                     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后修改时间',
                                     `param` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '参数列表',
                                     `script_status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本状态 0:待审核,1:审核通过,2:被驳回',
                                     `comment` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核不通过原因',
                                     PRIMARY KEY (`script_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_server`  (
                                     `server_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '服务器ID',
                                     `server_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器名称',
                                     `server_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'root' COMMENT '服务器用户',
                                     `server_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器IP',
                                     `server_port` int(5) NOT NULL DEFAULT 22 COMMENT '服务器端口',
                                     `have_password` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '有无密码 0:无密码,1:有密码',
                                     `password_mode` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码获取方式 0:本地,1:平台',
                                     `password_uri` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码平台地址',
                                     `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                                     `licensed` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'agent代理 0未许可 1已许可',
                                     `agent_name` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'agent类型 0ngrider 1其它',
                                     `agent_port` int(5) NULL DEFAULT NULL COMMENT 'agent启动端口',
                                     `create_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人',
                                     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
                                     `update_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
                                     `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '是否生效',
                                     `status` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'pending' COMMENT '状态 pending,running,success,fail',
                                     PRIMARY KEY (`server_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_task`  (
                                   `task_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `task_no` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '任务编号',
                                   `task_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名称',
                                   `scene_id` int(11) NULL DEFAULT NULL COMMENT '场景ID',
                                   `script_id` int(11) NULL DEFAULT NULL COMMENT '脚本ID',
                                   `pre_task_id` int(11) NULL DEFAULT NULL COMMENT '所依赖的任务ID',
                                   `create_user` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `is_valid` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1' COMMENT '是否生效',
                                   `channel_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'SSH' COMMENT '通道类型',
                                   `script_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '脚本名称',
                                   `submit_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提交信息',
                                   `server_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器ID集合',
                                   PRIMARY KEY (`task_id`) USING BTREE,
                                   INDEX `task_no`(`task_no`) USING BTREE,
                                   INDEX `task_name`(`task_name`) USING BTREE,
                                   INDEX `is_valid`(`is_valid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;$$$

CREATE TABLE IF NOT EXISTS `emergency_user`  (
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `last_modified_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最后修改时间',
                                   `enabled` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否启用',
                                   `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名',
                                   `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
                                   `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
                                   `nick_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

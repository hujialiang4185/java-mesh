CREATE TABLE IF NOT EXISTS `emergency_script`
(
    `script_id`     int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '脚本ID',
    `script_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本名',
    `is_public`     varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '是否公有，0:私有,1:公有',
    `script_type`   varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '脚本类型 0:shell 1:jython 2:groovy',
    `submit_info`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '提交信息',
    `have_password` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '有无密码 0:无密码,1:有密码',
    `password_mode` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '密码获取方式 0:本地,1:平台',
    `password`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '密码',
    `server_user`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '服务器用户',
    `server_ip`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '服务器IP',
    `content`       text CHARACTER SET utf8 COLLATE utf8_general_ci         NOT NULL COMMENT '脚本内容',
    `script_user`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '脚本创建人',
    `script_group`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '脚本分组',
    `update_time`   timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `param`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '参数列表',
    `script_status` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '脚本状态 0:新增,1:待审核,2:已审核,3:被驳回',
    `approver`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '审核人',
    `comment`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '审核不通过原因',
    PRIMARY KEY
        (
         `script_id`
            )
        USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_plan`
(
    `plan_id`           int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `plan_no`           varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '预案编号',
    `plan_name`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '预案名称',
    `create_user`       varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`       timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_valid`          varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否生效',
    `check_user`        varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '审核人',
    `check_time`        timestamp                                               NULL     DEFAULT NULL COMMENT '审核时间',
    `check_remark`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '审核意见',
    `status`            varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '0' COMMENT '状态位 0 新增 1 待审核 2 已审核 3拒绝 4 运行中 5 运行成功 6 运行失败',
    `schedule_type`     varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '0' COMMENT '调度类型 0 仅触发一次 1 固定间隔 2 cron表达式',
    `schedule_conf`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '调度配置（cron表达式 固定间隔 时间点）',
    `schedule_status`   varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '0' COMMENT '调度状态 0停止调度 1开始调度',
    `trigger_last_time` bigint(13)                                              NOT NULL DEFAULT 0 COMMENT '上次触发的时间戳',
    `trigger_next_time` bigint(13)                                              NOT NULL DEFAULT 0 COMMENT '下次触发的时间戳',
    `update_time`       timestamp                                               NULL     DEFAULT NULL COMMENT '更新时间',
    `update_user`       varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '更新人',
    `plan_group`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '预案分组名',
    PRIMARY KEY
        (
         `plan_id`
            ) USING BTREE,
    UNIQUE INDEX `plan_no`
        (
         `plan_no`
            )
        USING BTREE,
    INDEX `is_valid`
        (
         `is_valid`
            )
        USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_plan_detail`
(
    `detail_id`      int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `plan_id`        int(11)                                                 NOT NULL COMMENT '预案ID',
    `scene_id`       int(11)                                                 NOT NULL COMMENT '场景ID',
    `task_id`        int(11)                                                 NULL     DEFAULT NULL COMMENT '任务ID',
    `pre_scene_id`   int(11)                                                 NULL     DEFAULT NULL COMMENT '所依赖的场景ID',
    `pre_task_id`    int(11)                                                 NULL     DEFAULT NULL COMMENT '所依赖的任务ID',
    `parent_task_id` int(11)                                                 NULL     DEFAULT NULL COMMENT '父任务ID',
    `create_user`    varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`    timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_valid`       varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否生效',
    `sync`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT '同步' COMMENT '执行标志位 异步执行async 同步执行sync',
    PRIMARY KEY
        (
         `detail_id`
            ) USING BTREE,
    INDEX `plan_id`
        (
         `plan_id`
            )
        USING BTREE,
    INDEX `scene_id`
        (
         `scene_id`
            )
        USING BTREE,
    INDEX `task_id`
        (
         `task_id`
            )
        USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_task`
(
    `task_id`      int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_no`      varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '任务编号',
    `task_name`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名称',
    `task_type`    varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '3' COMMENT '任务类型 1自定义压测 2 引流压测 3命令行',
    `task_desc`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '任务描述',
    `scene_id`     int(11)                                                 NULL     DEFAULT NULL COMMENT '场景ID',
    `script_id`    int(11)                                                 NULL     DEFAULT NULL COMMENT '脚本ID',
    `server_id`    varchar(255)                                            NULL     DEFAULT NULL COMMENT '服务器ID集合',
    `agent_ids`    text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT 'agentId集合',
    `pre_task_id`  int(11)                                                 NULL     DEFAULT NULL COMMENT '所依赖的任务ID',
    `create_user`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`  timestamp                                               NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_valid`     varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否生效',
    `channel_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '通道类型',
    `script_name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '脚本名称',
    `submit_info`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '提交信息',
    `perf_test_id` int(11)                                                 NULL     DEFAULT NULL COMMENT '压测场景ID',
    PRIMARY KEY
        (
         `task_id`
            )
        USING BTREE,
    INDEX `task_no`
        (
         `task_no`
            )
        USING BTREE,
    INDEX `task_name`
        (
         `task_name`
            )
        USING BTREE,
    INDEX `is_valid`
        (
         `is_valid`
            )
        USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_exec`
(
    `exec_id`        int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `plan_id`        int(11)                                                 NULL     DEFAULT NULL COMMENT '预案ID',
    `scene_id`       int(11)                                                 NULL     DEFAULT NULL COMMENT '场景ID',
    `task_id`        int(11)                                                 NULL     DEFAULT NULL COMMENT '任务ID',
    `task_detail_id` int(11)                                                 NULL     DEFAULT NULL COMMENT '子任务ID',
    `script_id`      int(11)                                                 NULL     DEFAULT NULL COMMENT '脚本ID',
    `create_user`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`    timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `start_time`     timestamp                                               NULL     DEFAULT NULL COMMENT '开始执行时间',
    `end_time`       timestamp                                               NULL     DEFAULT NULL COMMENT '结束执行时间',
    `is_valid`       varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否有效',
    PRIMARY KEY
        (
         `exec_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_exec_record`
(
    `record_id`      int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exec_id`        int(11)                                                 NOT NULL COMMENT '执行ID',
    `plan_id`        int(11)                                                 NOT NULL COMMENT '预案ID',
    `plan_detail_id` int(11)                                                 NULL     DEFAULT NULL COMMENT '预案明细ID',
    `scene_id`       int(11)                                                 NOT NULL COMMENT '场景ID',
    `task_id`        int(11)                                                 NULL     DEFAULT NULL COMMENT '任务ID',
    `pre_scene_id`   int(11)                                                 NULL     DEFAULT NULL COMMENT '所依赖的场景ID',
    `pre_task_id`    int(11)                                                 NULL     DEFAULT NULL COMMENT '所依赖的任务ID',
    `parent_task_id` int(11)                                                 NULL     DEFAULT NULL COMMENT '父任务ID',
    `status`         varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '0' COMMENT '执行状态（0 待执行 1执行中 2执行成功 3执行失败 4执行取消 5人工确认成功 6人工确认失败）',
    `script_id`      int(11)                                                 NULL     DEFAULT NULL COMMENT '脚本ID',
    `script_name`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '脚本名',
    `script_content` text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT '脚本内容',
    `script_type`    varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '脚本类型 0 shell 1 jython 2 gr0ovy',
    `script_params`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '脚本参数',
    `server_id`      varchar(255)                                            NULL     DEFAULT NULL COMMENT '服务器ID集合',
    `agent_ids`      text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT 'agentId集合',
    `server_ip`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '远程服务器IP',
    `server_user`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '服务器用户',
    `have_password`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '有无密码 0:无密码,1:有密码',
    `password_mode`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '密码获取方式 0:本地,1:平台',
    `password`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '密码',
    `log`            text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT '运行日志',
    `create_user`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`    timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `start_time`     timestamp                                               NULL     DEFAULT NULL COMMENT '开始执行时间',
    `end_time`       timestamp                                               NULL     DEFAULT NULL COMMENT '结束执行时间',
    `ensure_user`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '确认人',
    `is_valid`       varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '有效标志',
    `sync`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '执行方式',
    `perf_test_id`   int(11)                                                 NULL     DEFAULT NULL COMMENT '性能测试ID',
    PRIMARY KEY
        (
         `record_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_exec_record_detail`
(
    `detail_id`    int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exec_id`      int(11)                                                 NOT NULL COMMENT '执行ID',
    `record_id`    int(11)                                                 NOT NULL COMMENT '执行记录ID',
    `status`       varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '0' COMMENT '执行状态（0 待执行 1执行中 2执行成功 3执行失败 4执行取消 5人工确认成功 6人工确认失败）',
    `server_id`    int(11)                                                 NULL     DEFAULT NULL COMMENT '服务器ID',
    `agent_id`     int(11)                                                 NULL COMMENT 'agent Id',
    `server_ip`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '远程服务器IP',
    `perf_test_id` int(11)                                                 NULL     DEFAULT NULL COMMENT '性能测试ID',
    `log`          text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT '运行日志',
    `create_user`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`  timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `start_time`   timestamp                                               NULL     DEFAULT NULL COMMENT '开始执行时间',
    `end_time`     timestamp                                               NULL     DEFAULT NULL COMMENT '结束执行时间',
    `ensure_user`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '确认人',
    `ensure_time`  timestamp                                               NULL     DEFAULT NULL COMMENT '确认时间',
    `is_valid`     varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '有效标志',
    `pid`          int(5)                                                  NULL     DEFAULT NULL COMMENT '进程号',
    PRIMARY KEY
        (
         `detail_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_server`
(
    `server_id`     int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '服务器ID',
    `server_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '服务器名称',
    `server_user`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'root' COMMENT '服务器用户',
    `server_ip`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器IP',
    `server_port`   int(5)                                                  NOT NULL DEFAULT 22 COMMENT '服务器端口',
    `server_memory` int(11)                                                 NULL     DEFAULT NULL COMMENT '服务器内存',
    `have_password` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '有无密码 0:无密码,1:有密码',
    `password_mode` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '密码获取方式 0:本地,1:平台',
    `password_uri`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '密码平台地址',
    `password`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '密码',
    `licensed`      varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT 'agent代理 0未许可 1已许可',
    `agent_name`    varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT 'agent类型 0ngrider 1其它',
    `agent_port`    int(5)                                                  NULL     DEFAULT NULL COMMENT 'agent启动端口',
    `create_user`   varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '创建人',
    `create_time`   timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   timestamp                                               NULL     DEFAULT NULL COMMENT '修改时间',
    `update_user`   varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '修改人',
    `is_valid`      varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否生效',
    `status`        varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL DEFAULT 'pending' COMMENT '状态 pending,running,success,fail',
    `server_group`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '服务器分组',
    PRIMARY KEY
        (
         `server_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_element`
(
    `element_id`     int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '组件ID',
    `element_no`     varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '组件编号',
    `element_title`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '组件名称',
    `element_type`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组件类型',
    `element_params` text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT '组件参数列表json字符串',
    `parent_id`      int(11)                                                 NULL     DEFAULT NULL COMMENT '父组件ID',
    `script_id`      int(11)                                                 NULL     DEFAULT NULL COMMENT '脚本ID',
    `argus_path`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '压测脚本路径',
    `is_valid`       varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否有效 0 无效 1有效',
    `create_user`    varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '创建人',
    `create_time`    timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `seq`            int(11)                                                 NULL     DEFAULT NULL COMMENT '显示顺序',
    PRIMARY KEY
        (
         `element_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 50
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_resource`
(
    `resource_id`   int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '资源ID',
    `resource_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '资源名称',
    `resource_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '资源路径',
    `script_id`     int(11)                                                 NULL     DEFAULT NULL COMMENT '脚本ID',
    `is_valid`      varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否生效',
    `create_user`   varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '创建人',
    `create_time`   timestamp                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY
        (
         `resource_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_log_audit`
(
    `id`                int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `resource_type`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资源类型(模块名称)',
    `operation_type`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作类型(增删改查)',
    `level`             int(2)                                                  NULL DEFAULT NULL COMMENT '级别，0-提示、1-一般、2-警告、3-危险、4-高危',
    `operation_results` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作结果',
    `operation_people`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人',
    `ip_address`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ip地址',
    `operation_details` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作详情(具体接口名称)',
    `operation_date`    timestamp                                               NULL DEFAULT NULL COMMENT '操作时间戳',
    PRIMARY KEY
        (
         `id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE IF NOT EXISTS `emergency_agent`
(
    `agent_id`     int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT '主键',
    `agent_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'agent名称',
    `agent_ip`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'IP',
    `agent_port`   int(5)                                                  NOT NULL COMMENT 'port',
    `agent_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT 'agent状态  INACTIVE,READY,BUSY',
    `is_valid`     varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '1' COMMENT '是否生效 0失效 1生效',
    PRIMARY KEY
        (
         `agent_id`
            ) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
$$$

CREATE TABLE
    IF
    NOT EXISTS `emergency_agent_config`
(
    `agent_id`     INT(11) NOT NULL COMMENT 'agent id',
    `agent_config` text    NULL COMMENT 'agent配置',
    PRIMARY KEY (`agent_id`) USING BTREE
) ENGINE = INNODB
  CHARACTER
      SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;
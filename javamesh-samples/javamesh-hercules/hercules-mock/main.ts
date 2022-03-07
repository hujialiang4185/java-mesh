import express from 'express';
import ws from 'ws';
import moment from 'moment'

const app = express()
// WS
const wss1 = new ws.WebSocketServer({ port: 8081 });
let client1: ws
wss1.on('connection', function connection(ws) {
    client1 = ws
});
app.get('/argus-emergency/ws/:message', function (req, res) {
    client1.send(req.params.message)
    res.json()
})

const wss2 = new ws.WebSocketServer({ port: 8080 });
let client2: ws
wss2.on('connection', function connection(ws) {
    client2 = ws
});
app.get('/argus/ws/:message', function (req, res) {
    client2.send(req.params.message)
    res.json()
})
// 其他
app.post('/argus-emergency/api/resource', function (req, res) {
    res.json({
        data: {
            uid: "001"
        }
    })
})
// 用户
const user = {
    nickname: "张三",
    username: "zhangsan",
    role: "管理员",
    group_name: "群组1",
    update_time: "2021-01-01 00:00:00",
    auth: ["admin", "approver", "operator"] // admin, approver, operator
}
app.get('/argus-user/api/user/me', function (req, res) {
    res.json({
        data: user
    })
})
app.get('/argus-user/api/user', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                ...user,
                username: index === 0 ? "admin" : "zhangsan" + index,
                status: ["正常", "失效"][index % 2],
                role: ["管理员", "操作员", "审核员"][index % 3]
            }
        }),
        total: 11
    })
})
app.put('/argus-user/api/user', function (req, res) {
    res.json()
})
const password = {
    username: "zhangsan",
    password: "Adxe12xdrf"
}
app.get('/argus-user/api/user', function (req, res) {
    res.json({
        data: password
    })
})
app.post('/argus-user/api/user/chagnePwd', function (req, res) {
    res.end()
})
app.post('/argus-user/api/user/resetPwd', function (req, res) {
    res.json({
        data: password
    })
})
app.post('/argus-user/api/user/registe', function (req, res) {
    res.json()
})
app.post('/argus-user/api/user/batchActive', function (req, res) {
    res.json()
})
app.post('/argus-user/api/user/batchDeactive', function (req, res) {
    res.json()
})
app.post('/argus-user/api/user/logout', function (req, res) {
    res.json()
})
app.post('/argus-user/api/user/login', function (req, res) {
    res.json()
})
// 性能脚本
app.get('/argus/api/script', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                type: ["folder", "file"][index % 2], script_name: "test" + index + ".groovy",
                commit: "Quick test for http://100.95.133.126:48080/testLongText",
                update_time: "2019-03-19 10:53", version: "224 ", size: "10kb",
                group_name: "分组1", group_id: 1
            }
        }),
        total: 11
    })
})
app.get('/argus/api/script/deleteCheck', function (req, res) {
    res.json({
        data: ["xxx.py", "xxx.py"]
    })
})
app.delete('/argus/api/script', function (req, res) {
    res.json({ msg: "删除失败, 请重试" })
})
app.put('/argus/api/script', function (req, res) {
    res.json({ msg: "更新失败" })
})
app.post('/argus/api/script/check', function (req, res) {
    res.json({
        data: "共一行错误\n第一行错误!"
    })
})
app.get('/argus/api/script/search', function (req, res) {
    res.json({
        data: ["10.1.0.1/TEST_000000000.py", "10.1.0.1/TEST_000000001.py"]
    })
})
app.get('/argus/api/script/get', function (req, res) {
    res.json({
        data: {
            script: `from selenium import webdriver`,
            script_resource: `resource`,
            language: "Groovy"
        }
    })
})
app.get('/argus/api/script/host', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return { host_id: index, domain: index + ".huawei.com" }
        })
    })
})
app.post('/argus/api/script/host', function (req, res) {
    res.json({
        msg: "创建失败"
    })
})
app.delete('/argus/api/script/host', function (req, res) {
    res.json({
        msg: "删除失败"
    })
})
let hostTime = 90000
app.get('/argus/api/script/host/chart', function (req, res) {
    if (!req.query.start) {
        hostTime += 1000
        res.json({
            data: [{
                time: moment(new Date(hostTime)).format("mm:ss"),
                usage: Number(Math.random().toFixed(2)) * 100,
                memory: 100 + Number(Math.random().toFixed(2)) * 100
            }]
        })
    }
    res.json({
        data: Array.from({ length: 91 }, function (_, index) {
            return {
                time: moment(new Date(index * 1000)).format("mm:ss"),
                usage: Number(Math.random().toFixed(2)) * 100,
                memory: 100 + Number(Math.random().toFixed(2)) * 100
            }
        })
    })
})
app.post("/argus/api/script", function (req, res) {
    res.json({
        msg: "创建失败"
    })
})
app.post("/argus/api/script/folder", function (req, res) {
    res.json({
        msg: "创建失败"
    })
})
// 性能场景
app.get('/argus/api/scenario', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                scenario_name: "ARGUS快速场景",
                scenario_type: "动态编排", create_by: "xwx638739",
                create_time: "2019-03-19 10:53", update_time: "2019-03-19 10:53",
                label: ["a", "b", "c"], desc: "描述",
                scenario_id: index, app_name: "ARGUS" + index,
                group_name: "分组1", group_id: 1
            }
        }),
        total: 11
    })
})
app.get('/argus/api/scenario/search', function (req, res) {
    let data = ['场景A', '场景B', '场景C', '场景D']
    if (req.query.value) {
        data = data.filter(item => item.indexOf(String(req.query.value)) !== -1)
    } else {
        data = data.slice(0, 2)
    }
    res.json({
        data
    })
})
app.post('/argus/api/scenario', function (req, res) {
    res.json()
})
app.put('/argus/api/scenario', function (req, res) {
    res.json()
})
app.get("/argus/api/scenario/deleteCheck", function (req, res) {
    res.json({
        data: ["Argus"]
    })
})
app.delete('/argus/api/scenario', function (req, res) {
    res.json({
        msg: "场景被应用, 无法删除"
    })
})
// 性能任务
app.get('/argus/api/task', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                test_id: index,
                status: ["running", "fail", "success", "pending"][index % 4],
                test_name: "Test for 100.95.133.126",
                status_label: "运行中",
                test_type: "快速压测", script_path: "100.95.133.126/traLongText", owner: "admin",
                start_time: "2019-03-19 10:53", duration: "00:01:00",
                tps: "7.5", mtt: "0", fail_rate: "0%",
                label: ["a", "b"], desc: "描述, 长文本长文本长文本长文本长文本长文本"
            }
        }),
        total: 80
    })
})
app.get('/argus/api/task/maxAgent', function (req, res) {
    res.json({
        data: 10
    })
})
app.get('/argus/api/task/tags', function (req, res) {
    res.json({
        data: ['场景A', '场景B', '场景C', '场景D']
    })
})
app.post('/argus/api/task', function (req, res) {
    res.json({
        msg: "创建失败"
    })
})
app.put('/argus/api/task/update', function (req, res) {
    res.json({
        msg: '更新失败'
    })
})
app.get('/argus-emergency/api/task/view', function (req, res) {
    res.json({
        data: {
            test_name: "测试名称",
            status: "running",
            status_label: "运行中",
            label: ["ARGUS", "快速压测", "ARGUS", "性能压测"],
            desc: "LongTextLongTextLongTextLongTextLongTextLongTextLongText...",
            duration: "12:21",
            vuser: 10,
            tps: 2.3,
            tps_peak: 5,
            avg_time: 4535.26,
            test_count: 115,
            success_count: 114,
            fail_count: 1,
            test_comment: "备注文本，长文本长文本",
            log_name: ["anent-NONE-log1.zip", "anent-NONE-log2.zip"],
            progress_message: ["第一行失败", "第二行失败"],
        }
    })
})
app.get('/argus-emergency/api/task/service', function (req, res) {
    res.json({
        data: [
            { transaction: '测试1', tps: 123, response_ms: 12, success_count: 9, fail_count: 1, fail_rate: "10%" }
        ]
    })
})
app.get('/argus-emergency/api/task/resource', function (req, res) {
    res.json({
        data: {
            ip: req.query.ip || "192.168.0.1",
            cpu: 4,
            memory: 8,
            start_up: 1.5,
            cpu_usage: Math.random() / 5 + 0.3,
            memory_usage: Math.random() / 5 + 0.3,
            io_busy: Math.random() / 5 + 0.3,
            cpu_user: Math.floor(Math.random() * 20),
            cpu_sys: Math.floor(Math.random() * 20 + 20),
            cpu_wait: Math.floor(Math.random() * 20 + 50),
            cpu_idle: Math.floor(Math.random() * 20 + 70),
            memory_total: Math.floor(Math.random() * 20),
            memory_swap: Math.floor(Math.random() * 20 + 20),
            memory_buffers: Math.floor(Math.random() * 20 + 50),
            memory_used: Math.floor(Math.random() * 20 + 70),
            disk_read: Math.floor(Math.random() * 20),
            disk_write: Math.floor(Math.random() * 20 + 20),
            disk_busy: Math.floor(Math.random() * 20 + 50),
            network_rbyte: Math.floor(Math.random() * 20),
            network_wbyte: Math.floor(Math.random() * 20 + 20),
            memory_rpackage: Math.floor(Math.random() * 20 + 50),
            memory_wpackage: Math.floor(Math.random() * 20 + 70),
        }
    })
})
app.get('/argus-emergency/api/task/jvm', function (req, res) {
    res.json({
        data: {
            ip: req.query.ip || "192.168.0.1",
            cpu_java: Math.floor(Math.random() * 20 + 70),
            heap_init: Math.floor(Math.random() * 20),
            heap_max: Math.floor(Math.random() * 20 + 20),
            heap_used: Math.floor(Math.random() * 20 + 50),
            heap_committed: Math.floor(Math.random() * 20 + 70),
            memory_init: Math.floor(Math.random() * 20),
            memory_max: Math.floor(Math.random() * 20 + 20),
            memory_used: Math.floor(Math.random() * 20 + 50),
            memory_committed: Math.floor(Math.random() * 20 + 70),
            jvm_cache: Math.floor(Math.random() * 10),
            jvm_newgen: Math.floor(Math.random() * 10 + 15),
            jvm_oldgen: Math.floor(Math.random() * 10 + 30),
            jvm_survivor: Math.floor(Math.random() * 10 + 45),
            jvm_penmgen: Math.floor(Math.random() * 10 + 60),
            jvm_metaspace: Math.floor(Math.random() * 10 + 75),
            gc_newc: Math.floor(Math.random() * 20),
            gc_oldc: Math.floor(Math.random() * 20 + 20),
            gc_news: Math.floor(Math.random() * 20 + 50),
            gc_olds: Math.floor(Math.random() * 20 + 70),
            thread_count: Math.floor(Math.random() * 20),
            thread_daemon: Math.floor(Math.random() * 20 + 20),
            thread_peak: Math.floor(Math.random() * 20 + 50),
        }
    })
})
app.get('/argus-emergency/api/task/search/ip', function (req, res) {
    res.json({
        data: ["192.168.0.1", "192.168.0.2", "192.168.0.3", "192.168.0.4", "192.168.0.5"]
    })
})
app.post('/argus/api/task/pressurePrediction', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                "time": index,
                "pressure": Math.floor(Math.random() * 10)
            }
        })
    })
})
app.delete('/argus/api/task', function (req, res) {
    res.json({
        msg: "删除出错了, 稍后再试"
    })
})
app.post('/argus/api/task/start', function (req, res) {
    res.json()
})
app.post('/argus/api/task/stop', function (req, res) {
    res.json({
        msg: "停止失败"
    })
})
// 性能代理
app.get('/argus/api/agent', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                agent_id: index, status: "running", status_label: "运行中", domain: "192.168.14.187", port: 12002,
                agent_name: "agent", version: "3.4.2", region: "NONE", licensed: false
            }
        })
        ,
        total: 11
    })
})
app.get('/argus/api/agent/get', function (req, res) {
    res.json({
        data: {
            agent_id: 10, status: "running", domain: "192.168.14.187", port: 12002,
            agent_name: "agent", version: "3.4.2", region: "NONE", licensed: true,
            status_label: "运行中"
        }
    })
})
app.get('/argus/api/agent/chart', function (req, res) {
    res.json({
        data: {
            usage: Number(Math.random().toFixed(2)) * 100,
            memory: 100 + Number(Math.random().toFixed(2)) * 100
        }
    })
})
app.delete('/argus/api/agent', function (req, res) {
    res.json({
        msg: "删除失败, 代理运行中"
    })
})
app.post('/argus/api/agent/stop', function (req, res) {
    res.json({
        msg: "停止失败, 无响应"
    })
})
app.post('/argus/api/agent/license', function (req, res) {
    res.json()
})
app.get('/argus/api/agent/link', function (req, res) {
    res.json({
        data: {
            link: 'ngrinder-agent-3.4.2.tar'
        }
    })
})
// 性能报告
app.get("/argus/api/report", function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                report_id: index, test_name: "Test for 100.95.133.126", test_type: "快速压测", test_id: 0,
                owner: "admin", start_time: "2019-03-19 10:53", end_time: "2019-3-19 10:53", duration: "00:01:11"
            }
        }),
        total: 11
    })
})
app.get("/argus/api/report/get", function (req, res) {
    res.json({
        data: {
            test_name: "argus-test 快速压测",
            label: ["ARGUS", "快速压测", "ARGUS", "性能压测"],
            desc: "argus-test快速压测是一个测试压测, 测试压测, 测试压测测试压测, 测试压测",
            agent: 1, sampling_ignore: 0, plugin: 1, target_host: "100.95.133.12",
            start_time: "2019-03-19 10:20:26", test_time: "00:01:00",
            end_time: "2019-03-19 10:21:26", run_time: "00:01:00",
            process: 1, thread: null, vuser: 10, tps: 2.2, tps_max: 4, avg_time: 4535.26,
            test_count: 115, success_count: 115, fail_count: 0
        }
    })
})
app.get("/argus/api/report/chart", function (req, res) {
    res.json({
        data: Array.from({ length: 91 }, function (_, index) {
            return {
                time: moment(new Date(index * 1000)).format("mm:ss"),
                tps: Number(Math.random().toFixed(1)) * 10,
                avg_time: Number(Math.random().toFixed(4)) * 30000,
                receive_avg: Number(Math.random().toFixed(1)) * 10,
                vuser: Number(Math.random().toFixed(1)) * 10,
                fail_count: Number(Math.random().toFixed(1)) * 10,
            }
        })
    })
})
// 容灾脚本
app.get('/argus-emergency/api/script', function (req, res) {
    res.json({
        data: Array.from({ length: Number(req.query.pageSize) }, function (_, index) {
            return {
                script_id: index,
                script_name: index + "run.sh",
                status: ["approved", "approving", "unapproved", "unapproved"][index % 4],
                status_label: ["已审核", "待审核", "新增", "拒绝"][index % 4],
                type: ["GUI", "IDE", "NORMAL"][index % 3],
                owner: "张三",
                submit_info: "xxx",
                create_time: "2021-01-01 00:00:00",
                comment: "脚本不规范",
                has_pwd: "是",
                pwd_from: "本地",
                param: "a,b",
                content: `#!/bin/bash`,
                group_id: 1,
                group_name: "分组1",
                approver: "张三",
                auditable: index === 1
            }
        }),
        total: 11
    })
})
app.delete('/argus-emergency/api/script', function (req, res) {
    res.json({
        msg: "删除失败"
    })
})
app.post('/argus-emergency/api/script', function (req, res) {
    res.json()
})
app.put('/argus-emergency/api/script', function (req, res) {
    res.json()
})
app.post('/argus-emergency/api/script/debug', function (req, res) {
    res.json({
        data: {
            debug_id: 5
        }
    })
})
app.post('/argus-emergency/api/script/debugStop', function (req, res) {
    res.json()
})
let scriptDebugLine = 0
app.get("/argus-emergency/api/script/debugLog", function (req, res) {
    scriptDebugLine++
    if (scriptDebugLine < 10) {
        res.json({
            data: [],
            line: scriptDebugLine
        })
    } else if (scriptDebugLine < 20) {
        res.json({
            data: [scriptDebugLine + "行"],
            line: scriptDebugLine
        })
    } else {
        res.json({
            data: ["最后一行"],
        })
    }
})
app.post('/argus-emergency/api/script/submitReview', function (req, res) {
    res.json()
})
app.post('/argus-emergency/api/script/approve', function (req, res) {
    res.json()
})
app.get('/argus-emergency/api/script/get', function (req, res) {
    res.json({
        data: {
            script_name: "abc.sh",
            owner: "张三",
            submit_info: "xxx",
            create_time: "2021-01-01 00:00:00",
            comment: "脚本不规范",
            has_pwd: "是",
            pwd_from: "本地",
            param: "a,b",
            content: `#!/bin/bash`
        }
    })
})
app.get('/argus-emergency/api/script/search', function (req, res) {
    if (req.query.value) {
        res.json({
            data: [req.query.value + ".sh"]
        })
    }
    res.json({
        data: ["1.sh"]
    })
})
app.get('/argus-emergency/api/script/getByName', function (req, res) {
    res.json({
        data: {
            script_name: "abc.sh",
            owner: "张三",
            submit_info: "xxx",
            create_time: "2021-01-01 00:00:00",
            comment: "脚本不规范",
            has_pwd: "是",
            pwd_from: "本地",
            param: "a,b",
            content: `#!/bin/bash`
        }
    })
})
app.get('/argus-emergency/api/script/ide/get', function (req, res) {
    res.json({
        data: {
            script_name: "abc.sh",
            content: "print('hello')",
            param: "a,b",
            has_resource: true,
            libs: "001/.npmrc 002/xxx"
        }
    })
})
app.post('/argus-emergency/api/script/ide', function (req, res) {
    res.json()
})
app.put('/argus-emergency/api/script/ide', function (req, res) {
    res.json()
})
app.post('/argus-emergency/api/script/orchestrate', function (req, res) {
    res.json({
        data: {
            script_id: 1
        }
    })
})
const orchestrate = {
    data: {
        tree: {
            key: "9639388182803-Root", children: [
                { key: "9639388182804-BeforeProcess" },
                { key: "9639388182805-BeforeThread" },
                {
                    key: "9639388182806-TransactionController", children: [{
                        key: "9639388182812-CSVDataSetConfig"
                    }]
                },
                { key: "9639388182807-AfterProcess" },
                { key: "9639388182808-AfterThread" },
                { key: "9639388182809-TestFunc" },
                { key: "9639388182811-Before" },
                { key: "9639388182812-After" },
            ]
        },
        map: {
            "9639388182803-Root": {
                title: "脚本",
                sampling_interval: 2,
                sampling_ignore: 0,
            },
            "9639388182804-BeforeProcess": { title: "@BeforeProcess" },
            "9639388182805-BeforeThread": { title: "@BeforeThread" },
            "9639388182809-TestFunc": { title: "@Test" },
            "9639388182806-TransactionController": { title: "TransactionController" },
            "9639388182807-AfterProcess": { title: "@AfterProcess" },
            "9639388182808-AfterThread": { title: "@AfterThread" },
            "9639388182811-Before": { title: "@Before" },
            "9639388182812-After": { title: "@After" },
            "9639388182812-CSVDataSetConfig": { title: "CSV数据文件设置", filenames: "001/.npmrc 002/xxx" }
        }
    }
}
app.get('/argus-emergency/api/script/orchestrate/get', function (req, res) {
    res.json(orchestrate)
})
app.put('/argus-emergency/api/script/orchestrate', function (req, res) {
    res.json()
})
app.get('/argus-emergency/api/script/argus/orchestrate', function (req, res) {
    res.json(orchestrate)
})
app.put('/argus-emergency/api/script/argus/orchestrate', function (req, res) {
    res.json({
        data: {
            script: "print('hello')"
        }
    })
})
// 容灾活动
app.get("/argus-emergency/api/plan", function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                plan_id: index,
                plan_no: "CP0" + index,
                plan_name: "A机房XX",
                status: ["approved", "approving", "unapproved", "unapproved", "running", "ran", "ran", "wait"][index % 8],
                status_label: ["已审核", "待审核", "新增", "拒绝", "运行中", "成功", "失败", "预约"][index % 8],
                create_time: "2021-01-01 00:00:00",
                creator: "z30008585",
                comment: "备注, 备注",
                history_id: 1,
                group_id: 1,
                group_name: "分组1",
                auditable: index === 1,
                expand: [
                    { key: 1, scena_no: "C01", scena_name: "场景一", channel_type: "SSH", script_name: "C01.sh", submit_info: "提交信息", test_id: 1 },
                    {
                        key: 2, scena_no: "C01", scena_name: "场景一", task_no: "C01T01", task_name: "任务一", channel_type: "SSH",
                        script_name: "C01T01.sh", submit_info: "提交信息", test_id: 1
                    },
                    {
                        key: 3, scena_no: "C01", scena_name: "场景一", task_no: "C01T01", task_name: "任务一", subtask_no: "C01T01S01",
                        subtask_name: "子任务一", channel_type: "SSH", script_name: "C01T01S01.sh", submit_info: "提交信息", test_id: 1, 
                        user_id: "Username", tag_string: "test, group", start_time: "2017-02-01 10:12:13", duration: 800, tps: 10, mean_test_time: 12.1
                    },
                ]
            }
        }),
        total: 11
    })
})
app.get("/argus-emergency/api/plan/get", function (req, res) {
    res.json({
        data: {
            plan_no: "CP001",
            plan_name: "A机房XX",
        }
    })
})
app.get("/argus-emergency/api/plan/search/status_label", function (req, res) {
    res.json({
        data: ["审核中"]
    })
})
app.post("/argus-emergency/api/plan", function (req, res) {
    res.json({
        data: {
            plan_id: 1
        }
    })
})
app.post("/argus-emergency/api/plan/copy", function (req, res) {
    res.json({
        data: {
            plan_id: 1
        }
    })
})
app.post('/argus-emergency/api/plan/approve', function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/plan/task", function (req, res) {
    res.json({
        data: {
            key: (Math.random() * 10000).toFixed(0),
            submit_info: "提交信息"
        }
    })
})
app.post("/argus-emergency/api/plan/run", function (req, res) {
    res.json({
        data: {
            history_id: 1
        }
    })
})
app.put("/argus-emergency/api/plan/task", function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/plan/submitReview", function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/plan/cancel", function (req, res) {
    res.json()
})
app.put("/argus-emergency/api/plan", function (req, res) {
    res.json()
})
app.get("/argus-emergency/api/plan/task", function (req, res) {
    res.json({
        data: [{
            key: 1,
            title: "场景1",
            task_no: 1,
            task_name: "场景1",
            children: [{
                key: 2,
                title: "任务2",
                task_no: 2,
                task_name: "任务2",
                task_type: "命令行脚本",
                script_name: "1.sh",
                submit_info: "xxx",
                sync: "同步",
                service_id: [{server_id: 0, server_name: "服务名称0", server_ip: "192.168.0.1"}],
                children: [{
                    key: 3,
                    title: "任务3",
                    task_no: 3,
                    task_name: "任务3",
                    task_type: "自定义脚本压测",
                    script_name: "1.sh",
                    submit_info: "xxx",
                    vuser: 5,
                    basic: "by_count",
                    by_count: 100,
                    growth_interval: 4,
                    increment: 2,
                    init_value: 1,
                    init_wait: 3,
                    is_increased: true,
                    sampling_ignore: 10,
                    sampling_interval: 100,
                    test_param: "param",
                    service_id: [{server_id: "1", server_name: "服务名称0", server_ip: "192.168.0.1"}],
                    sync: "同步",
                }]
            }]
        },
        {
            key: 5,
            title: "场景5",
            task_no: 5,
            task_name: "场景5",
        }]
    })
})
// 执行记录
app.get("/argus-emergency/api/history", function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                history_id: index,
                plan_name: "活动名称",
                status: ["运行中", "成功", "失败", "终止"][index % 4],
                creator: "z30008585",
                start_time: "2021-01-01 00:00:00",
                execute_time: "10:00"
            }
        }),
        total: 11
    })
})
app.get("/argus-emergency/api/history/scenario", function (req, res) {
    res.json({
        data: Array.from({ length: 4 }, function (_, index) {
            return {
                key: "key" + index,
                scena_name: "A机房分流, 长文本长文本长文本长文本长文本长文本长文本长文本",
                scena_id: "id" + index,
                status: ['error', 'process', 'finish', 'wait'][index % 4],
                status_label: ["失败", "运行中", "成功", "待执行"][index % 4]
            }
        })
    })
})
app.get('/argus-emergency/api/history/scenario/task', function (req, res) {
    res.json({
        data: Array.from({ length: 4 }, function (_, index) {
            return {
                key: "T" + index,
                task_no: index,
                task_id: index,
                task_name: "A机房分流" + Math.random(),
                operator: "z30008585",
                start_time: "2021-01-01 00:00:00",
                end_time: "2021-01-01 00:00:00",
                sync: "同步",
                status: ['error', 'process', 'finish', 'wait'][index % 4],
                status_label: ["失败", "运行中", "成功", "待执行"][index % 4],
                test_id: index === 0 ? 1 : null
            }
        })
    })
})
app.post("/argus-emergency/api/history/scenario/task/runAgain", function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/history/scenario/task/ensure", function (req, res) {
    res.json()
})
let line = 0
app.get("/argus-emergency/api/history/scenario/task/log", function (req, res) {
    line++
    if (line === 10) {
        res.json({
            data: ["最后一行"]
        })
    }
    res.json({
        data: [line + "行"],
        line
    })
})
const hosts = Array.from({ length: 11 }, function (_, index) {
    return {
        status: ["running", "pending", "success", "fail"][index % 4],
        status_label: ["运行中", "准备中", "成功", "失败"][index % 4],
        server_id: String(index),
        server_name: "服务名称" + index,
        server_ip: "192.168.0.1",
        server_user: "root",
        have_password: "有",
        password_mode: "本地",
        agent_port: "19001",
        licensed: false,
        group_id: 1,
        group_name: "分组1"
    }
})
app.get("/argus-emergency/api/host", function (req, res) {
    const excludes = req.query.excludes as string[]
    const end = Number(req.query.current || 1) * 5
    const data = hosts.filter(function (item) {
        return !excludes?.includes(item.server_id)
    })
    res.json({
        data: data.slice(end - 5, end),
        total: data.length
    })
})
app.get("/argus-emergency/api/host/search", function (req, res) {
    res.json({
        data: ["192.168.0.1"]
    })
})
app.delete("/argus-emergency/api/host", function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/host/license", function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/host", function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/host/install", function (req, res) {
    res.json()
})
app.get("/argus-emergency/api/host/search/password_uri", function (req, res) {
    res.json({
        data: ["root@192.168.0.1"]
    })
})
app.post('/argus/api/script/upload', function (req, res) {
    res.json()
})
app.post("/argus-emergency/api/script/upload", function (req, res) {
    res.json()
})
app.get("/argus-user/api/group", function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                group_id: index,
                group_name: "分组" + index,
                created_by: "zengfan",
                created_time: "2017-01-01 00:00:00"
            }
        })
    })
})
app.post("/argus-user/api/group", function (req, res) {
    res.json()
})
app.delete("/argus-user/api/group", function (req, res) {
    res.json({
        msg: "被使用, 无法删除"
    })
})
app.get("/argus-user/api/group/search", function (req, res) {
    res.json({
        data: ["group1", "group2"]
    })
})
app.get('/argus-user/api/user/approver/search', function (req, res) {
    res.json({
        data: ["user1", "user2"]
    })
})
app.listen(4000)
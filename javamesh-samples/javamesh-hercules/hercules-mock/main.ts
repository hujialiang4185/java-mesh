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
    res.end()
})

const wss2 = new ws.WebSocketServer({ port: 8080 });
let client2: ws
wss2.on('connection', function connection(ws) {
    client2 = ws
});
app.get('/argus/ws/:message', function (req, res) {
    client2.send(req.params.message)
    res.end()
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
                username: "zhangsan" + index,
                status: ["正常", "失效"][index % 2],
                role: ["管理员", "操作员", "审核员"][index % 3]
            }
        }),
        total: 11
    })
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
    res.end()
})
app.post('/argus-user/api/user/batchActive', function (req, res) {
    res.end()
})
app.post('/argus-user/api/user/batchDeactive', function (req, res) {
    res.end()
})
// 性能脚本
app.get('/argus/api/script', function (req, res) {
    res.json({
        data: Array.from({ length: 10 }, function (_, index) {
            return {
                type: ["folder", "file"][index % 2], script_name: "100.95.133.126",
                commit: "Quick test for http://100.95.133.126:48080/testLongText",
                update_time: "2019-03-19 10:53", version: "224 ", size: "10kb"
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
app.get('/argus/api/user/registe', function (req, res) {
    res.json()
})
app.get('/argus/api/user/registe', function (req, res) {
    res.json()
})
app.get('/argus/api/user/registe', function (req, res) {
    res.json()
})
app.get('/argus/api/user/registe', function (req, res) {
    res.json()
})
app.get('/argus/api/user/registe', function (req, res) {
    res.json()
})
app.get('/argus/api/user/registe', function (req, res) {
    res.json()
})
app.listen(4000)
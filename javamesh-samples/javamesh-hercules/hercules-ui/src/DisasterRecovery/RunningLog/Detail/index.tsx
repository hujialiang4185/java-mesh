import React, { useEffect, useRef, useState } from "react"
import { Button, Descriptions, Divider, Form, message, Modal, Radio, Steps, Table } from "antd"
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import socket from "../../socket"
import "./index.scss"
import axios from "axios"
import { Link, Route, Switch, useLocation, useRouteMatch } from "react-router-dom"
import Editor from "@monaco-editor/react";
import { debounce } from 'lodash';
import TaskView from "../../../component/TaskView"

export default function App() {
    const { path } = useRouteMatch();
    return <Switch>
        <Route exact path={path} component={Home} />
        <Route exact path={path + '/Report'}><TaskView /></Route>
    </Switch>
}

type Scena = { key: string, scena_name: string, scena_id: string, status: 'wait' | 'process' | 'finish' | 'error', status_label: string }
type Task = { key: string, status: string, test_id: string }
function Home() {
    let submit = false
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const history_id = urlSearchParams.get("history_id") || ""
    const [scenaList, setScenaList] = useState<Scena[]>([])
    const [taskList, setTaskList] = useState<Task[]>()
    const [loading, setLoading] = useState(false)
    const [current, setCurrent] = useState(0)
    const scenaKeysRef = useRef<string[]>([])
    const taskKeysRef = useRef<string[]>([])
    const scenaIdRef = useRef<string>()

    async function loadTask(history_id: string) {
        setLoading(true)
        try {
            const res = await axios.get("/argus-emergency/api/history/scenario/task", { params: { history_id, scena_id: scenaIdRef.current } })
            const data = res.data.data
            taskKeysRef.current = data.map(function (item: { key: string }) { return "/task/" + item.key })
            setTaskList(data)
        } catch (error: any) {
            message.error(error.message)
        }
        setLoading(false)
    }
    useEffect(function () {
        async function loadScena(history_id: string) {
            try {
                const res = await axios.get("/argus-emergency/api/history/scenario", { params: { history_id } })
                const data = res.data.data
                if (data.length === 0) return
                scenaKeysRef.current = data.map(function (item: { key: string }) { return "/scena/" + item.key })
                setScenaList(data)
                if (!scenaIdRef.current) scenaIdRef.current = data[0].scena_id
                loadTask(history_id)
            } catch (error: any) {
                message.error(error.message)
            }
        }
        loadScena(history_id);

        const dbLoadScena = debounce(loadScena, 1000)
        const dbLoadTask = debounce(loadTask, 1000)
        function handleSocket(event: MessageEvent<any>) {
            const message = event.data
            if (scenaKeysRef.current.includes(message)) {
                // 可能收到多条消息, 只响应最新的
                dbLoadScena(history_id)
            } else if (taskKeysRef.current.includes(message)) {
                // 停止响应任务更新
                dbLoadTask(history_id);
            }
        }
        socket.addEventListener("message", handleSocket)
        return function () {
            socket.removeEventListener("message", handleSocket)
        }
    }, [history_id])
    const [data, setData] = useState({ plan_no: "", plan_name: "" })
    useEffect(function () {
        (async function () {
            try {
                const res = await axios.get("/argus-emergency/api/history/get", { params: { history_id } })
                setData(res.data.data)
            } catch (error: any) {
                message.error(error.message)
            }
        })()
    }, [history_id])
    return <div className="RunningLogDetail">
        <Breadcrumb label="执行记录" sub={{ label: "详细信息", parentUrl: "/PerformanceTest/RunningLog" }} />
        <Card>
            <Descriptions className="Desc">
                <Descriptions.Item label="项目编号">{data.plan_no}</Descriptions.Item>
                <Descriptions.Item label="项目名称">{data.plan_name}</Descriptions.Item>
            </Descriptions>
            <Steps current={current} className="Steps" size="small" type="navigation" onChange={function (current) {
                setCurrent(current)
                scenaIdRef.current = scenaList[current]!.scena_id
                loadTask(history_id)
            }}>{scenaList.map(function (item, index) {
                return <Steps.Step key={item.key} status={item.status} title={item.scena_name} description={item.status_label} />
            })}</Steps>
            <Divider />
            <Table size="middle" rowKey="key" loading={loading} dataSource={taskList} pagination={false}
                columns={[
                    {
                        title: "编号", dataIndex: "task_no", ellipsis: true
                    },
                    {
                        title: "任务名称", dataIndex: "task_name", ellipsis: true
                    },
                    {
                        title: "操作员", dataIndex: "operator", ellipsis: true
                    },
                    {
                        title: "开始时间", dataIndex: "start_time", ellipsis: true
                    },
                    {
                        title: "结束时间", dataIndex: "end_time", ellipsis: true
                    },
                    {
                        title: "执行状态", dataIndex: "status_label", ellipsis: true
                    },
                    {
                        title: "执行方式", dataIndex: "sync", ellipsis: true
                    },
                    {
                        title: "操作", width: 300, dataIndex: "key", align: "center", render(key, record) {
                            return <>
                                <Button type="primary" disabled={record.status !== "error"} size="small" onClick={async function () {
                                    if (submit) return
                                    submit = true
                                    try {
                                        await axios.post("/argus-emergency/api/history/scenario/task/runAgain", { history_id, key })
                                        message.success("执行成功")
                                        await loadTask(history_id)
                                    } catch (error: any) {
                                        message.error(error.message)
                                    }
                                    submit = false
                                }}>重新执行</Button>
                                <TaskConfirm record={record} load={function () {
                                    loadTask(history_id)
                                }} />
                                <TaskLog record={record} />
                                <TaskReport record={record} />
                            </>
                        }
                    },
                ]}
            />
        </Card>
    </div>
}

function TaskReport(props: { record: Task }) {
    const [data, setData] = useState<{ test_id?: string }[]>([]);
    const { path } = useRouteMatch();
    return <>
        <Button disabled={!props.record.test_id} type="primary" size="small" onClick={async function () {
            try {
                const res = await axios.get("/argus-emergency/api/task/scenario/report", {params: {key: props.record.key}})
                setData(res.data.data)
            } catch (error: any) {
                message.error(error.message)
            }
        }}>报告</Button>
        <Modal className="TaskReport" title="报告列表" width={1200} visible={data.length > 0} maskClosable={false} footer={null} onCancel={function () { setData([]) }}>
            <Table size="small" rowKey="server_id" dataSource={data} columns={[
                { title: "测试名称", dataIndex: "test_name", ellipsis: true },
                { title: "状态", dataIndex: "status_label", ellipsis: true },
                { title: "主机名称", dataIndex: "server_name", ellipsis: true },
                { title: "服务器IP", dataIndex: "server_ip", ellipsis: true },
                { title: "运行时间(s)", dataIndex: "duration", ellipsis: true },
                { title: "虚拟用户数", dataIndex: "vuser", ellipsis: true },
                { title: "TPS", dataIndex: "tps", ellipsis: true },
                { title: "TPS峰值", dataIndex: "tps_peak", ellipsis: true },
                { title: "平均时间(ms)", dataIndex: "avg_time", ellipsis: true },
                { title: "执行测试数量", dataIndex: "test_count", ellipsis: true },
                { title: "测试成功数量", dataIndex: "success_count", ellipsis: true },
                { title: "错误", dataIndex: "fail_count", ellipsis: true },
                {
                    title: "操作", dataIndex: "test_id", width: 80,
                    render(test_id) {
                        return <Button disabled={!test_id} type="primary" size="small">
                            <Link to={path + "/Report?test_id=" + test_id}>报告</Link>
                        </Button>
                    }
                }
            ]} />
        </Modal>
    </>
}

function TaskConfirm(props: { record: Task, load: () => void }) {
    let submit = false
    const [isModalVisible, setIsModalVisible] = useState(false);
    return <>
        <Button type="primary" disabled={props.record.status !== "error"} size="small" onClick={function () { setIsModalVisible(true) }}>人工确认</Button>
        <Modal className="TaskConfirm" title="人工确认" width={400} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            <Form initialValues={{ confirm: "成功" }} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    const data = { ...values, key: props.record.key }
                    await axios.post("/argus-emergency/api/history/scenario/task/ensure", data)
                    message.success("提交成功")
                    setIsModalVisible(false)
                } catch (error: any) {
                    message.error(error.message)
                }
                submit = false
            }}>
                <Form.Item name="confirm" label="执行结果">
                    <Radio.Group options={["成功", "失败"]} />
                </Form.Item>
                <Form.Item className="Buttons">
                    <Button type="primary" htmlType="submit">提交</Button>
                    <Button onClick={function () {
                        setIsModalVisible(false)
                    }}>取消</Button>
                </Form.Item>
            </Form>
        </Modal>
    </>
}

function TaskLog(props: { record: Task }) {
    let submit = false
    const [isModalVisible, setIsModalVisible] = useState(false)
    const timeIntervalRef = useRef(0)
    const [data, setData] = useState<string[]>([])
    async function load(key: string, line?: number) {
        try {
            const params = { key, line }
            const res = await axios.get('/argus-emergency/api/history/scenario/task/log', { params })
            setData(function (data) {
                return data.concat(res.data.data).slice(-10000)
            })
            return res.data.line as number
        } catch (error: any) {
            message.error(error.message)
        }
    }
    useEffect(function () {
        return function () {
            clearInterval(timeIntervalRef.current)
        }
    }, [])
    return <>
        <Button disabled={!!props.record.test_id} type="primary" size="small" onClick={async function () {
            if (submit) return
            submit = true
            let line = await load(props.record.key)
            submit = false
            setIsModalVisible(true)
            // 设置定时器
            if (!line) return
            timeIntervalRef.current = setInterval(async function () {
                line = await load(props.record.key, line)
                if (!line) {
                    clearInterval(timeIntervalRef.current)
                }
            }, 1000) as any
        }}>日志</Button>
        {isModalVisible && <Modal className="LogButton" title="查看日志" width={1200} visible={true} maskClosable={false} footer={null} onCancel={function () {
            clearInterval(timeIntervalRef.current)
            setIsModalVisible(false)
            // 清理
            setData([])
        }}>
            <Editor height={620} language="plaintext" options={{ readOnly: true }} value={data.join("\n")} />
        </Modal>}
    </>
}

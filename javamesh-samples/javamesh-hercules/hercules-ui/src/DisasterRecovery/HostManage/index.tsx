import { Button, Form, Input, InputNumber, message, Modal, Radio, Table } from "antd"
import React, { Key, useContext, useEffect, useRef, useState } from "react"
import Breadcrumb from "../../component/Breadcrumb"
import Card from "../../component/Card"
import { SearchOutlined, PlusOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import "./index.scss"
import ServiceSelect from "../../component/ServiceSelect"
import axios from "axios"
import { useForm } from "antd/lib/form/Form"
import { debounce } from "lodash"
import socket from "../socket"
import Context from "../../ContextProvider"
import Editor from "@monaco-editor/react"

type Data = { server_id: string, agent_status: string, agent_status_label: string, agent_type: string, agent_id: string }

export default function App() {
    let submit = false
    const [data, setData] = useState<{ data: Data[], total: number }>({ data: [], total: 0 })
    const [loading, setLoading] = useState(false)
    const stateRef = useRef<any>({})
    const keysRef = useRef<Key[]>([])
    const { auth } = useContext(Context)
    async function load() {
        setLoading(true)
        try {
            const params = {
                pageSize: stateRef.current.pagination?.pageSize || 10,
                current: stateRef.current.pagination?.current,
                sorter: stateRef.current.sorter?.field,
                order: stateRef.current.sorter?.order,
                ...stateRef.current.search,
                ...stateRef.current.filters
            }
            const res = await axios.get("/argus-emergency/api/host", { params })
            setData(res.data)
            // setSelectedRowKeys([])
            // 需要监听的任务列表
            keysRef.current = res.data.data.map(function (item: Data) {
                return "/host/" + item.server_id
            })
        } catch (error: any) {
            message.error(error.message)
        }
        setLoading(false)
    }
    function batchDelete(selectedRowKeys: React.Key[]) {
        if (submit) return
        submit = true
        Modal.confirm({
            title: '是否删除?',
            icon: <ExclamationCircleOutlined />,
            content: '删除后无法恢复, 请谨慎操作',
            okType: 'danger',
            async onOk() {
                try {
                    await axios.delete("/argus-emergency/api/host", { params: { server_id: selectedRowKeys } })
                    message.success("删除成功")
                } catch (e: any) {
                    message.error(e.message)
                }
                load()
            },
        })
        submit = false
    }
    async function batchInstall(selectedRowKeys: React.Key[]) {
        if (submit) return
        submit = true
        Modal.confirm({
            title: '是否安装?',
            icon: <ExclamationCircleOutlined />,
            content: '将执行安装代理, 请谨慎操作',
            okType: 'danger',
            async onOk() {
                try {
                    await axios.post("/argus-emergency/api/host/install", { server_id: selectedRowKeys })
                    message.success("安装已提交,请稍等")
                } catch (e: any) {
                    message.error(e.message)
                }
                load()
            },
        })
        submit = false
    }
    useEffect(function () {
        load()
        const dbLoad = debounce(load, 1000)
        function handleSocket(event: MessageEvent<any>) {
            const message = event.data
            if (keysRef.current.includes(message)) {
                // 只响应最新
                dbLoad()
            }
        }
        socket.addEventListener("message", handleSocket)
        return function () {
            socket.removeEventListener("message", handleSocket)
        }
    }, [])
    const statusMap = new Map<string, string>()
    statusMap.set("PROGRESSING", "#1A99FE")
    statusMap.set("INACTIVE", "#8090B0")
    statusMap.set("READY", "#2BBF2A")
    statusMap.set("ERROR", "#FF4E4E")
    return <div className="HostManage">
        <Breadcrumb label="引擎管理" />
        <Card>
            <div className="ToolBar">
                <AddHost load={load} />
                <div className="Space"></div>
                <Form layout="inline" onFinish={function (values) {
                    stateRef.current.search = values
                    load()
                }}>
                    <Form.Item name="keywords">
                        <Input placeholder="Keywords" />
                    </Form.Item>
                    <Button htmlType="submit" icon={<SearchOutlined />}>查找</Button>
                </Form>
            </div>
            <Table size="middle" rowKey="id" loading={loading} dataSource={data.data}
                pagination={{ total: data.total, size: "small", showTotal() { return `共 ${data.total} 条` }, showSizeChanger: true }}
                onChange={function (pagination, filters, sorter) {
                    stateRef.current = { ...stateRef.current, pagination, filters, sorter }
                    load()
                }}
                columns={[
                    {
                        title: "状态",
                        dataIndex: "agent_status",
                        render(_, record) {
                            return <div title={record.agent_status_label}>
                                <span className="icon md"
                                    style={{ fontSize: 24, color: statusMap.get(record.agent_status) }}>lightbulb_outline</span>
                            </div>
                        },
                        align: "center",
                        width: 80,
                        sorter: true,
                        ellipsis: true
                    },
                    {
                        title: "许可(单击可切换)",
                        dataIndex: "licensed",
                        align: "center",
                        width: 200,
                        render(licensed, record) {
                            if (licensed === undefined) return null
                            return <span className={`Licensed${licensed === true ? " active" : " deactive"}`} onClick={async function () {
                                if (submit) return
                                submit = true
                                try {
                                    await axios.post('/argus-emergency/api/host/license', { server_id: record.server_id, licensed: !licensed })
                                    message.success("修改成功")
                                    await load()
                                } catch (error: any) {
                                    message.error(error.message)
                                }
                                submit = false
                            }}>
                                <span>未许可</span>
                                <span>已许可</span>
                            </span>
                        }
                    },
                    { ellipsis: true, title: "服务器名称", dataIndex: "server_name" },
                    { ellipsis: true, title: "服务器IP", dataIndex: "server_ip" },
                    { ellipsis: true, title: "服务器内存(MB)", dataIndex: "server_memory" },
                    { ellipsis: true, title: "SSH用户", dataIndex: "server_user" },
                    { ellipsis: true, title: "有无密码", dataIndex: "have_password" },
                    { ellipsis: true, title: "密码获取", dataIndex: "password_mode" },
                    { ellipsis: true, title: "Agent名称", dataIndex: "agent_name" },
                    { ellipsis: true, title: "Agent端口", dataIndex: "agent_port" },
                    { ellipsis: true, title: "Agent类型", dataIndex: "agent_type_label" },
                    { ellipsis: true, title: "分组", dataIndex: "group_name" },
                    {
                        title: "操作", width: 150, dataIndex: "server_id", render(server_id, record) {
                            return <>
                                {auth.includes("operator") && <Button type="link" size="small" onClick={function () {
                                    batchDelete([server_id])
                                }}>删除</Button>}
                                {auth.includes("operator") && record.agent_type === "gui" && <ConfigHost agent_id={record.agent_id}/>}
                                {auth.includes("operator") && !record.agent_type && <Button type="link" size="small" onClick={function () {
                                    batchInstall([server_id])
                                }}>安装Agent</Button>}
                            </>
                        }
                    }
                ]}
            />
        </Card>
    </div>
}

function ConfigHost(props: {agent_id: string}) {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [form] = Form.useForm()
    return <>
        <Button type="link" size="small" onClick={async function () {
            try {
                const res = await axios.get("/argus-emergency/api/host/agent_config", {params: {agent_id: props.agent_id}})
                form.setFieldsValue(res.data.data)
                setIsModalVisible(true)
            } catch (error: any) {
                message.error(error.message)
            }
            
        }}>修改配置</Button>
        <Modal className="ConfigHost" width={750} title="修改配置" visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () { setIsModalVisible(false) }}>
            <Form form={form} onFinish={async function (value) {
                try {
                    JSON.parse(value.agent_config)
                    await axios.post("/argus-emergency/api/host/agent_config", {agent_id: props.agent_id, agent_config: value.agent_config})
                    message.success("修改成功")
                    setIsModalVisible(false)
                } catch (error: any) {
                    message.error(error.message)
                }
            }}>
                <Form.Item label="脚本内容" className="Editor WithoutLabel" name="agent_config">
                    <Editor language="json" height={450} />
                </Form.Item>
                <Form.Item className="Buttons">
                    <Button className="Save" htmlType="submit" type="primary">提交</Button>
                    <Button onClick={function () {
                        setIsModalVisible(false)
                    }}>取消</Button>
                </Form.Item>
            </Form>
        </Modal>
    </>
}

function AddHost(props: { load: () => void }) {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [hasPwd, setHasPwd] = useState(false)
    const [isLocal, setIsLocal] = useState(true)
    const [form] = useForm()
    const { auth } = useContext(Context)
    return <>
        <Button disabled={!auth.includes("operator")} type="primary" icon={<PlusOutlined />} onClick={function () { setIsModalVisible(true) }}>添加服务器</Button>
        <Modal className="AddHost" title="添加服务器" width={750} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () { setIsModalVisible(false) }}>
            <Form form={form} labelCol={{ span: 4 }}
                initialValues={{ have_password: "无", password_mode: "本地", server_port: 22 }}
                onFinish={async function (values) {
                    try {
                        await axios.post("/argus-emergency/api/host", values)
                        form.resetFields()
                        setIsModalVisible(false)
                        props.load()
                        message.success("创建成功")
                        setHasPwd(false)
                        setIsLocal(true)
                    } catch (error: any) {
                        message.error(error.message)
                    }
                }}
            >
                <div className="Line">
                    <Form.Item labelCol={{ span: 8 }} name="server_name" label="服务器名称" rules={[{ required: true, max: 32 }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item labelCol={{ span: 8 }} name="server_memory" label="内存大小(MB)" rules={[{ type: "integer" }]}>
                        <InputNumber min={0} />
                    </Form.Item>
                </div>
                <div className="Line">
                    <Form.Item labelCol={{ span: 8 }} name="server_ip" label="服务器IP" rules={[{
                        required: true,
                        pattern: /^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/,
                        message: "请输入IP地址"
                    }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item labelCol={{ span: 8 }} name="server_port" label="端口" rules={[{ type: "integer", required: true }]}>
                        <InputNumber min={0} max={65535} />
                    </Form.Item>
                </div>
                <Form.Item name="group_name" label="分组">
                    <ServiceSelect allowClear url="/argus-user/api/group/search" />
                </Form.Item>
                <Form.Item name="have_password" label="有无密码">
                    <Radio.Group options={["无", "有"]} onChange={function (e) {
                        setHasPwd(e.target.value === "有")
                    }} />
                </Form.Item>
                {hasPwd && <Form.Item name="password_mode" label="密码获取">
                    <Radio.Group options={["本地", "平台"]} onChange={function (e) {
                        setIsLocal(e.target.value === "本地")
                    }} />
                </Form.Item>}
                {hasPwd && !isLocal && <Form.Item name="password_uri" label="密码平台" rules={[{ required: true }]}>
                    <ServiceSelect url="/argus-emergency/api/host/search/password_uri" />
                </Form.Item>}
                {hasPwd && isLocal && <Form.Item name="server_user" label="SSH用户" rules={[{ required: true, max: 32 }]}>
                    <Input />
                </Form.Item>}
                {hasPwd && isLocal && <Form.Item name="password" label="密码" rules={[{ required: true, max: 32 }]}>
                    <Input />
                </Form.Item>}
                <Form.Item className="Buttons">
                    <Button type="primary" htmlType="submit">创建</Button>
                    <Button onClick={function () {
                        setIsModalVisible(false)
                    }}>取消</Button>
                </Form.Item>
            </Form>
        </Modal>
    </>
}
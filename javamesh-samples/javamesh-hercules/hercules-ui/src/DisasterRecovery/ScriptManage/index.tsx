import { Button, Descriptions, Form, Input, message, Modal, Radio, Table } from "antd"
import React, { useContext, useEffect, useRef, useState } from "react"
import Breadcrumb from "../../component/Breadcrumb"
import Card from "../../component/Card"
import { CloseOutlined, SearchOutlined, PlusOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import axios from "axios"
import CacheRoute, { CacheSwitch, useDidRecover } from 'react-router-cache-route'
import { Link, Route, useHistory, useRouteMatch } from "react-router-dom"
import GUI from "./GUI"
import "./index.scss"
import Editor from "@monaco-editor/react"
import Context from "../../ContextProvider"
import ApproveFormItems from "../ApproveFormItems"
import NORMAL from "./NORMAL"
import NORMALCreate from "./NORMALCreate"
import { useForm } from "antd/lib/form/Form"
import ServiceSelect from "../../component/ServiceSelect"
import IDE from "./IDE"
import IDECreate from "./IDECreate"

export default function App() {
    const { path } = useRouteMatch();
    return <CacheSwitch>
        <CacheRoute exact path={path} component={Home} />
        <Route exact path={path + '/IDECreate'}><IDECreate /></Route>
        <Route exact path={path + '/NORMALCreate'}><NORMALCreate /></Route>
        <Route exact path={path + '/NORMAL'}><NORMAL /></Route>
        <Route exact path={path + '/GUI'}><GUI /></Route>
        <Route exact path={path + '/IDE'}><IDE /></Route>
    </CacheSwitch>
}

type Data = {
    script_id: string, script_name: string, param: string,
    owner: string, status: string, submit_info: string,
    has_pwd: string, pwd_from: string, content: string,
    type: string, group_id: string, auditable: string
}
function Home() {
    let submit = false
    const { path } = useRouteMatch();
    const { auth } = useContext(Context)
    const [data, setData] = useState<{ data: Data[], total: number }>({ data: [], total: 0 })
    const [loading, setLoading] = useState(false)
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
    const stateRef = useRef<any>({})
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
            try {
                const res = await axios.get('/argus-emergency/api/script', { params })
                setData(res.data)
            } catch (error: any) {
                message.error(error.message)
            }
        } catch (e: any) {
            message.error(e.message)
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
                    await axios.delete("/argus-emergency/api/script", { params: { script_id: selectedRowKeys.join(",") } })
                    message.success("删除成功")
                    load()
                } catch (e: any) {
                    message.error(e.message)
                    throw e
                }
            },
        })
        submit = false
    }
    useEffect(function () {
        load()
    }, [])
    useDidRecover(load)

    return <div className="ScriptManage">
        <Breadcrumb label="脚本管理" />
        <Card>
            <div className="ToolBar">
                <AddScript />
                <Button icon={<CloseOutlined />} onClick={function () {
                    if (selectedRowKeys.length === 0) {
                        return
                    }
                    batchDelete(selectedRowKeys)
                }}>批量删除</Button>
                <div className="Space"></div>
                <Form layout="inline" onFinish={function (values) {
                    stateRef.current.search = values
                    load()
                }}>
                    <Form.Item name="script_name">
                        <Input placeholder="脚本名称" />
                    </Form.Item>
                    <Form.Item name="owner">
                        <Input placeholder="脚本归属" />
                    </Form.Item>
                    <Button htmlType="submit" icon={<SearchOutlined />}>查找</Button>
                </Form>
            </div>
            <Table size="middle" dataSource={data.data} loading={loading} rowKey="script_id"
                rowSelection={{
                    selectedRowKeys, onChange(selectedRowKeys) {
                        setSelectedRowKeys(selectedRowKeys)
                    }
                }}
                onChange={function (pagination, filters, sorter) {
                    stateRef.current = { ...stateRef.current, pagination, filters, sorter }
                    load()
                }}
                pagination={{ total: data.total, size: "small", showTotal() { return `共 ${data.total} 条` }, showSizeChanger: true }}
                columns={[
                    {
                        title: "脚本名称",
                        dataIndex: "script_name",
                        sorter: true,
                        width: 240,
                        ellipsis: true
                    },
                    {
                        title: "脚本归属",
                        dataIndex: "owner",
                        ellipsis: true
                    },
                    {
                        title: "脚本类型",
                        dataIndex: "type",
                        ellipsis: true,
                        render(value) {
                            switch (value) {
                                case "GUI":
                                    return "GUI脚本"
                                case "IDE":
                                    return "非GUI脚本"
                                case "NORMAL":
                                    return "非压测脚本"
                            }
                        }
                    },
                    {
                        title: "脚本用途",
                        dataIndex: "submit_info",
                        ellipsis: true
                    },
                    {
                        title: "脚本状态",
                        dataIndex: "status_label",
                        ellipsis: true
                    },
                    {
                        title: "审核人",
                        dataIndex: "approver",
                        ellipsis: true
                    },
                    {
                        title: "创建时间",
                        width: 200,
                        dataIndex: "create_time",
                        ellipsis: true
                    },
                    {
                        title: "备注",
                        dataIndex: "comment",
                        ellipsis: true
                    },
                    {
                        title: "分组",
                        dataIndex: "group_name",
                        ellipsis: true
                    },
                    {
                        title: "操作",
                        width: 200,
                        dataIndex: "script_id",
                        render(script_id, record) {
                            return <>
                                <ViewScript data={record} />
                                {auth.includes("operator") && <Button type="link" size="small" onClick={function () {
                                    batchDelete([script_id])
                                }}>删除</Button>}
                                {auth.includes("operator") && <Link to={
                                    path + "/" + record.type + "?script_id=" + script_id
                                }>
                                    <Button type="link" size="small">修改</Button>
                                </Link>}
                                {record.auditable && <ApproveScript key="approve" data={record} load={load} />}
                                {record.status === "unapproved" && auth.includes("operator") && <SubmitReview load={load} script_id={script_id} group_id={record.group_id} />}
                            </>
                        }
                    },
                ]} />
        </Card>
    </div>
}

function SubmitReview(props: { load: () => void, script_id: string, group_id: string }) {
    const [isModalVisible, setIsModalVisible] = useState(false)
    const [form] = useForm()
    return <>
        <Button type="link" size="small" onClick={function () { setIsModalVisible(true) }}>提审</Button>
        <Modal className="SubmitScriptReview" title="提交审核" visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () { setIsModalVisible(false) }}>
            <Form form={form} requiredMark={false} labelCol={{ span: 4 }} onFinish={async function (values) {
                try {
                    await axios.post('/argus-emergency/api/script/submitReview', { ...values, script_id: props.script_id })
                    message.success("提交成功")
                    setIsModalVisible(false)
                    form.resetFields()
                    props.load()
                } catch (error: any) {
                    message.error(error.message)
                }
            }}>
                <Form.Item name="approver" label="审批人" rules={[{ required: true }]}>
                    <ServiceSelect url='/argus-user/api/user/approver/search' />
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

function ViewScript(props: { data: Data }) {
    const [isModalVisible, setIsModalVisible] = useState(false);
    return <>
        <Button type="link" size="small" onClick={function () { setIsModalVisible(true) }}>查看</Button>
        <Modal title="查看脚本" width={950} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            {isModalVisible && <ScriptDetail data={props.data} />}
        </Modal>
    </>
}

function ApproveScript(props: { data: Data, load: () => {} }) {
    const [isModalVisible, setIsModalVisible] = useState(false);
    return <>
        <Button type="link" size="small" onClick={function () { setIsModalVisible(true) }}>审核</Button>
        <Modal className="ApproveScript" title="审核脚本" width={950} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            {isModalVisible && <ScriptDetail data={props.data} />}
            <Form className="Form" requiredMark={false} onFinish={async function (values) {
                try {
                    await axios.post("/argus-emergency/api/script/approve", { ...values, script_id: props.data.script_id })
                    props.load()
                } catch (error: any) {
                    message.error(error.message)
                }
            }}>
                <ApproveFormItems />
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

function ScriptDetail(props: { data: Data }) {
    return <div className="ScriptDetail">
        <Descriptions className="Desc">
            <Descriptions.Item label="脚本名称">{props.data.script_name}</Descriptions.Item>
            <Descriptions.Item label="脚本归属">{props.data.owner}</Descriptions.Item>
            <Descriptions.Item label="脚本用途">{props.data.submit_info}</Descriptions.Item>
        </Descriptions>
        <div className="Editor">
            <Editor height={300} language="shell" options={{ readOnly: true }} value={props.data.content} />
        </div>
    </div>
}

function AddScript() {
    let submit = false
    const { auth } = useContext(Context)
    const history = useHistory();
    const { path } = useRouteMatch()
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [normal, setNormal] = useState(false)
    const [form] = useForm()
    return <>
        <Button disabled={!auth.includes("operator")} type="primary" icon={<PlusOutlined />} onClick={function () { setIsModalVisible(true) }}>添加脚本</Button>
        <Modal className="AddScript" title="添加脚本" width={950} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            <Form form={form} requiredMark={false} labelCol={{ span: 2 }} initialValues={{ public: "私有", type: "压测脚本", orchestrate_type: "GUI", language: "Shell" }} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    if (values.orchestrate_type === "GUI") {
                        const res = await axios.post("/argus-emergency/api/script/orchestrate", values)
                        history.push(`${path}/GUI?script_id=${res.data.data.script_id}`)
                    } else {
                        history.push(`${path}/${values.type === "压测脚本" ? "IDECreate" : "GUICreate"}`, values)
                    }
                    setIsModalVisible(false)
                    form.resetFields()
                } catch (error: any) {
                    message.error(error.message)
                }
                submit = false
            }}>
                <div className="Line">
                    <Form.Item className="Middle" labelCol={{ span: 4 }} name="script_name" label="脚本名" rules={[
                        { min: 3, max: 20, required: true, whitespace: true },
                        { pattern: /^[A-Za-z][\w.-]+$/, message: "格式不正确" }
                    ]}>
                        <Input placeholder='必须以字母开头, 可以包括字母, 数字和 ._- 最短3位, 最长20位'/>
                    </Form.Item>
                    <Form.Item className="Middle" labelCol={{ span: 4 }} name="group_name" label="分组">
                        <ServiceSelect allowClear url="/argus-user/api/group/search" />
                    </Form.Item>
                </div>
                <Form.Item label="脚本用途" name="submit_info" rules={[{ required: true }]}>
                    <Input.TextArea maxLength={50} showCount autoSize={{ minRows: 2, maxRows: 2 }} />
                </Form.Item>
                <div className="Line">
                    <Form.Item className="Middle" labelCol={{ span: 4 }} name="type" label="脚本类型">
                        <Radio.Group options={["压测脚本", "非压测脚本"]} onChange={function(e) {
                            setNormal(e.target.value === "非压测脚本")
                        }}/>
                    </Form.Item>
                    {normal ? <Form.Item className="Middle" labelCol={{ span: 4 }} name="language" label="脚本类型">
                        <Radio.Group options={["Shell", "Python", "Jython"]} />
                    </Form.Item> : <Form.Item className="Middle" labelCol={{ span: 4 }} name="orchestrate_type" label="编排方式">
                        <Radio.Group options={["GUI", "非GUI"]} />
                    </Form.Item>}
                </div>
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

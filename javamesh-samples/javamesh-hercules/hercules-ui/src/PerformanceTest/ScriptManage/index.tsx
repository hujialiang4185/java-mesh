import { Button, Checkbox, Collapse, Form, Input, message, Modal, Select, Table } from "antd"
import React, { useEffect, useRef, useState } from "react"
import Breadcrumb from "../../component/Breadcrumb"
import Card from "../../component/Card"
import { CloseOutlined, SearchOutlined, FileTextOutlined, FolderOpenFilled, CloudUploadOutlined, PlusCircleOutlined, MinusCircleOutlined, InfoCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import axios from "axios"
import CacheRoute, { CacheSwitch, useDidRecover } from 'react-router-cache-route'
import { Link, Route, useRouteMatch } from "react-router-dom"
import Detail from "./Detail"
import "./index.scss"
import Upload from "../../component/Upload"
import ServiceSelect from "../../component/ServiceSelect"

export default function App() {
    const { path } = useRouteMatch();
    return <CacheSwitch>
        <CacheRoute exact path={path} component={Home} />
        <Route exact path={`${path}/Detail`}><Detail /></Route>
    </CacheSwitch>
}

type Data = { type: string, script_name: string, commit: string, last_update: string }
function Home() {
    let submit = false
    const { path } = useRouteMatch();
    const [data, setData] = useState<{ data: Data[], total: number }>({ data: [], total: 0 })
    const [loading, setLoading] = useState(false)
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
    const [folder, setFolder] = useState<string[]>([])
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
                ...stateRef.current.filters,
                folder: stateRef.current.folder?.join("/")
            }
            try {
                const res = await axios.get("/argus/api/script", { params })
                setData(res.data)
            } catch (error: any) {
                message.error(error.message)
            }
        } catch (e: any) {
            message.error(e.message)
        }
        setLoading(false)
    }
    async function batchDelete(selectedRowKeys: React.Key[]) {
        if (submit) return
        submit = true
        const params = { script_name: selectedRowKeys, folder: folder.join('/') }
        try {
            const res = await axios.get("/argus/api/script/deleteCheck", { params })
            const confirm = res.data.data
            Modal.confirm({
                title: '是否删除?',
                icon: <ExclamationCircleOutlined />,
                content: confirm && confirm.length > 0 && "这些脚本或文件夹被其他压测场景使用, 仍然删除?" + confirm.join(" "),
                okType: 'danger',
                async onOk() {
                    try {
                        await axios.delete("/argus/api/script", { params })
                        message.success("删除成功")
                        load()
                    } catch (error: any) {
                        message.error(error.message)
                        throw error
                    }
                }
            })
        } catch (error: any) {
            message.error(error.message)
        }
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
                <AddFile load={load} folder={folder} />
                <AddFolder load={load} folder={folder} />
                <UploadFile load={load} folder={folder} />
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
                    <Form.Item name="keywords">
                        <Input className="Input" placeholder="Keywords" />
                    </Form.Item>
                    <Button htmlType="submit" icon={<SearchOutlined />}>查找</Button>
                </Form>
            </div>
            <Table size="middle" dataSource={data.data} loading={loading} rowKey="script_name"
                rowSelection={{
                    selectedRowKeys, onChange(selectedRowKeys) {
                        setSelectedRowKeys(selectedRowKeys)
                    }
                }}
                onChange={function (pagination, filters, sorter) {
                    stateRef.current = { ...stateRef.current, pagination, filters, sorter, folder }
                    load()
                }}
                pagination={{ total: data.total, size: "small", showTotal() { return `共 ${data.total} 条` }, showSizeChanger: true }}
                columns={[
                    {
                        title: folder.length > 0 && <span className="icon mb icon-undo" style={{ fontSize: 16 }} onClick={function () {
                            folder.pop()
                            stateRef.current = { ...stateRef.current, folder }
                            setFolder(folder)
                            load()
                        }}></span>,
                        dataIndex: "type",
                        render: function (type) {
                            return type === "file"
                                ? <span className="icon fa fa-file-text" style={{ fontSize: 16, color: "#27BD98" }}></span>
                                : <span className="icon fa fa-folder-open" style={{ fontSize: 16, color: "#F4B454" }}></span>
                        },
                        width: 120,
                        align: "center",
                    },
                    {
                        title: "脚本(目录)名称",
                        dataIndex: "script_name",
                        sorter: true,
                        width: 240,
                        render: function (_, record) {
                            const array = [...folder, record.script_name]
                            return record.type === "file" ? <Button size="small" type="link">
                                <Link to={`${path}/Detail?path=${array.join("/")}`}>{record.script_name}</Link>
                            </Button> : <Button size="small" type="link" onClick={function () {
                                folder.push(record.script_name)
                                stateRef.current = { ...stateRef.current, folder }
                                setFolder(folder)
                                load()
                            }}>{record.script_name}</Button>
                        },
                        filters: function () {
                            const set = new Set<string>()
                            data.data.forEach(function (item) {
                                set.add(item.script_name)
                            })
                            return Array.from(set).map(function (item) {
                                return { text: item, value: item }
                            })
                        }(),
                        ellipsis: true
                    },
                    {
                        title: "提交信息",
                        dataIndex: "commit",
                        width: 300,
                        filters: function () {
                            const set = new Set<string>()
                            data.data.forEach(function (item) {
                                set.add(item.commit)
                            })
                            return Array.from(set).map(function (item) {
                                return { text: item, value: item }
                            })
                        }(),
                        ellipsis: true
                    },
                    {
                        title: "最后修改时间",
                        dataIndex: "update_time",
                        sorter: true,
                        defaultSortOrder: "descend",
                        ellipsis: true
                    },
                    {
                        title: "版次",
                        dataIndex: "version",
                        sorter: true,
                        ellipsis: true
                    },
                    {
                        title: "大小(KB)",
                        dataIndex: "size",
                        sorter: true,
                        ellipsis: true
                    },
                    { ellipsis: true, title: "分组", dataIndex: "group_name" },
                    {
                        title: "下载",
                        render: function (_, record) {
                            const array = [...folder, record.script_name]
                            return record.type === "file"
                                && <a href={"/argus/api/script/download?path=" + array.join("/")}>
                                    <span className="icon md" style={{ fontSize: 18, color: "#0185FF" }}>system_update_alt</span>
                                </a>
                        },
                        align: "center"
                    },
                ]} />
        </Card>
    </div>
}


function AddFile(props: { load: () => {}, folder: string[] }) {
    let submit = false
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();
    return <>
        <Button type="primary" icon={<FileTextOutlined />} onClick={function () {
            setIsModalVisible(true)
        }}>新建脚本</Button>
        <Modal className="AddFile" title="新建脚本" width={950} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            <Form form={form} labelCol={{ span: 2 }} initialValues={{ language: "Jython", method: "GET" }} requiredMark={false} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    const data = { ...values, folder: props.folder.join("/") }
                    await axios.post("/argus/api/script", data)
                    setIsModalVisible(false)
                    message.success("创建成功")
                    form.resetFields()
                    props.load()
                } catch (e: any) {
                    message.error(e.message)
                }
                submit = false
            }}>
                <div className="Line">
                    <div className="Label">脚本名</div>
                    <Form.Item name="language" style={{ width: 200 }}>
                        <Select>
                            <Select.Option value="Jython">Jython</Select.Option>
                            <Select.Option value="Groovy">Groovy</Select.Option>
                            <Select.Option value="Groovy Maven Project">Groovy Maven Project</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item className="WithoutLabel" style={{ width: 525 }} name="script_name" label="脚本名" rules={[
                        { min: 3, max: 20, required: true, whitespace: true },
                        { pattern: /^[A-Za-z][\w.-]+$/, message: "格式不正确" }
                    ]}>
                        <Input placeholder='必须以字母开头, 可以包括字母, 数字和 ._- 最短3位, 最长20位' />
                    </Form.Item>
                </div>
                <div className="Line">
                    <div className="Label">被测的URL</div>
                    <Form.Item name="method" style={{ width: 200 }}>
                        <Select>
                            <Select.Option value="GET">GET</Select.Option>
                            <Select.Option value="POST">POST</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item className="WithoutLabel" name="for_url" label="URL" rules={[
                        { max: 2048 },
                        {
                            async validator(_, value) {
                                if (!value) return
                                let url: URL
                                try {
                                    url = new URL(value)
                                } catch (error) {
                                    throw new Error("URL格式不合法")
                                }
                                const cookies = form.getFieldValue("cookies") || [{}]
                                cookies.forEach(function (item: { value_a: string }) {
                                    item.value_a = url.hostname
                                })
                                form.setFields([{
                                    name: "cookies",
                                    value: cookies,
                                }])
                            }
                        }
                    ]}>
                        <Input style={{ width: 525 }} />
                    </Form.Item>
                </div>
                <div className="Line">
                    <div className="Label"></div>
                    <Form.Item valuePropName="checked" name="has_resource">
                        <Checkbox>创建资源和库目录</Checkbox>
                    </Form.Item>
                    <span className="Info"><InfoCircleOutlined />您可以上传".class", ".py", ".jar" 类型的文件到lib目录, 或者其他任何资源到resources目录</span>
                </div>
                <div className="Line">
                    <div className="Label">分组</div>
                    <Form.Item name="group_name" style={{ width: 200 }}>
                        <ServiceSelect allowClear url="/argus-user/api/group/search" />
                    </Form.Item>
                </div>
                <Collapse expandIconPosition="right" expandIcon={function ({ isActive }) {
                    return <span className={`icon fa fa-angle-double-${isActive ? "down" : "right"}`}></span>
                }}>
                    <Collapse.Panel header="显示高级配置" key="0">
                        <div className="Line SubLine">
                            <div className="Label">Headers</div>
                            <div>
                                <Form.List initialValue={[{}]} name="headers">{function (fields, { add, remove }) {
                                    return fields.map(function (item) {
                                        return <div key={item.key} className="FormList">
                                            <Form.Item name={[item.name, "key"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                                            <span className="Equal">=</span>
                                            <Form.Item name={[item.name, "value"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                                            {item.key !== 0 && <MinusCircleOutlined onClick={function () {
                                                remove(item.name)
                                            }} />}
                                            <PlusCircleOutlined onClick={function () {
                                                add()
                                            }} />
                                        </div>
                                    })
                                }}</Form.List>
                            </div>
                        </div>
                        <div className="Line SubLine">
                            <div className="Label">Cookies</div>
                            <div>
                                <Form.List initialValue={[{}]} name="cookies">{function (fields, { add, remove }) {
                                    return fields.map(function (item) {
                                        return <div key={item.key} className="FormList">
                                            <Form.Item name={[item.name, "key"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                                            <span className="Equal">=</span>
                                            <Form.Item name={[item.name, "value"]} rules={[{ max: 32 }]} style={{ width: 100 }}><Input /></Form.Item>
                                            <Form.Item name={[item.name, "value_a"]} rules={[{ max: 32 }]} style={{ width: 100 }}><Input placeholder="host" /></Form.Item>
                                            <Form.Item name={[item.name, "value_b"]} rules={[{ max: 32 }]} style={{ width: 100 }}><Input placeholder="path" /></Form.Item>
                                            {item.key !== 0 && <MinusCircleOutlined onClick={function () {
                                                remove(item.name)
                                            }} />}
                                            <PlusCircleOutlined onClick={function () {
                                                add({ value_a: new URL(form.getFieldValue("for_url")).hostname })
                                            }} />
                                        </div>
                                    })
                                }}</Form.List>
                            </div>
                        </div>
                        <div className="Line SubLine">
                            <div className="Label">Params</div>
                            <div>
                                <Form.List initialValue={[{}]} name="params">{function (fields, { add, remove }) {
                                    return fields.map(function (item) {
                                        return <div key={item.key} className="FormList">
                                            <Form.Item name={[item.name, "key"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                                            <span className="Equal">=</span>
                                            <Form.Item name={[item.name, "value"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                                            {item.key !== 0 && <MinusCircleOutlined onClick={function () {
                                                remove(item.name)
                                            }} />}
                                            <PlusCircleOutlined onClick={function () {
                                                add()
                                            }} />
                                        </div>
                                    })
                                }}</Form.List>
                            </div>
                        </div>
                    </Collapse.Panel>
                </Collapse>
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

function AddFolder(props: { load: () => {}, folder: string[] }) {
    let submit = false
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();
    return <>
        <Button icon={<FolderOpenFilled />} onClick={function () {
            setIsModalVisible(true)
        }}>新建文件夹</Button>
        <Modal className="AddFolder" title="新建文件夹" width={500} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            <Form form={form} className="Form" labelCol={{ span: 4 }} requiredMark={false} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    const data = { ...values, folder: props.folder.join("/") }
                    await axios.post("/argus/api/script/folder", data)
                    setIsModalVisible(false)
                    message.success("创建成功")
                    form.resetFields()
                    props.load()
                } catch (e: any) {
                    message.error(e.message)
                }
                submit = false
            }}>
                <Form.Item name="folder_name" className="Input" label="文件夹名" rules={[
                    { max: 64, required: true, whitespace: true },
                ]}>
                    <Input />
                </Form.Item>
                <Form.Item name="group_name" label="分组">
                    <ServiceSelect allowClear url="/argus-user/api/group/search" />
                </Form.Item>
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

function UploadFile(props: { load: () => {}, folder: string[] }) {
    let submit = false
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();
    return <>
        <Button icon={<CloudUploadOutlined />} onClick={function () {
            setIsModalVisible(true)
        }}>上传脚本或资源</Button>
        <Modal className="AddFolder" title="上传脚本或资源" width={500} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            <Form form={form} className="Form" labelCol={{ span: 5 }} requiredMark={false} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    const formData = new FormData()
                    formData.append("folder", props.folder.join("/"))
                    formData.append("commit", values.commit)
                    formData.append('file', values.file[0]);
                    await axios.post('/argus/api/script/upload', formData)
                    setIsModalVisible(false)
                    message.success("创建成功")
                    form.resetFields()
                    props.load()
                } catch (e: any) {
                    message.error(e.message)
                }
                submit = false
            }}>
                <Form.Item name="commit" className="Input" label="提交信息" rules={[
                    { max: 64, required: true, whitespace: true },
                ]}>
                    <Input placeholder="单行输入" />
                </Form.Item>
                <Form.Item name="group_name" label="分组">
                    <ServiceSelect allowClear url="/argus-user/api/group/search" />
                </Form.Item>
                <Form.Item name="file" valuePropName="fileList" label="文件" rules={[{ required: true }]}>
                    <Upload max={1} />
                </Form.Item>
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
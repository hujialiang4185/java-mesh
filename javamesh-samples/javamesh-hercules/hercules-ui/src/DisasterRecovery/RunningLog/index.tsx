import { Button, Form, Input, message, Modal, Table } from "antd";
import React, { useContext, useEffect, useRef, useState } from "react"
import CacheRoute, { CacheSwitch, useDidRecover } from "react-router-cache-route";
import { Link, Route, useRouteMatch } from "react-router-dom";
import Breadcrumb from "../../component/Breadcrumb";
import Card from "../../component/Card";
import Detail from "./Detail"
import { SearchOutlined, CloseOutlined, ExclamationCircleOutlined } from '@ant-design/icons'
import "./index.scss"
import axios from "axios";
import Context from "../../ContextProvider";

export default function App() {
    const { path } = useRouteMatch();
    return <CacheSwitch>
        <CacheRoute exact path={path} component={Home} />
        <Route path={path + '/Detail'}><Detail /></Route>
    </CacheSwitch>
}

type Data = {
    history_id: string,
    plan_name: string,
    creator: string
}

function Home() {
    let submit = false
    let { path } = useRouteMatch();
    const [data, setData] = useState<{ data: Data[], total: number }>({ data: [], total: 0 })
    const [loading, setLoading] = useState(false)
    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([])
    const stateRef = useRef<any>({})
    const { auth } = useContext(Context)
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
                    await axios.delete("/argus-emergency/api/history", { params: { history_id: selectedRowKeys } })
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
    async function load() {
        setLoading(true)
        const params = {
            pageSize: stateRef.current.pagination?.pageSize || 10,
            current: stateRef.current.pagination?.current,
            sorter: stateRef.current.sorter?.field,
            order: stateRef.current.sorter?.order,
            ...stateRef.current.search,
            ...stateRef.current.filters
        }
        try {
            const res = await axios.get('/argus-emergency/api/history', { params })
            setData(res.data)
            setSelectedRowKeys([])
        } catch (error: any) {
            message.error(error.message)
        }
        setLoading(false)
    }
    useEffect(function () {
        load()
    }, [])
    useDidRecover(load)
    return <div className="RunningLog">
        <Breadcrumb label="执行记录" />
        <Card>
            <div className="ToolBar">
                <Button disabled={!auth.includes("operator")} icon={<CloseOutlined />} onClick={function () {
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
            <Table size="middle" loading={loading} dataSource={data.data} rowKey="history_id"
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
                        title: "项目名称",
                        dataIndex: "plan_name",
                        sorter: true,
                        filters: function () {
                            const set = new Set<string>()
                            data.data.forEach(function (item) {
                                set.add(item.plan_name)
                            })
                            return Array.from(set).map(function (item) {
                                return { text: item, value: item }
                            })
                        }(),
                        render(value, record) {
                            return <Link to={path + "/Detail?history_id=" + record.history_id}>{value}</Link>
                        },
                        ellipsis: true
                    },
                    {
                        title: "执行用户",
                        dataIndex: "creator",
                        filters: function () {
                            const set = new Set<string>()
                            data.data.forEach(function (item) {
                                set.add(item.creator)
                            })
                            return Array.from(set).map(function (item) {
                                return { text: item, value: item }
                            })
                        }(),
                        ellipsis: true
                    },
                    {
                        title: "执行状态",
                        width: 200,
                        dataIndex: "status",
                        ellipsis: true
                    },
                    {
                        title: "执行时间",
                        width: 200,
                        dataIndex: "execute_time",
                        ellipsis: true
                    }
                ]}
            />
        </Card>
    </div>
}
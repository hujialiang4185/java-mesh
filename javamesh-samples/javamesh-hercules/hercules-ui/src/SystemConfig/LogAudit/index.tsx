import { Button, Form, Input, message, Table } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Breadcrumb from "../../component/Breadcrumb";
import Card from "../../component/Card";
import { SearchOutlined } from '@ant-design/icons'
import "./index.scss"
import axios from "axios";

type Data = {}
export default function App() {
    const [data, setData] = useState<{ data: Data[], total: number }>({ data: [], total: 0 })
    const [loading, setLoading] = useState(false)
    const stateRef = useRef<any>({})
    useEffect(function () {
        load()
    }, [])
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
            const res = await axios.get("/argus-user/api/logAudit", { params })
            setData(res.data)
        } catch (error: any) {
            message.error(error.message)
        }
        setLoading(false)
    }
    return <div className="LogAudit">
        <Breadcrumb label="系统配置" sub={{ label: "日志审计", parentUrl: "/SystemConfig" }} />
        <Card>
            <div className="ToolBar">
                <Form layout="inline">
                    <Form.Item name="keywords">
                        <Input placeholder="Keywords" />
                    </Form.Item>
                    <Button htmlType="submit" icon={<SearchOutlined />}>查找</Button>
                </Form>
            </div>
            <Table size="middle" loading={loading} dataSource={data.data} rowKey="log_id"
                onChange={function (pagination, filters, sorter) {
                    stateRef.current = { ...stateRef.current, pagination, filters, sorter }
                    load()
                }}
                pagination={{ total: data.total, size: "small", showTotal() { return `共 ${data.total} 条` }, showSizeChanger: true }}
                columns={[
                    { title: "资源类型", dataIndex: "resource_type", ellipsis: true },
                    { title: "操作类型", dataIndex: "operation_type", ellipsis: true },
                    { title: "操作级别", dataIndex: "level_label", ellipsis: true },
                    { title: "操作结果", dataIndex: "operation_results", ellipsis: true },
                    { title: "操作人", dataIndex: "operation_people", ellipsis: true },
                    { title: "IP地址", dataIndex: "ip_address", ellipsis: true },
                    { title: "操作详情", dataIndex: "operation_details", ellipsis: true },
                    { title: "操作时间", dataIndex: "operation_date", ellipsis: true },
                ]} 
            />
        </Card>
    </div>
}
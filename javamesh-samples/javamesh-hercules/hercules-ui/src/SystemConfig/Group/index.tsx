import { Button, Form, Input, message, Modal, Popconfirm, Table } from "antd";
import React, { useEffect, useRef, useState } from "react";
import Card from "../../component/Card";
import { PlusOutlined, SearchOutlined } from '@ant-design/icons'
import "./index.scss"
import Breadcrumb from "../../component/Breadcrumb";
import axios from "axios";

type Data = {}
export default function App() {
    let submit = false
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
            const res = await axios.get("/argus-user/api/group", { params })
            setData(res.data)
        } catch (error: any) {
            message.error(error.message)
        }
        setLoading(false)
    }
    return <div className="SystemGroup">
        <Breadcrumb label="系统配置" sub={{ label: "群组管理", parentUrl: "/SystemConfig" }} />
        <Card>
            <div className="ToolBar">
                <AddGroup load={load} />
                <div className="Space"></div>
                <Form layout="inline">
                    <Form.Item name="keywords">
                        <Input placeholder="Keywords" />
                    </Form.Item>
                    <Button htmlType="submit" icon={<SearchOutlined />}>查找</Button>
                </Form>
            </div>
            <Table loading={loading} dataSource={data.data} rowKey="group_id" columns={[
                { title: "群组名称", dataIndex: "group_name" },
                { title: "创建人", dataIndex: "created_by" },
                { title: "创建时间", dataIndex: "created_time" },
                { title: "操作", dataIndex: "group_id",width: 200, render(group_id) {
                    return <Popconfirm title="是否删除?" onConfirm={async function () {
                        if (submit) return
                        submit = true
                        try {
                            await axios.delete("/argus-user/api/group", {params: {group_id: [group_id]}})
                            message.success("删除成功!")
                            load()
                        } catch (error: any) {
                            message.error(error.message)
                        }
                        submit = false
                    }}>
                        <Button type="link" size="small">删除</Button>
                    </Popconfirm>
                } }
            ]} />
        </Card>
    </div>
}

function AddGroup(props: { load: () => void }) {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [form] = Form.useForm();
    return <>
        <Button type="primary" icon={<PlusOutlined />} onClick={function () { setIsModalVisible(true) }}>添加群组</Button>
        <Modal className="AddGroup" title="添加群组" width={400} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
            setIsModalVisible(false)
        }}>
            <Form form={form} requiredMark={false} onFinish={async function (values) {
                try {
                    await axios.post("/argus-user/api/group", values)
                    form.resetFields()
                    setIsModalVisible(false)
                    props.load()
                } catch (error: any) {
                    message.error(error.message)
                }
            }}>
                <Form.Item name="group_name" label="群组名称" rules={[{
                    required: true,
                    pattern: /^\w{6,15}$/,
                    message: "不得少于6个字且不得超过15个字, 只能输入字母、数字、下划线"
                }]}>
                    <Input />
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
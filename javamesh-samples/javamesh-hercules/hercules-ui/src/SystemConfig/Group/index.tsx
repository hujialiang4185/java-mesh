import { Button, Form, Input, Table } from "antd";
import React from "react";
import Card from "../../component/Card";
import { PlusOutlined, SearchOutlined } from '@ant-design/icons'
import "./index.scss"
import Breadcrumb from "../../component/Breadcrumb";

export default function App() {

    return <div className="SystemGroup">
        <Breadcrumb label="系统配置" sub={{label: "群组管理", parentUrl: "/SystemConfig"}}/>
        <Card>
            <div className="ToolBar">
                <Button type="primary" icon={<PlusOutlined />}>添加群组</Button>
                <div className="Space"></div>
                <Form layout="inline">
                    <Form.Item name="keywords">
                        <Input placeholder="Keywords" />
                    </Form.Item>
                    <Button htmlType="submit" icon={<SearchOutlined />}>查找</Button>
                </Form>
            </div>
            <Table columns={[
                {title: "群组名称", dataIndex: "group"},
                {title: "操作", width: 200}
            ]}/>
        </Card>
    </div>
}
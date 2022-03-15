import { Button, Collapse, Form, Input, message, Select } from "antd"
import React from "react"
import { PlusCircleOutlined, MinusCircleOutlined } from '@ant-design/icons'
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import { useHistory, useLocation } from "react-router-dom"
import "./index.scss"
import axios from "axios"

function filterEmpty(arr: {}[]){
    return arr.filter(function(item: {}){return Object.keys(item).length > 0})
}
export default function App() {
    let submit = false
    const history = useHistory()
    const state = useLocation().state as any
    const [form] = Form.useForm()
    return <div className="IDECreate">
        <Breadcrumb label="脚本管理" sub={{ label: "详情", parentUrl: "/PerformanceTest/ScriptManage" }} />
        <Card>
            <Form form={form} labelCol={{ span: 2 }} initialValues={{ language: "Jython", method: "GET", ...state }} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    values.headers = filterEmpty(values.headers)
                    values.cookies = filterEmpty(values.cookies)
                    values.params = filterEmpty(values.params)
                    await axios.post("/argus-emergency/api/script/ide", { ...values, ...state })
                    history.goBack()
                } catch (error: any) {
                    message.error(error.message)
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
                    <Form.Item className="WithoutLabel" style={{ width: 525 }} name="script_name" label="脚本名">
                        <Input disabled />
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
                <Collapse expandIconPosition="right" defaultActiveKey="0" expandIcon={function ({ isActive }) {
                    return <span className={`icon fa fa-angle-double-${isActive ? "down" : "right"}`}></span>
                }}>
                    <Collapse.Panel header="高级配置" key="0">
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
                    <Button onClick={history.goBack}>取消</Button>
                </Form.Item>
            </Form>
        </Card>
    </div>
}
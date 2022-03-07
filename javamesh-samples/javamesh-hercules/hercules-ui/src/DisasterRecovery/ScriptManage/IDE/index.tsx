import { Button, Form, Input, message } from "antd"
import React, { useEffect } from "react"
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import { useHistory, useLocation } from "react-router-dom"
import axios from "axios"
import "./index.scss"
import Editor from "@monaco-editor/react";

export default function App() {
    let submit = false
    const history = useHistory()
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const script_id = urlSearchParams.get("script_id")
    const [form] = Form.useForm()
    useEffect(function () {
        (async function () {
            try {
                const res = await axios.get('/argus-emergency/api/script/ide/get', { params: { script_id } })
                form.setFieldsValue(res.data.data)
            } catch (error: any) {
                message.error(error.message)
            }
        })()
    }, [form, script_id])
    return <div className="ScriptUpdate">
        <Breadcrumb label="脚本管理" sub={{ label: "详情", parentUrl: "/PerformanceTest/ScriptManage" }} />
        <Card>
            <Form form={form}  labelCol={{ span: 3 }} onFinish={async function (values) {
                if (submit) return
                submit = true
                try {
                    await axios.put('/argus-emergency/api/script/ide', { ...values, script_id })
                    message.success("更新成功")
                    history.goBack()
                } catch (e: any) {
                    message.error(e.message)
                }
                submit = false
            }}>
                <Form.Item labelCol={{ span: 1 }} name="script_name" label="脚本名">
                    <Input disabled />
                </Form.Item>
                <Form.Item label="脚本内容" className="Editor WithoutLabel" name="content" rules={[{ required: true, max: 5000 }]}>
                    <Editor className="MonacoEditor" language="python" height={200} />
                </Form.Item>
                <Form.Item className="ScriptParam" labelCol={{ span: 1 }} name="param" label="脚本参数" rules={[{
                    pattern: /^[\w,.|]+$/,
                    message: "格式错误"
                }]}>
                    <Input.TextArea className="Param" showCount maxLength={50} autoSize={{ minRows: 2, maxRows: 2 }}
                        placeholder="测试参数可以在脚本中通过System.getProperty('param')取得, 参数只能为数字、字母、下划线、逗号、圆点(.)或竖线(|)组成, 禁止输入空格, 长度在0-50之间。" />
                </Form.Item>
                <Form.Item className="Buttons">
                    <Button className="Save" htmlType="submit" type="primary">提交</Button>
                    <Button onClick={function () {
                        history.goBack()
                    }}>取消</Button>
                </Form.Item>
            </Form>
        </Card>
    </div>
}
import { Button, Form, FormInstance, Input, message, Radio } from "antd"
import React, { useState } from "react"
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import { useHistory, useLocation } from "react-router-dom"
import axios from "axios"
import "./index.scss"
import ServiceSelect from "../../../component/ServiceSelect"
import Upload from "../../../component/Upload"
import DebugScript from "../DebugScript"
import Editor from "@monaco-editor/react";

export default function App() {
    let submit = false
    const history = useHistory()
    const state = useLocation().state as any
    const [form] = Form.useForm()
    return <div className="ScriptCreate">
        <Breadcrumb label="脚本管理" sub={{ label: "创建", parentUrl: "/PerformanceTest/ScriptManage" }} />
        <Card>
            <Form form={form}  labelCol={{ span: 3 }}
                initialValues={{ script_from: "手工录入", public: "私有", ...state }}
                onFinish={async function (forms) {
                    const values = {...forms, ...state}
                    if (submit) return
                    submit = true
                    try {
                        if (values.script_from === "本地导入") {
                            const formData = new FormData()
                            formData.append('file', values.file[0]);
                            delete values.file
                            for (let key in values) {
                                formData.append(key, values[key]);
                            }
                            await axios.post('/argus-emergency/api/script/upload', formData)
                        } else {
                            await axios.post('/argus-emergency/api/script', values)
                        }
                        message.success("创建成功")
                        history.goBack()
                    } catch (e: any) {
                        message.error(e.message)
                    }
                    submit = false
                }}>
                <div className="Line">
                    <Form.Item className="Middle" labelCol={{ span: 1 }} name="script_name" label="脚本名">
                        <Input disabled />
                    </Form.Item>
                    <Form.Item className="Middle" name="language" label="脚本分类">
                        <Input disabled />
                    </Form.Item>
                    <Form.Item className="Middle" name="public" label="归属">
                        <Radio.Group options={["私有", "共有"]}/>
                    </Form.Item>
                </div>
                <Script form={form} />
                <Form.Item className="ScriptParam" labelCol={{ span: 1 }} name="param" label="脚本参数" rules={[{
                    pattern: /^[\w,.|]+$/,
                    message: "格式错误"
                }]}>
                    <Input.TextArea showCount maxLength={50} autoSize={{ minRows: 2, maxRows: 2 }}
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
function formatLanguage(language: string) {
    switch (language) {
        case "Shell":
            return "shell"
        case "Groovy":
            return "java"
        default:
            return "python"
    }
}
function Script({ form }: { form: FormInstance }) {
    const [scriptFrom, setScriptFrom] = useState("input")
    const state = useLocation().state as any
    
    return <>
        <div className="Line">
            <Form.Item className="Middle" name="script_from" label="脚本来源">
                <Radio.Group onChange={function (e) {
                    setScriptFrom(e.target.value)
                }} options={["手工录入", "脚本克隆", "本地导入"]} />
            </Form.Item>
            {scriptFrom === "脚本克隆" && <Form.Item className="Middle" name="content_from" label="克隆来源">
                <ServiceSelect url='/argus-emergency/api/script/search' onChange={async function (name) {
                    try {
                        const res = await axios.get("/argus-emergency/api/script/getByName", { params: { name } })
                        form.setFields([{name: "content", value: res.data.data.content}])
                    } catch (error: any) {
                        message.error(error.message)
                    }
                }} />
            </Form.Item>}
            {scriptFrom === "本地导入" && <Form.Item className="Middle" name="file" label="文件" rules={[{ required: true }]}>
                <Upload max={1} />
            </Form.Item>}
        </div>
        {scriptFrom !== "本地导入" && <>
            <Form.Item label="脚本内容" className="Editor WithoutLabel" name="content" rules={[{ required: true }]}>
                <Editor className="MonacoEditor" height={200} language={formatLanguage(state.language)} />
            </Form.Item>
            <DebugScript form={form} language={state.language}/>
        </>}
    </>
}
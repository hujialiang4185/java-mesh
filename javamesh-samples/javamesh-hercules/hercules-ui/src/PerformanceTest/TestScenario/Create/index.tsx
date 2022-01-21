import Editor from "@monaco-editor/react"
import { Button, Form, Input, message } from "antd"
import axios from "axios"
import React, { useState } from "react"
import { useHistory } from "react-router-dom"
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import ServiceSelect from "../../../component/ServiceSelect"
import ScenarioFormItems from "../ScenarioFormItems"
import "./index.scss"

export default function App() {
    let submit = false
    const [script, setScript] = useState<{ script: string, script_resource: string }>()
    const history = useHistory()
    return <div className="ScenarioEditor">
        <Breadcrumb label="编辑场景" sub={{ label: "压测场景", parentUrl: "/PerformanceTest/TestScenario" }} />
        <Card>
            <Form labelCol={{ span: 2 }}
                onFinish={async function (values) {
                    if (submit) return
                    submit = true
                    try {
                        await axios.post('/argus/api/scenario', values)
                        message.success("压测场景创建成功")
                        history.goBack()
                    } catch (e: any) {
                        message.error(e.message)
                    }
                    submit = false
                }}
            >
                <ScenarioFormItems />
                <Form.Item label="选择脚本" name="script_path" rules={[{ required: true }]}>
                    <ServiceSelect url={"/argus/api/script/search"} onChange={async function (value) {
                        try {
                            const res = await axios.get("/argus/api/script/get", { params: { path: value } })
                            setScript(res.data.data)
                        } catch (error: any) {
                            message.error(error.message)
                        }
                    }} />
                </Form.Item>
                <Form.Item label="脚本相关资源">
                    <Input.TextArea value={script?.script_resource} readOnly autoSize={{ minRows: 2, maxRows: 2 }} />
                </Form.Item>
                <Form.Item className="Editor">
                    <Editor options={{ readOnly: true }} value={script?.script} language="python" height={300} />
                </Form.Item>
                <Form.Item className="Buttons">
                    <Button className="Save" htmlType="submit" type="primary">提交</Button>
                    <Button htmlType="reset">重置</Button>
                </Form.Item>
            </Form>
        </Card>
    </div>
}
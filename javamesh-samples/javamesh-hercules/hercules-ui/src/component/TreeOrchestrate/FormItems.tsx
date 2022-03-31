import { Col, Divider, Form, Input, InputNumber, Radio, Row, Select } from "antd"
import Checkbox from "antd/lib/checkbox/Checkbox"
import { PlusCircleOutlined, MinusCircleOutlined } from '@ant-design/icons'
import React, { useEffect, useRef, useState } from "react"
import Editor from "@monaco-editor/react";
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api';
import OSSUpload from "../OSSUpload"


function defaultFieldsValues(type: string) {
    switch (type) {
        case "Root":
            return {
                sampling_interval: 2,
                sampling_ignore: 0,
            }
        case "JSR223PostProcessor":
        case "JSR223PreProcessor":
        case "JSR223Assertion":
            return {
                language: "shell"
            }
        case "CSVDataSetConfig":
            return {
                delimiter: ",",
                recycle: true,
                share_mode: "ALL_THREADS"
            }
        case "HTTPRequest":
            return {
                protocol: "http",
                method: "GET",
            }
        default:
            return {}
    }
}

export { defaultFieldsValues }

export default function App(props: { type: String, onChange: () => void }) {
    switch (props.type) {
        case "TransactionController":
            return <>
                <Form.Item name="presure" label="压力分配(%)" rules={[{ type: "integer", max: 100, min: 0 }]}>
                    <InputNumber />
                </Form.Item>
            </>
        case "HTTPRequest":
            return <>
                <Divider orientation="left">Web服务器</Divider>
                <Row gutter={24}>
                    <Col span="6">
                        <Form.Item label="协议" name="protocol">
                            <Select options={[{ value: "http" }]} />
                        </Form.Item>
                    </Col>
                    <Col span="12">
                        <Form.Item label="服务器名称或IP" name="domain">
                            <Input />
                        </Form.Item>
                    </Col>
                    <Col span="6">
                        <Form.Item label="端口" name="port">
                            <Input />
                        </Form.Item>
                    </Col>
                </Row>
                <Divider orientation="left">HTTP请求</Divider>
                <Row gutter={24}>
                    <Col span="6">
                        <Form.Item name="method">
                            <Select options={[{ value: "GET" }, { value: "POST" }, { value: "PUT" }, { value: "DELETE" }, { value: "TRACE" }, { value: "HEAD" }, { value: "OPTIONS" }]} onChange={props.onChange}/>
                        </Form.Item>
                    </Col>
                    <Col span="12">
                        <Form.Item label="路径" name="path">
                            <Input />
                        </Form.Item>
                    </Col>
                    <Col span="6">
                        <Form.Item label="内容编码" name="content_encoding">
                            <Input />
                        </Form.Item>
                    </Col>
                </Row>
                <Divider orientation="left">请求参数</Divider>
                <HTTPRequest name="parameters" />
                <Divider orientation="left">消息体数据</Divider>
                <Form.Item label="消息体" name="body">
                    <Input.TextArea maxLength={1000} showCount />
                </Form.Item>
            </>
        case "JARImport":
            return <>
                <Divider orientation="left">Import代码块</Divider>
                <Form.Item name="content">
                    <Editor className="MonacoEditor" height={400} language="java" onChange={props.onChange} />
                </Form.Item>
                <Form.Item label="JAR文件" name="filenames">
                    <OSSUpload max={10} mark='&path=lib' onChange={props.onChange} />
                </Form.Item>
            </>
        case "WhileController":
            return <>
                <Divider orientation="left">循环继续条件</Divider>
                <Form.Item name="condition">
                    <Input.TextArea maxLength={1000} showCount />
                </Form.Item>
            </>
        case "LoopController":
            return <Form.Item label="循环次数" name="loop_count" rules={[{ type: "integer" }]}>
                <InputNumber className="InputNumber" min={0} />
            </Form.Item>
        case "ConstantTimer":
            return <Form.Item label="线程延迟(毫秒)" name="delay" rules={[{ type: "integer" }]}>
                <InputNumber className="InputNumber" min={0} />
            </Form.Item>
        case "JSR223PreProcessor":
        case "JSR223PostProcessor":
        case "JSR223Assertion":
            return <ScriptEditor onChange={props.onChange} />
        case "ResponseAssertion":
            return <>
                <Divider orientation="left">测试字段</Divider>
                <Form.Item name="test_field">
                    <Radio.Group options={["响应文本", "响应代码", "响应消息", "响应头", "请求头", "URL样本", "文档(文本)", "请求数据"]} />
                </Form.Item>
                <Divider orientation="left">匹配模式</Divider>
                <Form.Item name="test_type">
                    <Radio.Group options={["包括", "匹配", "相等"]} />
                </Form.Item>
                <Form.Item name="test_strings">
                    <Input.TextArea maxLength={1000} showCount />
                </Form.Item>
            </>
        case "TestFunc":
            return <>
                <Form.Item name="method_name" label="方法名">
                    <Input />
                </Form.Item>
                <Divider orientation="left">方法内容</Divider>
                <Form.Item name="script">
                    <Editor className="MonacoEditor" height={400} language="python" onChange={props.onChange} />
                </Form.Item>
            </>
        case "Counter":
            return <>
                <Form.Item name="start" label="开始值">
                    <InputNumber className="InputNumber" min={0} />
                </Form.Item>
                <Form.Item name="incr" label="递增">
                    <InputNumber className="InputNumber" min={0} />
                </Form.Item>
                <Form.Item name="end" label="最大值">
                    <InputNumber className="InputNumber" min={0} />
                </Form.Item>
                <Form.Item name="format" label="数字格式">
                    <Input />
                </Form.Item>
                <Form.Item name="name" label="引用名称">
                    <Input />
                </Form.Item>
                <Form.Item name="per_user" label="与每用户独立的跟踪计数器" valuePropName="checked">
                    <Checkbox />
                </Form.Item>
                <Form.Item name="reset_on_each_thread_group" label="在每个线程组迭代中重置计数器" valuePropName="checked">
                    <Checkbox />
                </Form.Item>
            </>
        case "CSVDataSetConfig":
            return <>
                <Form.Item label="文件名" name="filenames">
                    <OSSUpload max={1} mark='&path=resource' onChange={props.onChange} />
                </Form.Item>
                <Form.Item label="文件编码" name="file_encoding">
                    <Select options={[{ value: "UTF-8" }, { value: "UTF-16" }, { value: "ISO-8859-15" }, { value: "US-ASCII" }]} />
                </Form.Item>
                <Form.Item label="变量名称(西文逗号间隔)" name="variable_names">
                    <Input />
                </Form.Item>
                <Form.Item label="忽略首行(只在设置了变量名称才生效)" name="ignore_first_line" valuePropName="checked">
                    <Checkbox />
                </Form.Item>
                <Form.Item label="分割符(用'\t'代表制表符)" name="delimiter">
                    <Input />
                </Form.Item>
                <Form.Item label="是否允许带引号" name="quoted_data" valuePropName="checked">
                    <Checkbox />
                </Form.Item>
                <Form.Item label="遇到文件结束符再次循环?" name="recycle" valuePropName="checked">
                    <Checkbox />
                </Form.Item>
                <Form.Item label="遇到文件结束符停止线程?" name="stop_on_eof" valuePropName="checked">
                    <Checkbox />
                </Form.Item>
                <Form.Item label="线程共享模式" name="share_mode">
                    <Select options={[{ value: "ALL_THREADS" }, { value: "CURRENT_AGENT" }, { value: "CURRENT_PROCESS" }, { value: "CURRENT_THREAD" }]} />
                </Form.Item>
            </>
        case "HTTPCookieManager":
            return <>
                <Divider orientation="left">Cookie</Divider>
                <HTTPCookie />
            </>
        case "HTTPHeaderManager":
            return <>
                <Divider orientation="left">Header</Divider>
                <HTTPRequest name="headers" />
            </>
    }
    return null
}

function ScriptEditor(props: { onChange: () => void }) {
    const [language, setLanguage] = useState("")
    const radioRef = useRef<HTMLDivElement>(null)
    const monacoRef = useRef<monaco.editor.IStandaloneCodeEditor>();
    useEffect(function () {
        setLanguage((radioRef.current?.querySelector(".ant-radio-checked > input") as HTMLInputElement).value)
    }, [])
    return <>
        <Divider orientation="left">脚本内容</Divider>
        <Form.Item name="language">
            <Radio.Group ref={radioRef} options={["shell", "javascript", "groovy",]} onChange={function (e) {
                setLanguage(e.target.value)
                monacoRef.current?.setValue("")
            }} />
        </Form.Item>
        <Form.Item name="script">
            <Editor className="MonacoEditor" height={400} language={language} onChange={props.onChange} onMount={function(editor) {
                monacoRef.current = editor
            }}/>
        </Form.Item>
    </>
}


function HTTPRequest(props: { name: string }) {
    return <div className="HTTPRequestHeaders">
        <Form.List initialValue={[{}]} name={props.name}>{function (fields, { add, remove }) {
            return fields.map(function (item) {
                return <div key={item.name} className="FormList">
                    <Form.Item name={[item.name, "name"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                    <span className="Equal">=</span>
                    <Form.Item name={[item.name, "value"]} rules={[{ max: 32 }]}><Input /></Form.Item>
                    <PlusCircleOutlined onClick={function () { add() }} />
                    {item.key !== 0 && <MinusCircleOutlined onClick={function () { remove(item.name) }} />}
                </div>
            })
        }}</Form.List>
    </div>
}

function HTTPCookie() {
    return <div className="HTTPRequestHeaders">
        <Form.List initialValue={[{}]} name="cookies">{function (fields, { add, remove }) {
            return fields.map(function (item) {
                return <div key={item.name} className="FormList">
                    <Form.Item name={[item.name, "name"]} rules={[{ max: 32 }]}><Input placeholder="名称" /></Form.Item>
                    <span className="Equal">=</span>
                    <Form.Item name={[item.name, "value"]} rules={[{ max: 256 }]}><Input placeholder="值" /></Form.Item>
                    <Form.Item name={[item.name, "domain"]} rules={[{ max: 32 }]}><Input placeholder="域" /></Form.Item>
                    <Form.Item name={[item.name, "path"]} rules={[{ max: 32 }]}><Input placeholder="路径" /></Form.Item>
                    <Form.Item label="Secure" name={[item.name, "safe"]} valuePropName="checked"><Checkbox /></Form.Item>
                    <PlusCircleOutlined onClick={function () { add() }} />
                    {item.key !== 0 && <MinusCircleOutlined onClick={function () { remove(item.name) }} />}
                </div>
            })
        }}</Form.List>
    </div>
}
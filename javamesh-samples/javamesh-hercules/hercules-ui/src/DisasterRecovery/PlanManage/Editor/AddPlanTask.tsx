import { Button, Checkbox, Collapse, Divider, Drawer, Form, Input, InputNumber, message, Radio, Switch } from "antd";
import axios from "axios";
import React, { useState } from "react";
import "./AddPlanTask.scss"
import SearchSelect from "./SearchSelect";
import TabelTransfer from "./TabelTransfer";
import Editor from "@monaco-editor/react";
import { FormItemLabelProps } from "antd/lib/form/FormItemLabel";

export default function App(props: { onFinish: (values: any) => Promise<void>, initialValues: any, children: React.ReactNode }) {
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isCmd, setIsCmd] = useState(props.initialValues.task_type === "命令行脚本")
  const [script, setScript] = useState({ submit_info: "", content: "" })
  const [form] = Form.useForm();
  const types = ["自定义脚本压测", "引流压测", "命令行脚本"]

  async function loadScript(name?: string) {
    if (!name) return
    try {
      const res = await axios.get("/argus-emergency/api/script/getByName", { params: { name, status: "approved" } })
      setScript(res.data.data)
    } catch (error: any) {
      message.error(error.message)
    }
  }
  return <>
    <Button type="link" size="small" onClick={function () { 
      setIsModalVisible(true)
      loadScript(props.initialValues.script_name)
    }}>{props.children}</Button>
    <Drawer className="AddPlanTask" title={props.children} width={950} visible={isModalVisible} maskClosable={false} footer={null} onClose={function () {
      setIsModalVisible(false)
    }}>
      <Form form={form} labelCol={{ span: 2 }}
        initialValues={props.initialValues}
        onFinish={async (values) => {
          try {
            await props.onFinish(values)
            form.resetFields()
            setIsModalVisible(false)
          } catch (error: any) {
            message.error(error.message)
          }
        }}>
        <Form.Item label="名称" name="task_name" rules={[{ required: true, max: 64 }]}><Input /></Form.Item>
        <div className="Line">
          <Form.Item labelCol={{ span: 4 }} className="Middle" label="任务类型" name="task_type" rules={[{ required: true }]}>
            <Radio.Group onChange={function (e) {
              setIsCmd(e.target.value === "命令行脚本")
              form.setFields([{ name: "script_name", value: "" }])
              setScript({ submit_info: "", content: "" })
            }} options={types.map(function (item, index) {
              return {
                value: item,
                label: item,
                disabled: index === 1,
              }
            })} />
          </Form.Item>
          <Form.Item labelCol={{ span: 4 }} className="Middle" label="执行方式" name="sync" valuePropName="checked">
            <Switch checkedChildren="同步" unCheckedChildren="异步" defaultChecked />
          </Form.Item>
        </div>
        <Form.Item label="执行主机" name="service_id">
          <TabelTransfer />
        </Form.Item>
        {isCmd ? <CmdFormItems script={script} loadScript={loadScript} /> : <TaskFormItems script={script} loadScript={loadScript} />}
        <Form.Item className="Buttons">
          <Button type="primary" htmlType="submit">提交</Button>
          <Button onClick={function () {
            setIsModalVisible(false)
          }}>取消</Button>
        </Form.Item>
      </Form>
    </Drawer>
  </>
}

type Script = {
  submit_info: string,
  content: string
}
function CmdFormItems(props: { script: Script, loadScript: (name: string) => void }) {
  return <>
    <Collapse expandIconPosition="right" defaultActiveKey="0" expandIcon={function ({ isActive }) {
      return <span className={`icon fa fa-angle-double-${isActive ? "down" : "right"}`}></span>
    }}>
      <Collapse.Panel header="脚本配置" key="0">
        <Form.Item className="ScriptName" label="脚本名称" name="script_name" rules={[{ required: true }]}>
          <SearchSelect onChange={props.loadScript} />
        </Form.Item>
        <Form.Item label="脚本用途">
          <Input.TextArea value={props.script.submit_info} disabled />
        </Form.Item>
        <div className="Editor">
          <Editor height={150} language="shell" options={{ readOnly: true }} value={props.script.content} />
        </div>
      </Collapse.Panel>
    </Collapse>
  </>
}

function TaskFormItems(props: { script: Script, loadScript: (name: string) => void }) {
  return <>
    <Collapse expandIconPosition="right" defaultActiveKey="0" expandIcon={function ({ isActive }) {
      return <span className={`icon fa fa-angle-double-${isActive ? "down" : "right"}`}></span>
    }}>
      <Collapse.Panel header="脚本配置" key="0">
        <Form.Item className="ScriptName" label="脚本名称" name="script_name" rules={[{ required: true }]}>
          <SearchSelect type="gui" onChange={props.loadScript} />
        </Form.Item>
        <Form.Item label="脚本用途">
          <Input.TextArea value={props.script.submit_info} disabled />
        </Form.Item>
        <div className="Editor">
          <Editor height={150} language="shell" options={{ readOnly: true }} value={props.script.content} />
        </div>
      </Collapse.Panel>
    </Collapse>
    <Divider orientation="left">压测配置</Divider>
    <Form.Item name="vuser" label="虚拟用户数" labelCol={{ span: 3 }} labelAlign="left" rules={[{ type: "integer", required: true }]}>
      <InputNumber min={1} />
    </Form.Item>
    <RootBasicScenario labelCol={{ span: 3 }} labelAlign="left" label="基础场景" />
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="采样间隔" name="sampling_interval" rules={[{ type: "integer" }]}>
      <InputNumber className="InputNumber" min={0} addonAfter="MS" />
    </Form.Item>
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="忽略采样数量" name="sampling_ignore" rules={[{ type: "integer" }]}>
      <InputNumber className="InputNumber" min={0} />
    </Form.Item>
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" name="test_param" label="测试参数" rules={[{
      pattern: /^[\w,.|]+$/,
      message: "格式错误"
    }]}>
      <Input.TextArea showCount maxLength={50} autoSize={{ minRows: 2, maxRows: 2 }}
        placeholder="测试参数可以在脚本中通过System.getProperty('param')取得, 参数只能为数字、字母、下划线、逗号、圆点(.)或竖线(|)组成, 禁止输入空格, 长度在0-50之间。" />
    </Form.Item>
    <RootPresure />
  </>
}

export function RootBasicScenario(props: FormItemLabelProps) {
  const [basic, setBasic] = useState(false)
  return <Form.Item {...props} className="RootBasicScenario" initialValue="by_time" name="basic">
    <Radio.Group onChange={function (e) {
      setBasic(e.target.value === "by_count")
    }}>
      <Radio value="by_time">测试时长</Radio>
      <div>
        <Form.Item label="小时" className="WithoutLabel" initialValue="0" name="by_time_h" rules={[{ type: "integer" }]}>
          <InputNumber disabled={basic} className="Time" min={0} max={8759}/>
        </Form.Item>
        <span className="Sep">:</span>
        <Form.Item label="分钟" className="WithoutLabel" initialValue="0" name="by_time_m" rules={[{ type: "integer" }]}>
          <InputNumber disabled={basic} className="Time" min={0} max={60} />
        </Form.Item>
        <span className="Sep">:</span>
        <Form.Item label="秒" className="WithoutLabel" initialValue="0" name="by_time_s" rules={[{ type: "integer" }]}>
          <InputNumber disabled={basic} className="Time" min={0} max={60} />
        </Form.Item>
        <span className="Format">HH:MM:SS</span>
      </div>
      <Radio value="by_count">测试次数</Radio>
      <div>
        <Form.Item label="次数" className="WithoutLabel" name="by_count" rules={[{ type: "integer" }]}>
          <InputNumber disabled={!basic} className="Count" min={0} max={10000} addonAfter="最大值: 10000" />
        </Form.Item>
      </div>
    </Radio.Group>
  </Form.Item>
}

export function RootPresure() {
  const [disabled, setDisabled] = useState(true)
  return <>
    <Divider orientation="left">压力配置</Divider>
    <Form.Item name="is_increased" valuePropName="checked">
      <Checkbox onChange={function (e) { setDisabled(!e.target.checked) }}>压力递增</Checkbox>
    </Form.Item>
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="初始数" name="init_value" rules={[{ type: "integer" }]}>
      <InputNumber disabled={disabled} className="InputNumber" min={0} />
    </Form.Item>
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="增量" name="increment" rules={[{ type: "integer" }]} >
      <InputNumber disabled={disabled} className="InputNumber" min={0} />
    </Form.Item>
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="初始等待时间" name="init_wait" rules={[{ type: "integer" }]}>
      <InputNumber disabled={disabled} className="InputNumber" min={0} addonAfter="MS" />
    </Form.Item>
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="进程增长间隔" name="growth_interval" rules={[{ type: "integer" }]}>
      <InputNumber disabled={disabled} addonAfter="MS" min={0} className="InputNumber" />
    </Form.Item>
  </>
}
import { Button, Divider, Drawer, Form, Input, InputNumber, message, Radio, Select, Switch } from "antd";
import axios from "axios";
import React, { useState } from "react";
import "./AddPlanTask.scss"
import SearchSelect from "./SearchSelect";
import TabelTransfer from "./TabelTransfer";
import Editor from "@monaco-editor/react";
import { RootBasicScenario, RootPresure } from "../../../component/TreeOrchestrate/FormItems";

const types = ["自定义脚本压测", "全链路引流压测", "命令行脚本"]
export default function App(props: { onFinish: (values: any) => void }) {
  const [isModalVisible, setIsModalVisible] = useState(false);
  // const [script, setScript] = useState("")
  const [isCmd, setIsCmd] = useState(false)
  const [form] = Form.useForm();
  return <>
    <Button type="link" size="small" onClick={function () { setIsModalVisible(true) }}>加任务</Button>
    <Drawer className="AddPlanTask" title="加任务" width={950} visible={isModalVisible} maskClosable={false} footer={null} onClose={function () {
      setIsModalVisible(false)
    }}>
      <Form form={form} requiredMark={false} labelCol={{ span: 2 }}
        initialValues={{ channel_type: "SSH", task_type: types[isCmd ? 2 : 0] }}
        onFinish={async (values) => {
          try {
            values.sync === false ? values.sync = "异步" : values.sync = "同步"
            // 获取key
            const res = await axios.post("/argus-emergency/api/plan/task", values)
            props.onFinish({ ...values, ...res.data.data, title: values.task_name })
            form.resetFields()
            // setScript("")
            setIsModalVisible(false)
          } catch (error: any) {
            message.error(error.message)
          }
        }}>
        <Form.Item label="名称" name="task_name" rules={[{ required: true, max: 64 }]}><Input /></Form.Item>
        <div className="Line">
          <Form.Item labelCol={{ span: 4 }} className="Middle" label="通道类型" name="channel_type">
            <Select options={[{ value: "SSH" }, { value: "API" }]} />
          </Form.Item>
          <Form.Item labelCol={{ span: 4 }} className="Middle" label="执行方式" name="sync" valuePropName="checked">
            <Switch checkedChildren="同步" unCheckedChildren="异步" defaultChecked />
          </Form.Item>
        </div>
        <Form.Item label="执行主机" name="service_id">
          <TabelTransfer />
        </Form.Item>
        <Form.Item label="任务类型" name="task_type">
          <Radio.Group onChange={function (e) {
            setIsCmd(e.target.value === "命令行脚本")
          }} options={types.map(function (item, index) {
            return {
              value: item,
              label: item,
              disabled: index === 1,
            }
          })} />
        </Form.Item>
        {isCmd ? <CmdFormItems /> : <TaskFormItems />}
        <Form.Item className="Buttons">
          <Button type="primary" htmlType="submit">创建</Button>
          <Button onClick={function () {
            setIsModalVisible(false)
          }}>取消</Button>
        </Form.Item>
      </Form>
    </Drawer>
  </>
}


function CmdFormItems() {
  const [script, setScript] = useState({ submit_info: "", content: "" })
  return <>
    <Form.Item className="ScriptName" label="脚本名称" name="script_name">
      <SearchSelect onChange={async function (name) {
        try {
          const res = await axios.get("/argus-emergency/api/script/getByName", { params: { name, status: "approved" } })
          setScript(res.data.data)
        } catch (error: any) {
          message.error(error.message)
        }
      }} />
    </Form.Item>
    <Form.Item label="脚本用途">
      <Input.TextArea value={script.submit_info} disabled />
    </Form.Item>
    <div className="Editor">
      <Editor height={150} language="shell" options={{ readOnly: true }} value={script.content} />
    </div>
  </>
}

function TaskFormItems() {
  const [script, setScript] = useState({ submit_info: "", content: "" })
  return <>
    <Form.Item className="ScriptName" label="脚本名称" name="gui_script_name">
      <SearchSelect type="gui" onChange={async function (name) {
        try {
          const res = await axios.get("/argus-emergency/api/script/getByName", { params: { name, status: "approved" } })
          setScript(res.data.data)
        } catch (error: any) {
          message.error(error.message)
        }
      }} />
    </Form.Item>
    <Form.Item label="脚本用途">
      <Input.TextArea value={script.submit_info} disabled />
    </Form.Item>
    <div className="Editor">
      <Editor height={150} language="shell" options={{ readOnly: true }} value={script.content} />
    </div>
    <Divider orientation="left">压测配置</Divider>
    <Form.Item name="agent" label="代理数" labelCol={{ span: 3 }} labelAlign="left" rules={[{ type: "integer" }]}>
      <InputNumber min={1} />
    </Form.Item>
    <Form.Item name="vuser" label="虚拟用户数" labelCol={{ span: 3 }} labelAlign="left" rules={[{ type: "integer" }]}>
      <InputNumber min={1} />
    </Form.Item>
    <RootBasicScenario labelCol={{ span: 3 }} labelAlign="left" label="基础场景" />
    <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="采样间隔" name="sampling_interval" rules={[{ type: "integer" }]}>
      <InputNumber className="InputNumber" min={0} />
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
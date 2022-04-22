import { Button, Checkbox, Collapse, Divider, Drawer, Form, Input, InputNumber, message, Radio, Switch } from "antd";
import axios from "axios";
import React, { useRef, useState } from "react";
import "./AddPlanTask.scss"
import SearchSelect from "./SearchSelect";
import TabelTransfer from "./TabelTransfer";
import Editor from "@monaco-editor/react";
import { debounce } from "lodash";

export default function App(props: { onFinish: (values: any) => Promise<void>, initialValues: any, create?: boolean }) {
  props.initialValues.gui_script_name = props.initialValues.script_name
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [isCmd, setIsCmd] = useState(props.initialValues.task_type === "命令行脚本")
  const [script, setScript] = useState({ submit_info: "", content: "" })
  const [basic, setBasic] = useState(props.initialValues.basic === "by_count")
  const [presure, setPresure] = useState(!props.initialValues.is_increased)
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
    }}>{props.create ? "加任务" : "修改"}</Button>
    <Drawer className="AddPlanTask" title={props.create ? "加任务" : "修改"} width={1200} visible={isModalVisible} footer={null} onClose={function () {
      setIsModalVisible(false)
    }}>
      <Form form={form} labelCol={{ span: 2 }}
        initialValues={props.initialValues}
        onFinish={async (values) => {
          values.submit_info = script.submit_info
          if (values.gui_script_name) {
            values.script_name = values.gui_script_name
          }
          try {
            await props.onFinish(values)
            if (props.create) {
              form.resetFields()
              setIsCmd(false)
              setScript({ submit_info: "", content: "" })
              setBasic(false)
              setPresure(true)
            }
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
              console.log("set xxx")
              form.setFields([{ name: "script_name", value: "" }, { name: "gui_script_name", value: "" }])
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
        <Form.Item label="执行主机" name="server_list" rules={[{ required: true }]}>
          <TabelTransfer />
        </Form.Item>
        {isCmd ? <>
          <Form.Item className="ScriptName" label="脚本名称" name="script_name" rules={[{ required: true }]}>
            <SearchSelect type="normal" onChange={loadScript} />
          </Form.Item>
          <Collapse expandIconPosition="right" expandIcon={function ({ isActive }) {
            return <span className={`icon fa fa-angle-double-${isActive ? "down" : "right"}`}></span>
          }}>
            <Collapse.Panel header="脚本配置" key="0">
              <Form.Item label="脚本用途">
                <Input.TextArea value={script.submit_info} disabled />
              </Form.Item>
              <div className="Editor">
                <Editor height={150} language="shell" options={{ readOnly: true }} value={script.content} />
              </div>
            </Collapse.Panel>
          </Collapse>
        </> : <>
          <Form.Item className="ScriptName" label="脚本名称" name="gui_script_name" rules={[{ required: true }]}>
            <SearchSelect type="gui" onChange={loadScript} />
          </Form.Item>
          <Collapse expandIconPosition="right" expandIcon={function ({ isActive }) {
            return <span className={`icon fa fa-angle-double-${isActive ? "down" : "right"}`}></span>
          }}>
            <Collapse.Panel header="脚本配置" key="0">
              <Form.Item label="脚本用途">
                <Input.TextArea value={script.submit_info} disabled />
              </Form.Item>
              <div className="Editor">
                <Editor height={150} language="shell" options={{ readOnly: true }} value={script.content} />
              </div>
            </Collapse.Panel>
          </Collapse>
          <Divider orientation="left">压测配置</Divider>
          <Form.Item name="vuser" label="虚拟用户数" labelCol={{ span: 3 }} labelAlign="left" rules={[{ type: "integer", required: true }]}>
            <InputNumber min={1} max={10000} placeholder="请输入该测试所期望的虚拟用户数" addonAfter="最大10000" />
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="基础场景" className="RootBasicScenario" name="basic" rules={[{ required: true }]} >
            <Radio.Group onChange={function (e) {
              setBasic(e.target.value === "by_count")
            }}>
              <Radio value="by_time">测试时长</Radio>
              <div>
                <Form.Item label="小时" className="WithoutLabel" name="by_time_h" rules={[{ type: "integer" }]}>
                  <InputNumber disabled={basic} className="Time" min={0} max={8759} />
                </Form.Item>
                <span className="Sep">:</span>
                <Form.Item label="分钟" className="WithoutLabel" name="by_time_m" rules={[{ type: "integer" }]}>
                  <InputNumber disabled={basic} className="Time" min={0} max={59} />
                </Form.Item>
                <span className="Sep">:</span>
                <Form.Item label="秒" className="WithoutLabel" name="by_time_s" rules={[{ type: "integer" }]}>
                  <InputNumber disabled={basic} className="Time" min={0} max={59} />
                </Form.Item>
                <span className="Format">HH:MM:SS</span>
              </div>
              <Radio value="by_count">测试次数</Radio>
              <div>
                <Form.Item label="次数" className="WithoutLabel" name="by_count" rules={[{ type: "integer" }]}>
                  <InputNumber disabled={!basic} className="Count" min={0} max={10000}
                    addonAfter="最大值: 10000" placeholder="每个线程运行测试数量" />
                </Form.Item>
              </div>
            </Radio.Group>
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="采样间隔" name="sampling_interval" rules={[{ type: "integer" }]}>
            <InputNumber className="InputNumber" min={0} addonAfter="MS" placeholder="默认2ms对TPS进行采样" />
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="忽略采样数量" name="sampling_ignore" rules={[{ type: "integer" }]}>
            <InputNumber className="InputNumber" min={0} placeholder="请输入要忽略的采样个数。实际忽略的采样时间是 忽略的个数 * 采样间隔。" />
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" name="test_param" label="测试参数" rules={[{
            pattern: /^[\w,.|]+$/,
            message: "格式错误"
          }]}>
            <Input.TextArea showCount maxLength={50} autoSize={{ minRows: 2, maxRows: 2 }}
              placeholder="测试参数可以在脚本中通过System.getProperty('param')取得, 参数只能为数字、字母、下划线、逗号、圆点(.)或竖线(|)组成, 禁止输入空格, 长度在0-50之间。" />
          </Form.Item>
          <Divider orientation="left">压力配置</Divider>
          <Form.Item name="is_increased" valuePropName="checked">
            <Checkbox onChange={function (e) { setPresure(!e.target.checked) }}>压力递增</Checkbox>
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="初始数" name="init_value" rules={[{ type: "integer" }]}>
            <InputNumber disabled={presure} className="InputNumber" min={0} />
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="增量" name="increment" rules={[{ type: "integer" }]} >
            <InputNumber disabled={presure} className="InputNumber" min={0} />
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="初始等待时间" name="init_wait" rules={[{ type: "integer" }]}>
            <TimePicker disabled={presure} />
          </Form.Item>
          <Form.Item labelCol={{ span: 3 }} labelAlign="left" label="进程增长间隔" name="growth_interval" rules={[{ type: "integer" }]}>
            <TimePicker disabled={presure} />
          </Form.Item>
        </>}
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

function TimePicker(props: { disabled: boolean, value?: number, onChange?: (value: number) => void }) {
  const [date, setDate] = useState(new Date(props.value && props.value > 0 ? props.value : 0))
  const onChangeRef = useRef(debounce(function(date: Date){
    setDate(date)
    props.onChange?.(+date)
  }, 100))
  return <div className="TimePicker">
    <InputNumber value={date.getUTCHours()} disabled={props.disabled} className="Time" min={0} max={23} onChange={function(value){
      date.setUTCHours(value)
      onChangeRef.current(date)
    }}/>
    <span className="Sep">:</span>
    <InputNumber value={date.getMinutes()} disabled={props.disabled} className="Time" min={0} max={59} onChange={function(value){
      date.setMinutes(value)
      onChangeRef.current(date)
    }}/>
    <span className="Sep">:</span>
    <InputNumber value={date.getSeconds()} disabled={props.disabled} className="Time" min={0} max={59} onChange={function(value){
      date.setSeconds(value)
      onChangeRef.current(date)
    }}/>
    <span className="Format">HH:MM:SS</span>
  </div>
}
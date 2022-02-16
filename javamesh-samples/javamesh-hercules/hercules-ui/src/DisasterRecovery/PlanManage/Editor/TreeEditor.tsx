import React, { Key, useState } from 'react'
import { Button, Table, Tree, Modal, Popconfirm, message, Form, Input } from 'antd';
import "./TreeEditor.scss"
import axios from 'axios';
import AddPlanTask from "./AddPlanTask"

type Data = {
  key: Key,
  title: string,
  children?: Data[]
}
function loop(data: Data[], key: Key, callback: (data: Data, i: number, gData: Data[]) => void) {
  for (let i = 0; i < data.length; i++) {
    if (data[i].key === key) {
      return callback(data[i], i, data);
    }
    const children = data[i].children
    if (children) {
      loop(children, key, callback);
    }
  }
};
export default class App extends React.Component<{ plan_id: string }> {
  state: { gData: Data[], expandedKeys: Key[] } = { gData: [], expandedKeys: [] }
  async componentDidMount() {
    try {
      const res = await axios.get("/argus-emergency/api/plan/task", { params: { plan_id: this.props.plan_id } })
      this.setState({
        gData: res.data.data,
      })
    } catch (error: any) {
      message.error(error.message)
    }
  }
  async save(data: Data[]) {
    this.setState({
      gData: data,
    });
    try {
      await axios.put("/argus-emergency/api/plan", { plan_id: this.props.plan_id, expand: data })
    } catch (error: any) {
      Modal.error({
        title: "保存失败",
        content: error.message,
        onOk: function () {
          window.history.back()
        }
      })
    }
  }
  render() {
    return <div className="TreeEditor">
      <Tree className="Tree"
        expandedKeys={this.state.expandedKeys}
        onExpand={expandedKeys => {
          this.setState({
            expandedKeys
          })
        }}
        draggable
        onDrop={info => {
          const dropKey = info.node.key;
          const dragKey = info.dragNode.key;
          const dropPosition = info.dropPosition;
          const data = [...this.state.gData];
          const dropRoot = data.find(function (item) {
            return dropKey === item.key;
          })
          const dragRoot = data.find(function (item) {
            return dragKey === item.key;
          })
          if (dropRoot !== dragRoot) return
          // Find dragObject
          let dragObj: Data = { key: 0, title: "" };
          loop(data, dragKey, (item, index, arr) => {
            arr.splice(index, 1);
            dragObj = item;
          });

          if (!info.dropToGap) {
            // Drop on the content
            loop(data, dropKey, item => {
              item.children = item.children || [];
              // where to insert 示例添加到头部, 可以是随意位置
              item.children.unshift(dragObj);
            });
          } else if (
            (info.node.children || []).length > 0 && // Has children
            info.node.expanded && // Is expanded
            dropPosition === 1 // On the bottom gap
          ) {
            loop(data, dropKey, item => {
              item.children = item.children || [];
              // where to insert 示例添加到头部, 可以是随意位置
              item.children.unshift(dragObj);
              // in previous version, we use item.children.push(dragObj) to insert the
              // item to the tail of the children
            });
          } else {
            let ar: Data[] = [];
            let i: number = 0;
            loop(data, dropKey, (item, index, arr) => {
              ar = arr;
              i = index;
            });
            if (dropPosition === -1) {
              ar.splice(i, 0, dragObj);
            } else {
              ar.splice(i + 1, 0, dragObj);
            }
          }
          // 保存
          this.save(data)
        }}
        treeData={this.state.gData}
      />
      <Table size="middle" rowKey="key" pagination={false}
        expandable={{
          expandedRowKeys: this.state.expandedKeys,
          onExpandedRowsChange: expandedKeys => {
            this.setState({
              expandedKeys
            })
          }
        }} dataSource={this.state.gData}
        columns={[
          { title: "任务(场景)名称", dataIndex: "title", width: 250, ellipsis: true },
          { title: "通道类型", dataIndex: "channel_type", ellipsis: true },
          { title: "脚本名称", dataIndex: "script_name", ellipsis: true },
          { title: "脚本用途", dataIndex: "submit_info", ellipsis: true },
          { title: "执行方式", dataIndex: "sync", ellipsis: true },
          {
            title: <AddScenaTask onFinish={(value: Data) => {
              const data = [...this.state.gData];
              data.push(value);
              // 保存
              this.save(data)
            }} />,
            width: 120, align: "left", dataIndex: "key", render: (key) => {
              return <>
                <AddPlanTask onFinish={value => {
                  const data = [...this.state.gData];
                  loop(data, key, item => {
                    item.children = item.children || [];
                    item.children.push(value);
                  });
                  // 保存
                  this.save(data)
                }} />
                <Popconfirm title="是否删除?" onConfirm={() => {
                  const data = [...this.state.gData];
                  loop(data, key, (item, index, arr) => {
                    arr.splice(index, 1);
                  });
                  // 保存
                  this.save(data)
                }}>
                  <Button type="link" size="small">删除</Button>
                </Popconfirm>
              </>
            }
          }
        ]} />
    </div>
  }
}

function AddScenaTask(props: { onFinish: (values: any) => void }) {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  return <>
    <Button type="link" size="small" onClick={function () { setIsModalVisible(true) }}>加场景</Button>
    <Modal className="AddScenaTask" title="加场景" width={950} visible={isModalVisible} maskClosable={false} footer={null} onCancel={function () {
      setIsModalVisible(false)
    }}>
      <Form form={form} requiredMark={false} labelCol={{ span: 4 }} initialValues={{ channel_type: "SSH" }} onFinish={async (values) => {
        try {
          values.sync === false ? values.sync = "异步" : values.sync = "同步"
          // 获取key
          const res = await axios.post("/argus-emergency/api/plan/task", values)
          props.onFinish({ ...values, ...res.data.data, title: values.task_name })
          form.resetFields()
          setIsModalVisible(false)
        } catch (error: any) {
          message.error(error.message)
        }
      }}>
        <Form.Item labelCol={{ span: 2 }} label="场景名称" name="task_name" rules={[{ required: true, max: 64 }]}>
          <Input />
        </Form.Item>
        <Form.Item labelCol={{ span: 2 }} label="场景描述" name="scena_desc">
          <Input.TextArea showCount maxLength={50} autoSize={{ minRows: 2, maxRows: 2 }} />
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
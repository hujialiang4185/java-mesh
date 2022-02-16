import { Input, Tree, Form, FormInstance, message } from "antd"
import { DataNode } from "antd/lib/tree"
import React, { Key } from "react"
import DropdownMenu, { menus, rules } from "./DropdownMenu"
import FormItems, { defaultFieldsValues } from "./FormItems"
import Icons from "./Icons"
import "./index.scss"

type Data = {
    key: string,
    children?: Data[]
}

export type Values = {
    tree: Data, map: Map<Key, any>
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

export default class App extends React.Component<{ initialValues: () => Promise<Values>, onSave: (values: Values) => void }, { tree: Data, selected: string }> {
    formRef = React.createRef<FormInstance>();
    map = new Map()
    async componentDidMount() {
        try {
            const values = await this.props.initialValues()
            this.setState({
                tree: values.tree,
                selected: values.tree.key,
            })
            this.map = values.map
            this.formRef.current?.setFieldsValue(this.map.get(this.state.selected))
            this.onSelect(values.tree.key)
        } catch (error: any) {
            message.error(error.message)
        }
    }
    onSelect = (selected: string) => {
        this.formRef.current?.resetFields()
        this.formRef.current?.setFieldsValue(this.map.get(selected))
        this.setState({ selected })
    }
    renderTree: (item: Data) => DataNode = item => {
        const key = item.key
        const type = key.slice(14)
        const title = <DropdownMenu type={type}
            onAdd={type => {
                if (!item.children) {
                    item.children = [];
                }
                const key = new Date().valueOf() + "-" + type
                const data = { key, type }
                item.children.push(data);
                this.map.set(key, { title: menus.get(type), ...defaultFieldsValues(type) })
                this.onSelect(data.key)
                this.props.onSave({ tree: this.state.tree, map: this.map })
                this.setState({
                    tree: this.state.tree,
                });
            }}
            onDelete={["Root", "BeforeProcess", "BeforeThread", "AfterProcess", "AfterThread"].includes(type) ? undefined : () => {
                loop([this.state.tree], key, (item, index, arr) => {
                    arr.splice(index, 1);
                })
                this.onSelect(this.state.tree.key)
                this.props.onSave({ tree: this.state.tree, map: this.map })
                this.setState({
                    tree: this.state.tree,
                });
            }}
        >
            <div onClick={() => this.onSelect(item.key)}>
                <img src={Icons.get(type)} alt="" />
                <span>&nbsp;{this.map.get(item.key)?.title}</span>
            </div>
        </DropdownMenu>
        const children = item.children?.map(this.renderTree)
        return { key, title, children }
    }

    render() {
        if (!this.state) return null
        return <div className="TreeOrchestrate">
            <div className="Tree">
                <p>鼠标右键添加子节点</p>
                <Tree className="Tree" defaultExpandParent draggable autoExpandParent defaultExpandAll selectedKeys={[this.state.selected]}
                    treeData={[this.state.tree].map(this.renderTree)}
                    onDrop={info => {
                        const dropKey = info.node.key;
                        const dragKey = info.dragNode.key;
                        const dropPosition = info.dropPosition;
                        // 不符合关系要求
                        const dropType = dropKey.toString().slice(14)
                        const dragType = dragKey.toString().slice(14)
                        if (!rules.get(dropType)?.has(dragType)) {
                            return
                        }

                        const data = [this.state.tree];
                        // Find dragObject
                        let dragObj: Data = { key: "" };
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
                        this.props.onSave({ tree: this.state.tree, map: this.map })
                        this.setState({
                            tree: this.state.tree,
                        });
                    }}
                />
            </div>
            <Form className="Form" requiredMark={false} ref={this.formRef} onValuesChange={async () => {
                try {
                    const data = await this.formRef.current?.validateFields()
                    this.map.set(this.state.selected, data)
                    this.setState({
                        tree: this.state.tree,
                    });
                    this.props.onSave({ tree: this.state.tree, map: this.map })
                } catch (error: any) {

                }
            }}>
                <h3>{menus.get(this.state.selected.slice(14))}</h3>
                <Form.Item name="title" label="名称" rules={[{ required: true }]}>
                    <Input maxLength={128} disabled={this.state.selected.slice(14) === "Root"} />
                </Form.Item>
                <Form.Item name="comments" label="注释">
                    <Input maxLength={128} />
                </Form.Item>
                <FormItems type={this.state.selected.slice(14)} />
            </Form>
        </div>
    }
}
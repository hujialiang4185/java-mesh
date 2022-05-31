import { message, Table, Transfer } from "antd"
import axios from "axios"
import { debounce } from "lodash"
import React, { Key, useCallback, useEffect, useRef, useState } from "react"
import "./TabelTransfer.scss"

type Data = { agent_id: string }
export default function App(props: { onChange?: (value: Data[]) => void, value?: Data[], type: string }) {
    const [leftData, setLeftData] = useState<{ data: Data[], total: number }>({ data: [], total: 0 })
    const [rightData, setRightData] = useState<Data[]>(props.value || [])
    const [loading, setLoading] = useState(false)
    const stateRef = useRef<any>({excludes: props.value?.map(function (item){ return item.agent_id})})
    const [leftSelectedRowKeys, setLeftSelectedRowKeys] = useState<Key[]>([])
    const [rightSelectedRowKeys, setRightSelectedRowKeys] = useState<Key[]>([])
    const [pageSize, setPageSize] = useState(10)
    const load = useCallback(async function () {
        setLoading(true)
        const params = {
            pageSize: stateRef.current.pagination?.pageSize || 10,
            current: stateRef.current.pagination?.current,
            sorter: stateRef.current.sorter?.field,
            order: stateRef.current.sorter?.order,
            ...stateRef.current.search,
            ...stateRef.current.filters,
            excludes: stateRef.current.excludes
        }
        try {
            const res = await axios.get("/api/host/agent_active/"+props.type, { params })
            setLeftData(res.data)
            setLeftSelectedRowKeys([])
            setRightSelectedRowKeys([])
        } catch (error: any) {
            message.error(error.message)
        }
        setLoading(false)
    }, [props.type])
    useEffect(function () {
        load()
    }, [load])
    const debounceRef = useRef(debounce(load, 1000))
    return <Transfer className="TabelTransfer" showSearch showSelectAll={false}
        onSearch={function (_, server_name) {
            stateRef.current.search = { server_name }
            debounceRef.current()
        }}
        onChange={function (_, direction, moveKeys) {
            let rightDataNew = rightData
            if (direction === "right") {
                rightDataNew = rightDataNew.concat(
                    leftData.data.filter(function (item) {
                        return moveKeys.includes(item.agent_id)
                    })
                )
            } else {
                rightDataNew = rightDataNew.filter(function (item) {
                    return !moveKeys.includes(item.agent_id)
                })
            }
            setRightData(rightDataNew)
            props.onChange?.(rightDataNew)
            const rightKeys = rightDataNew.map(function (item) { return item.agent_id })
            stateRef.current.excludes = rightKeys
            load()
        }}
    >{function (props) {
        const columns = [
            { title: "Agent名称", dataIndex: "agent_name" },
            { title: "服务器IP", dataIndex: "server_ip" }
        ]
        if (props.direction === "left") {
            return <Table size="small" rowKey="agent_id" dataSource={leftData.data} loading={loading}
                onChange={function (pagination, filters, sorter) {
                    props.onItemSelectAll(leftSelectedRowKeys as string[], false)
                    stateRef.current = { ...stateRef.current, pagination, filters, sorter }
                    load()
                    const currentPageSize = pagination.pageSize
                    if (currentPageSize && currentPageSize !== pageSize) {
                        setPageSize(currentPageSize)
                    }
                }}
                pagination={{ total: leftData.total, size: "small", pageSize, showSizeChanger: true, showTotal() { return `共 ${leftData.total} 条` } }}
                rowSelection={{
                    selectedRowKeys: leftSelectedRowKeys,
                    onChange(selectedRowKeys) {
                        props.onItemSelectAll(leftSelectedRowKeys as string[], false)
                        setLeftSelectedRowKeys(selectedRowKeys)
                        props.onItemSelectAll(selectedRowKeys as string[], true)
                    }
                }}
                columns={columns}
            />
        } else {
            return <Table size="small" rowKey="agent_id" dataSource={rightData}
                pagination={{ total: rightData.length, size: "small", pageSize, showSizeChanger: true, showTotal() { return `共 ${rightData.length} 条` } }}
                onChange={function (pagination){
                    props.onItemSelectAll(rightSelectedRowKeys as string[], false)
                    stateRef.current.pagination = {...stateRef.current.pagination, pageSize: pagination.pageSize}
                    load()
                    const currentPageSize = pagination.pageSize
                    if (currentPageSize && currentPageSize !== pageSize) {
                        setPageSize(currentPageSize)
                    }
                }}
                rowSelection={{
                    selectedRowKeys: rightSelectedRowKeys,
                    onChange(selectedRowKeys) {
                        props.onItemSelectAll(rightSelectedRowKeys as string[], false)
                        setRightSelectedRowKeys(selectedRowKeys)
                        props.onItemSelectAll(selectedRowKeys as string[], true)
                    }
                }}
                columns={columns}
            />
        }
    }}</Transfer>
}



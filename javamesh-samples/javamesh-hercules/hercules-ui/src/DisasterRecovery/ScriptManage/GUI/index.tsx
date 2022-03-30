import { Button, message } from "antd"
import axios from "axios"
import React, { useRef, useState } from "react"
import { Prompt, useHistory, useLocation } from "react-router-dom"
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import "./index.scss"
import TreeOrchestrate, { Values } from "../../../component/TreeOrchestrate"

export default function App() {
    let submit = false
    const script_id = new URLSearchParams(useLocation().search).get("script_id") || ""
    const [saved, setSaved] = useState(true)
    const valuesRef = useRef<Values>()
    const history = useHistory()
    return <div className="ScriptOrchestrate">
        <Breadcrumb label="脚本管理" sub={{ label: "编排", parentUrl: "/PerformanceTest/ScriptManage" }} />
        <Card>
            <Button type="primary" disabled={saved} onClick={async function() {
                if (submit) return
                submit = true
                try {
                    const map: any = {}
                    valuesRef.current?.map.forEach(function (value, key) {
                        map[key] = value
                    })
                    await axios.put("/argus-emergency/api/script/orchestrate", { tree: valuesRef.current?.tree, map, script_id })
                    setSaved(true)
                    message.success("保存成功")
                    // history.goBack();
                } catch (error: any) {
                    message.error(error.message)
                }
                submit = false
            }}>保存</Button>
            <TreeOrchestrate initialValues={async function () {
                const res = await axios.get("/argus-emergency/api/script/orchestrate/get", { params: { script_id } })
                const tree = res.data.data.tree
                const mapData = res.data.data.map
                const map = new Map()
                for (const key in mapData) {
                    map.set(key, mapData[key])
                }
                return { tree, map }
            }} onSave={async function (values) {
                saved && setSaved(false)
                valuesRef.current = values
                
            }} />
        </Card>
        <Prompt
            when={!saved}
            message="未保存, 是否离开"
        />
    </div>
}
import React from "react"
import Breadcrumb from "../../../component/Breadcrumb"
import Card from "../../../component/Card"
import './index.scss'
import TaskForm from "./TaskForm"

export default function App(){
    return <div className="TaskCreate">
        <Breadcrumb label="压测任务" sub={{ label: "创建测试", parentUrl: "/PerformanceTest/TestTask" }} />
        <Card>
            <TaskForm/>
        </Card>
    </div>
}
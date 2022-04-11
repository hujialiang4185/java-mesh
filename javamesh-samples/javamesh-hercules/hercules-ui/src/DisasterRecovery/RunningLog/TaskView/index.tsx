import { Line, LineOptions, Liquid, LiquidOptions } from "@antv/g2plot";
import { Descriptions, message, Tabs, Tag } from "antd";
import { PresetColorTypes } from "antd/lib/_util/colors";
import axios from "axios";
import moment from "moment";
import React, { useEffect, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import Breadcrumb from "../../../component/Breadcrumb";
import Card from "../../../component/Card";
import ServiceSelect from "../../../component/ServiceSelect";
import BusinessCharts from "./BusinessCharts";
import "./index.scss"

export default function App() {
    const [data, setData] = useState<any>({})
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const test_id = urlSearchParams.get("test_id")

    useEffect(function () {
        const interval = setInterval(load, 5000)
        async function load() {
            try {
                const res = await axios.get("/argus-emergency/api/task/view", { params: { test_id } })
                setData(res.data.data)
            } catch (error: any) {
                clearInterval(interval)
                message.error(error.message)
            }
        }
        load()
        return function () {
            clearInterval(interval)
        }
    }, [test_id])
    return <div className="TaskView">
        <Breadcrumb label="压测任务" sub={{ label: "实时TPS数据", parentUrl: "/PerformanceTest/TestTask" }} />
        <Card>
            <div className="Label">基本信息</div>
            <div className="SubCard Info">
                <Descriptions>
                    <Descriptions.Item label={
                        <div className="Title">测试名称</div>
                    }>{data.test_name}</Descriptions.Item>
                    <Descriptions.Item span={2} label={
                        <div className="Title">压测状态</div>
                    }>{data.status_label}</Descriptions.Item>
                    <Descriptions.Item label={
                        <div className="Title">标签</div>
                    }>{data.label?.map(function (item: string, index: number) {
                        return <Tag key={index} color={PresetColorTypes[index + 5 % 13]}>{item}</Tag>
                    })}</Descriptions.Item>
                    <Descriptions.Item span={2} label={
                        <div className="Title">描述</div>
                    }>{data.desc}</Descriptions.Item>
                </Descriptions>
            </div>
            <div className="SubCard Basic">
                <div className="Item">
                    <div className="Value">{data.duration}</div>
                    <div className="Title">运行时间(s)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.vuser}</div>
                    <div className="Title">虚拟用户数</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.tps}</div>
                    <div className="Title">TPS</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.tps_peak}</div>
                    <div className="Title">TPS峰值</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.avg_time}</div>
                    <div className="Title">平均时间(ms)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.test_count}</div>
                    <div className="Title">执行测试数量</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.success_count}</div>
                    <div className="Title">测试成功数量</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.fail_count}</div>
                    <div className="Title">错误</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.agent}</div>
                    <div className="Title">Agent数量</div>
                </div>
            </div>
            <div className="SubCard Basic">
                <div className="Item">
                    <div className="Value">{data.start_time}</div>
                    <div className="Title">开始时间</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.response_time25}</div>
                    <div className="Title">响应时间中位数P25(ms)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.response_time50}</div>
                    <div className="Title">响应时间中位数P50(ms)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.response_time75}</div>
                    <div className="Title">响应时间中位数P75(ms)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.response_time90}</div>
                    <div className="Title">响应时间中位数P90(ms)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.response_time95}</div>
                    <div className="Title">响应时间中位数P95(ms)</div>
                </div>
                <div className="Item">
                    <div className="Value">{data.response_time99}</div>
                    <div className="Title">响应时间中位数P99(ms)</div>
                </div>
            </div>
            <div className="Label">TPS图表</div>
            <Tabs type="card" size="small">
                <Tabs.TabPane tab="业务性能指标" key="BusinessCharts">
                    <BusinessCharts />
                </Tabs.TabPane>
                <Tabs.TabPane tab="硬件资源指标" key="ResourceCharts">
                    <ResourceCharts />
                </Tabs.TabPane>
                <Tabs.TabPane tab="JVM性能指标" key="JvmCharts">
                    <JvmCharts />
                </Tabs.TabPane>
            </Tabs>
            <div className="Label">日志文件</div>
            {data.log_name?.map(function (item: string, index: number) {
                return <div key={index} >
                    <a href={`/argus/api/task/download?test_id=${test_id}&log_name=${item}`} target="_blank" rel="noreferrer">{item}</a>
                </div>
            })}
            <div className="Label">执行日志</div>
            {data.progress_message?.map(function (item: string, index: number) {
                return <div key={index}>{item}</div>
            })}
        </Card>
    </div>
}

function ResourceCharts() {
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const test_id = urlSearchParams.get("test_id") || ""
    const cpuUsageRef = useRef(null)
    const memoryUsageRef = useRef(null)
    const ioBusyRef = useRef(null)
    const cpuRef = useRef(null)
    const memoryRef = useRef(null)
    const diskRef = useRef(null)
    const networkRef = useRef(null)
    const [data, setData] = useState<any>({})
    const ipRef = useRef("")
    const chartsRef = useRef<{
        cpuUsageChart: Liquid,
        memoryUsageChart: Liquid,
        ioBusyChart: Liquid,
        cpuChart: Line,
        memoryChart: Line,
        diskChart: Line,
        networkChart: Line,
        second: number,
        interval: NodeJS.Timeout
    }>()
    useEffect(function () {
        const liquidOption: LiquidOptions = {
            percent: 0,
            outline: {
                border: 2,
                distance: 4,
            },
            wave: {
                length: 64,
            },
            statistic: {
                content: {
                    style(data) {
                        const percent = (data as { percent: number }).percent
                        return {
                            fontSize: "20px",
                            fill: percent > 0.65 ? 'white' : 'rgba(44,53,66,0.85)',
                        }
                    },
                    formatter(data) {
                        const percent = (data as { percent: number }).percent
                        return `${(percent * 100).toFixed(0)}%`
                    }
                }
            },
            liquidStyle({ percent }) {
                let color = '#5B8FF9'
                if (percent > 0.6) {
                    color = '#FAAD14'
                }
                if (percent > 0.8) {
                    color = '#ff4d4f'
                }
                return {
                    fill: color,
                    stroke: color,
                };
            },
        }
        const cpuUsageChart = new Liquid(cpuUsageRef.current!!, liquidOption)
        const memoryUsageChart = new Liquid(memoryUsageRef.current!!, liquidOption)
        const ioBusyChart = new Liquid(ioBusyRef.current!!, liquidOption)
        const lineOption: LineOptions = {
            data: [],
            xField: "time",
            yField: "value",
            seriesField: "name",
            xAxis: { tickInterval: 10, range: [0, 1] },
            smooth: true,
            area: {
                style: {
                    fillOpacity: 0.05,
                },
            },
            animation: false,
        }
        const cpuChart = new Line(cpuRef.current!!, {
            ...lineOption, yAxis: {
                label: {
                    formatter(text: any) {
                        return text + "%"
                    }
                }
            }
        })
        const memoryChart = new Line(memoryRef.current!!, {
            ...lineOption,
            yAxis: {
                label: {
                    formatter(text: any) {
                        return text + "MB"
                    }
                }
            }
        })
        const diskChart = new Line(diskRef.current!!, lineOption)
        const networkChart = new Line(networkRef.current!!, lineOption)

        cpuUsageChart.render()
        memoryUsageChart.render()
        ioBusyChart.render()
        cpuChart.render()
        memoryChart.render()
        diskChart.render()
        networkChart.render()
        const interval = setInterval(function () {
            load(test_id)
        }, 1000)
        chartsRef.current = {
            cpuUsageChart,
            memoryUsageChart,
            ioBusyChart,
            cpuChart,
            memoryChart,
            diskChart,
            networkChart,
            second: 0,
            interval
        }
        load(test_id, true)
        return function () {
            chartsRef.current = undefined
            clearInterval(interval)
            cpuUsageChart.destroy()
            memoryUsageChart.destroy()
            ioBusyChart.destroy()
            cpuChart.destroy()
            memoryChart.destroy()
            diskChart.destroy()
            networkChart.destroy()
        }
    }, [test_id])
    async function load(test_id: string, reset = false) {
        try {
            const res = await axios.get("/argus-emergency/api/task/resource", { params: { test_id, ip: ipRef.current } })
            const data = res.data.data
            if (!chartsRef.current) return
            if (reset) chartsRef.current.second = 0
            setData({
                ip: data.ip,
                cpu: data.cpu,
                memory: data.memory,
                start_up: data.start_up
            })
            const current = chartsRef.current
            current.cpuUsageChart.changeData(data.cpu_usage)
            current.memoryUsageChart.changeData(data.memory_usage)
            current.ioBusyChart.changeData(data.io_busy)

            const second = current.second
            const time = moment(new Date(second * 1000)).format("mm:ss")
            // CPU
            let chart = current.cpuChart
            let items = [
                { time, value: data.cpu_user, name: "user" },
                { time, value: data.cpu_sys, name: "sys" },
                { time, value: data.cpu_wait, name: "wait" },
                { time, value: data.cpu_idle, name: "idle" },
            ]
            let chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // Memory
            chart = current.memoryChart
            items = [
                { time, value: data.memory_total, name: "memoryTotal" },
                { time, value: data.memory_swap, name: "swapCache" },
                { time, value: data.memory_buffers, name: "buffers" },
                { time, value: data.memory_used, name: "memoryUsed" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()
            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // Disk
            chart = current.diskChart
            items = [
                { time, value: data.disk_read, name: "readBytesPerSec" },
                { time, value: data.disk_write, name: "writeBytesPerSec" },
                { time, value: data.disk_busy, name: "ioSpentPercentage" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()
            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // Network
            chart = current.networkChart
            items = [
                { time, value: data.network_rbyte, name: "readBytesPerSec" },
                { time, value: data.network_wbyte, name: "writeBytesPerSec" },
                { time, value: data.memory_rpackage, name: "readPackagePerSec" },
                { time, value: data.memory_wpackage, name: "writePackagePerSec" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()
            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // End
            current.second++
        } catch (error: any) {
            message.error(error.message)
            if (chartsRef.current) {
                clearInterval(chartsRef.current.interval)
            }
        }
    }
    return <div className="ResourceCharts">
        <ServiceSelect value={data.ip} placeholder="IP地址" url={"/argus-emergency/api/task/search/ip?test_id=" + test_id} onChange={function (value) {
            ipRef.current = value
            load(test_id, true)
        }} />
        <div className="Grid">
            <div className="Item Middle">
                <div className="Value">{data.cpu}</div>
                <div className="Title">CPU核心数</div>
            </div>
            <div className="Item Middle">
                <div className="Value">{data.memory}GiB</div>
                <div className="Title">内存大小</div>
            </div>
            <div className="Item Middle">
                <div className="Value">{data.start_up}小时</div>
                <div className="Title">启动时间</div>
            </div>
        </div>
        <div className="Grid">
            <div className="Item">
                <div ref={cpuUsageRef} className="Liquid"></div>
                <div className="Title">CPU利用率</div>
            </div>
            <div className="Item">
                <div ref={memoryUsageRef} className="Liquid"></div>
                <div className="Title">内存利用率</div>
            </div>
            <div className="Item">
                <div ref={ioBusyRef} className="Liquid"></div>
                <div className="Title">IO繁忙率</div>
            </div>
        </div>
        <div className="Grid">
            <div className="Item">
                <div ref={cpuRef} className="Line"></div>
                <div className="Title">CPU</div>
            </div>
            <div className="Item">
                <div ref={memoryRef} className="Line"></div>
                <div className="Title">内存</div>
            </div>
        </div>
        <div className="Grid">
            <div className="Item">
                <div ref={diskRef} className="Line"></div>
                <div className="Title">磁盘IO</div>
            </div>
            <div className="Item">
                <div ref={networkRef} className="Line"></div>
                <div className="Title">网络IO</div>
            </div>
        </div>
    </div>
}

function JvmCharts() {
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const test_id = urlSearchParams.get("test_id") || ""
    const cpuRef = useRef(null)
    const heapRef = useRef(null)
    const memoryRef = useRef(null)
    const jvmRef = useRef(null)
    const gcRef = useRef(null)
    const threadRef = useRef(null)
    const [data, setData] = useState<any>({})
    const ipRef = useRef("")
    const chartsRef = useRef<{
        cpuChart: Line,
        heapChart: Line,
        memoryChart: Line,
        jvmChart: Line,
        gcChart: Line,
        threadChart: Line,
        second: number,
        interval: NodeJS.Timeout
    }>()
    useEffect(function () {
        const lineOption: LineOptions = {
            data: [],
            xField: "time",
            yField: "value",
            seriesField: "name",
            xAxis: { tickInterval: 10, range: [0, 1] },
            smooth: true,
            area: {
                style: {
                    fillOpacity: 0.05,
                },
            },
            animation: false,
        }
        const cpuChart = new Line(cpuRef.current!!, {
            ...lineOption, yAxis: {
                label: {
                    formatter(text: any) {
                        return text + "%"
                    }
                }
            }
        })
        const heapChart = new Line(heapRef.current!!, {
            ...lineOption, yAxis: {
                label: {
                    formatter(text: any) {
                        return text + "MB"
                    }
                }
            }
        })
        const memoryChart = new Line(memoryRef.current!!, {
            ...lineOption, yAxis: {
                label: {
                    formatter(text: any) {
                        return text + "MB"
                    }
                }
            }
        })
        const jvmChart = new Line(jvmRef.current!!, {
            ...lineOption, yAxis: {
                label: {
                    formatter(text: any) {
                        return text + "MB"
                    }
                }
            }
        })
        const gcChart = new Line(gcRef.current!!, lineOption)
        const threadChart = new Line(threadRef.current!!, lineOption)
        cpuChart.render()
        heapChart.render()
        memoryChart.render()
        jvmChart.render()
        gcChart.render()
        threadChart.render()

        const interval = setInterval(function () {
            load(test_id)
        }, 1000)
        chartsRef.current = {
            cpuChart,
            heapChart,
            memoryChart,
            jvmChart,
            gcChart,
            threadChart,
            second: 0,
            interval
        }
        load(test_id, true)
        return function () {
            cpuChart.destroy()
            heapChart.destroy()
            memoryChart.destroy()
            jvmChart.destroy()
            gcChart.destroy()
            threadChart.destroy()
            clearInterval(interval)
            chartsRef.current = undefined
        }
    }, [test_id])
    async function load(test_id: string, reset = false) {
        try {
            const res = await axios.get("/argus-emergency/api/task/jvm", { params: { test_id, ip: ipRef.current } })
            const data = res.data.data
            if (!chartsRef.current) return
            if (reset) chartsRef.current.second = 0
            setData({ ip: data.ip })
            const current = chartsRef.current

            const second = current.second
            const time = moment(new Date(second * 1000)).format("mm:ss")
            // CPU
            let chart = current.cpuChart
            let items = [
                { time, value: data.cpu_java, name: "cpuForJava" },
            ]
            let chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // Heap
            chart = current.heapChart
            items = [
                { time, value: data.heap_init, name: "init" },
                { time, value: data.heap_max, name: "max" },
                { time, value: data.heap_used, name: "used" },
                { time, value: data.heap_committed, name: "committed" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // Memory
            chart = current.memoryChart
            items = [
                { time, value: data.memory_init, name: "init" },
                { time, value: data.memory_max, name: "max" },
                { time, value: data.memory_used, name: "used" },
                { time, value: data.memory_committed, name: "committed" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // Usage
            chart = current.jvmChart
            items = [
                { time, value: data.jvm_cache, name: "codeCache" },
                { time, value: data.jvm_newgen, name: "newGen" },
                { time, value: data.jvm_oldgen, name: "oldGen" },
                { time, value: data.jvm_survivor, name: "survivor" },
                { time, value: data.jvm_penmgen, name: "permegen" },
                { time, value: data.jvm_metaspace, name: "mateSpace" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // GC
            chart = current.gcChart
            items = [
                { time, value: data.gc_newc, name: "newGenCount" },
                { time, value: data.gc_oldc, name: "oldGenCount" },
                { time, value: data.gc_news, name: "newGenSpend" },
                { time, value: data.gc_olds, name: "oldGenSpend" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // GC
            chart = current.threadChart
            items = [
                { time, value: data.thread_count, name: "liveCount" },
                { time, value: data.thread_daemon, name: "deamonCount" },
                { time, value: data.thread_peak, name: "peakCount" },
            ]
            chartData = reset ? Array.from({ length: 91 * items.length }, function (_, index) {
                return {
                    time: moment(new Date(Math.floor(index / items.length) * 1000)).format("mm:ss"),
                }
            }) : chart.chart.getData()

            if (second > 90) {
                chartData.splice(0, items.length)
                chartData.push(...items)
            } else {
                chartData.splice(second * items.length, items.length, ...items)
            }
            chart.changeData(chartData)
            // End
            current.second++
        } catch (error: any) {
            message.error(error.message)
            if (chartsRef.current) {
                clearInterval(chartsRef.current.interval)
            }
        }
    }
    return <div className="ResourceCharts">
        <ServiceSelect value={data.ip} placeholder="IP地址" url={"/argus-emergency/api/task/search/ip?test_id=" + test_id} onChange={function (value) {
            ipRef.current = value
            load(test_id, true)
        }} />
        <div className="Grid">
            <div className="Item">
                <div ref={cpuRef} className="Line"></div>
                <div className="Title">CPU</div>
            </div>
            <div className="Item">
                <div ref={heapRef} className="Line"></div>
                <div className="Title">Heap</div>
            </div>
        </div>
        <div className="Grid">
            <div className="Item">
                <div ref={memoryRef} className="Line"></div>
                <div className="Title">MemoryPool</div>
            </div>
            <div className="Item">
                <div ref={jvmRef} className="Line"></div>
                <div className="Title">JVM Memory</div>
            </div>
        </div>
        <div className="Grid">
            <div className="Item">
                <div ref={gcRef} className="Line"></div>
                <div className="Title">GC</div>
            </div>
            <div className="Item">
                <div ref={threadRef} className="Line"></div>
                <div className="Title">Thread</div>
            </div>
        </div>
    </div>
}
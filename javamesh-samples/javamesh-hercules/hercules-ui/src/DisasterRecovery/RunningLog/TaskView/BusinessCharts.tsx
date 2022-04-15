import { message, Table } from "antd"
import axios from "axios"
import React from "react"
import { useEffect, useRef, useState } from "react"
import { useLocation } from "react-router-dom"
import { debounce } from 'lodash'
import * as echarts from 'echarts'
import "./BusinessCharts.scss"
import { Moment } from "moment"

export default function BusinessCharts() {
    const [services, setServices] = useState()
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const test_id = urlSearchParams.get("test_id") || ""
    const tpsRef = useRef(null)
    const userRef = useRef(null)
    const meanRef = useRef(null)
    const chartsRef = useRef<{
        tps: echarts.ECharts,
        user: echarts.ECharts,
        mean: echarts.ECharts
    }>()
    async function load(test_id: string, values?: { end?: Moment, start?: number, step?: string }) {
        try {
            const end = values?.end ? values.end.valueOf() : new Date().getTime()
            const start = end - (values?.start || 3600000)
            const step = values?.step?.replace(/[^0-9]/ig, "");
            const metricsRes = await axios.get<{
                data: {
                    time: number[],
                    errors: number[],
                    vuser: number[],
                    tps: number[],
                    user_defined: number[],
                    mean_test_time: number[],
                    mean_time_to_first_byte: number[]
                },
            }>('/argus-emergency/api/task/metrics', { params: { test_id, start, end, step } })
            const data = metricsRes.data.data

            const errors: number[][] = []
            const vuser: number[][] = []
            const tps: number[][] = []
            const user_defined: number[][] = []
            const mean_test_time_ms: number[][] = []
            const mean_time_to_first_byte: number[][] = []
            data.time.forEach(function (time, index) {
                tps.push([time, data.tps[index]])
                errors.push([time, data.errors[index]])
                vuser.push([time, data.vuser[index]])
                user_defined.push([time, data.user_defined[index]])
                mean_test_time_ms.push([time, data.mean_test_time[index]])
                mean_time_to_first_byte.push([time, data.mean_time_to_first_byte[index]])
            })
            chartsRef.current?.tps.setOption({
                series: [
                    {
                        name: 'TPS',
                        type: 'line',
                        showSymbol: false,
                        data: tps
                    },
                    {
                        name: 'Errors',
                        type: 'line',
                        showSymbol: false,
                        data: errors
                    }
                ]
            })
            chartsRef.current?.user.setOption({
                series: [
                    {
                        name: 'Vuser',
                        type: 'line',
                        showSymbol: false,
                        data: vuser
                    },
                    {
                        name: 'User Defined',
                        type: 'line',
                        showSymbol: false,
                        data: user_defined
                    },
                ]
            })
            chartsRef.current?.mean.setOption({
                series: [
                    {
                        name: 'Mean Test Time(ms)',
                        type: 'line',
                        showSymbol: false,
                        data: mean_test_time_ms
                    },
                    {
                        name: 'Mean Time To First Byte(ms)',
                        type: 'line',
                        showSymbol: false,
                        data: mean_time_to_first_byte
                    },
                ]
            })
            const res = await axios.get('/argus-emergency/api/task/service', { params: { test_id } })
            setServices(res.data.data)
        } catch (error: any) {
            message.error(error.message)
        }
    }
    useEffect(function () {
        const tps = echarts.init(tpsRef.current!)
        const user = echarts.init(userRef.current!)
        const mean = echarts.init(meanRef.current!)
        const option = {
            grid: {
                top: 50,
                left: 50,
                right: 50,
                bottom: 50,
            },
            legend: {
                top: 20
            },
            toolbox: {
                top: 20,
                right: 30,
                feature: {
                    saveAsImage: {},
                    dataView: {},
                }
            },
            tooltip: {
                trigger: 'axis',
            },
            xAxis: {
                type: 'time',
            },
            yAxis: {
                type: 'value',
                boundaryGap: ["0%", '20%'],
                splitLine: {
                    show: true
                }
            },
        }
        tps.setOption(option)
        user.setOption(option)
        mean.setOption(option)
        chartsRef.current = { tps, user, mean }
        // 自动缩放
        const resize = debounce(function () {
            tps.resize({width: 0})
            user.resize({width: 0})
            mean.resize()
            tps.resize({width: "auto"})
            user.resize({width: "auto"})
        }, 1000)
        window.addEventListener("resize", resize, false);
        return function () {
            tps.dispose()
            user.dispose()
            mean.dispose()
            window.removeEventListener("resize", resize, false)
        }
    }, [])
    useEffect(function () {
        load(test_id, {})
    }, [test_id])
    return <div className="BusinessCharts">
        <div ref={tpsRef} style={{ height: 300 }}></div>
        <div ref={userRef} style={{ height: 300 }}></div>
        <div ref={meanRef} style={{ height: 300 }}></div>
        <Table dataSource={services} size="small" rowKey="transaction" pagination={false} columns={[
            { title: "事务名称", dataIndex: "transaction" },
            { title: "TPS", dataIndex: "tps" },
            { title: "响应时间(ms)", dataIndex: "response_ms" },
            { title: "成功数", dataIndex: "success_count" },
            { title: "失败数", dataIndex: "fail_count" },
            { title: "失败率%", dataIndex: "fail_rate" }
        ]} />
    </div>
}
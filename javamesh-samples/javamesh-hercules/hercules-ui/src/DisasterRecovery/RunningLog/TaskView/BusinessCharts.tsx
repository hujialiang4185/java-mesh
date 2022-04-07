import { Button, DatePicker, Form, Input, Table } from "antd"
import axios from "axios"
import React from "react"
import { useEffect, useRef, useState } from "react"
import { useLocation } from "react-router-dom"
import { debounce } from 'lodash'
import * as echarts from 'echarts'
import "./BusinessCharts.scss"

export default function BusinessCharts() {
    const [services, setServices] = useState()
    const urlSearchParams = new URLSearchParams(useLocation().search)
    const test_id = urlSearchParams.get("test_id") || ""
    const tpsRef = useRef(null)
    const chartsRef = useRef<{
        tps: echarts.ECharts
    }>()
    async function load(test_id: string) {
        const res = await axios.get('/argus-emergency/api/task/service', { params: { test_id } })
        setServices(res.data.data)
    }
    useEffect(function(){
        const chart = echarts.init(tpsRef.current!)
        chart.setOption({
            grid: {
                top: 30,
                left: 50,
                right: 30,
                bottom: 30,
            },
            legend: {},
            toolbox: {
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
        })
        chartsRef.current = { tps: chart }
        // 自动缩放
        const resize = debounce(function(){
            chart.resize()
        }, 1000)
        window.addEventListener("resize", resize, false);
        return function() {
            window.removeEventListener("resize", resize, false)
        }
    },[])
    useEffect(function () {
        load(test_id)
        let base = +new Date(2022, 3, 15);

        let data = [[base, Math.random() * 300]];

        for (let i = 1; i < 200; i++) {
            let now = new Date((base += 1000));
            data.push([+now, Math.abs(Math.round((Math.random() - 0.5) * 20 + data[i - 1][1]))]);
        }
        chartsRef.current?.tps.setOption({
            series: [
                {
                    name: 'Fake Data',
                    type: 'line',
                    showSymbol: false,
                    data: data
                }
            ]
        })
    }, [test_id])
    return <div className="BusinessCharts">
        <Form layout="inline" initialValues={{ start: "1h" }}>
            <Form.Item name="start">
                <RangeInput />
            </Form.Item>
            <Form.Item name="end">
                <DatePicker placeholder="结束时间" showTime />
            </Form.Item>
            <Form.Item name="step">
                <Input placeholder="采样精度(s)" />
            </Form.Item>
            <Button type="primary">查询</Button>
        </Form>
        <div className="Chart" ref={tpsRef} style={{ height: 200 }}></div>
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

const rangeSteps = [
    1000 * 1,
    1000 * 10,
    1000 * 60,
    1000 * 5 * 60,
    1000 * 15 * 60,
    1000 * 30 * 60,
    1000 * 60 * 60,
    1000 * 2 * 60 * 60,
    1000 * 6 * 60 * 60,
    1000 * 12 * 60 * 60,
    1000 * 24 * 60 * 60,
    1000 * 48 * 60 * 60,
    1000 * 7 * 24 * 60 * 60,
    1000 * 14 * 24 * 60 * 60,
    1000 * 28 * 24 * 60 * 60,
    1000 * 56 * 24 * 60 * 60,
    1000 * 112 * 24 * 60 * 60,
    1000 * 182 * 24 * 60 * 60,
    1000 * 365 * 24 * 60 * 60,
    1000 * 730 * 24 * 60 * 60,
]

function parseDuration(durationStr: string) {
    if (durationStr === '') {
        return null;
    }
    if (durationStr === '0') {
        // Allow 0 without a unit.
        return 0;
    }

    const durationRE = new RegExp('^(([0-9]+)y)?(([0-9]+)w)?(([0-9]+)d)?(([0-9]+)h)?(([0-9]+)m)?(([0-9]+)s)?(([0-9]+)ms)?$');
    const matches = durationStr.match(durationRE);
    if (!matches) {
        return null;
    }

    let dur = 0;

    // Parse the match at pos `pos` in the regex and use `mult` to turn that
    // into ms, then add that value to the total parsed duration.
    const m = (pos: number, mult: number) => {
        if (matches[pos] === undefined) {
            return;
        }
        const n = parseInt(matches[pos]);
        dur += n * mult;
    };

    m(2, 1000 * 60 * 60 * 24 * 365); // y
    m(4, 1000 * 60 * 60 * 24 * 7); // w
    m(6, 1000 * 60 * 60 * 24); // d
    m(8, 1000 * 60 * 60); // h
    m(10, 1000 * 60); // m
    m(12, 1000); // s
    m(14, 1); // ms

    return dur;
};

function formatDuration(d: number) {
    let ms = d;
    let r = '';
    if (ms === 0) {
        return '0s';
    }

    const f = (unit: string, mult: number, exact: boolean) => {
        if (exact && ms % mult !== 0) {
            return;
        }
        const v = Math.floor(ms / mult);
        if (v > 0) {
            r += `${v}${unit}`;
            ms -= v * mult;
        }
    };

    // Only format years and weeks if the remainder is zero, as it is often
    // easier to read 90d than 12w6d.
    f('y', 1000 * 60 * 60 * 24 * 365, true);
    f('w', 1000 * 60 * 60 * 24 * 7, true);

    f('d', 1000 * 60 * 60 * 24, false);
    f('h', 1000 * 60 * 60, false);
    f('m', 1000 * 60, false);
    f('s', 1000, false);
    f('ms', 1, false);

    return r;
};


function RangeInput(props: { onChange?: (value: number) => void }) {
    const [value, setValue] = useState("1h")
    const lastRef = useRef(value)

    const onChangeRef = useRef(debounce(function (value: string) {
        const time = parseDuration(value)
        if (time) {
            const str = formatDuration(time)
            lastRef.current = str
            setValue(str)
            props.onChange?.(time)
        } else {
            setValue(lastRef.current)
        }
    }, 1000))
    return <div className="RangeInput">
        <Button onClick={function () {
            for (let i = rangeSteps.length - 1; i >= 0; i--) {
                const time = parseDuration(value)
                if (!time) return
                const element = rangeSteps[i];
                if (element < time) {
                    setValue(formatDuration(element))
                    props.onChange?.(element)
                    return
                }
            }
        }}>-</Button>
        <Input value={value} onChange={function (e) {
            setValue(e.target.value)
            onChangeRef.current(e.target.value)
        }} />
        <Button onClick={function () {
            for (let i = 0, len = rangeSteps.length; i < len; i++) {
                const time = parseDuration(value)
                if (!time) return
                const element = rangeSteps[i];
                if (element > time) {
                    setValue(formatDuration(element))
                    props.onChange?.(element)
                    return
                }
            }
        }}>+</Button>
    </div>
}
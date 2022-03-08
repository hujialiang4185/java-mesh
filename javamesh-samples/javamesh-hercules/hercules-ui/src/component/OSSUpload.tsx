import React from "react"
import { UploadOutlined } from '@ant-design/icons'
import { Button, Upload } from "antd"

export default function App(props: { max: number, value?: string, onChange?: (value: string) => void, mark?: string }) {
    return <Upload action={'/argus-emergency/api/resource'+window.location.search+props.mark}
        defaultFileList={props.value?.split(" ").map(function (item) {
            const index = item.lastIndexOf("/")
            const uid = item.slice(0, index)
            const name = item.slice(index + 1)
            return { uid, name, url: '/argus-emergency/api/resource/' + item, status: "done", response: {data: {uid}} }
        })}
        maxCount={props.max}
        onChange={function (info) {
            props.onChange?.(info.fileList.filter(function (item) { return item.status === "done" }).map(function (item) {
                return item.response.data.uid + "/" + item.name
            }).join(" "))
        }}>
        <Button icon={<UploadOutlined />}>上传文件</Button>
    </Upload>
}
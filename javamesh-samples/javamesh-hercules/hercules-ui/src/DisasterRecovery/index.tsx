import React from "react"
import { Route, Switch, useRouteMatch } from "react-router-dom"
import SubMenu from "../component/SubMenu"
import ScriptManage from "./ScriptManage"
import "./index.scss"
import NoMatch from "../component/NoMatch"
import PlanManage from "./PlanManage"
import HostManage from "./HostManage"
import RunningLog from "./RunningLog"

export default function App() {
  const { path } = useRouteMatch();
  const menuList = [
    { path: path, label: "压测引擎", comp: <HostManage />, exact: true },
    { path: path + "/ScriptManage", label: "脚本管理", comp: <ScriptManage />, exact: false },
    { path: path + "/PlanManage", label: "项目管理", comp: <PlanManage />, exact: false },
    { path: path + "/RunningLog", label: "压测报告", comp: <RunningLog />, exact: false },
  ]
  return <div className="AppBody">
    <SubMenu menuList={menuList}>性能测试</SubMenu>
    <div className="AppRoute">
      <Switch>
        {menuList.map(function (item) {
          return <Route key={item.path} exact={item.exact} path={item.path}>{item.comp}</Route>
        })}
        <Route path="*"><NoMatch /></Route>
      </Switch>
    </div>
  </div>
}
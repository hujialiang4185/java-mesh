import React from "react"
import "./index.scss"
import { Route, Switch, useRouteMatch } from "react-router-dom"
import SubMenu from "../component/SubMenu"
import NoMatch from "../component/NoMatch"
import User from "./User"
import Group from "./Group"
import LogAudit from "./LogAudit"

export default function App() {
    const { path } = useRouteMatch();
    const menuList = [
        { path: path, label: "用户管理", comp: <User />, exact: true },
        { path: path + "/Group", label: "分组管理", comp: <Group />, exact: false },
        { path: path + "/Audit", label: "日志审计", comp: <LogAudit />, exact: false },
    ]
    return <div className="AppBody">
    <SubMenu menuList={menuList}>系统配置</SubMenu>
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
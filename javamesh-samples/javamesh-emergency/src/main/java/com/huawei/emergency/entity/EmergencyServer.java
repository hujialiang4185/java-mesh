package com.huawei.emergency.entity;

import java.util.Date;

public class EmergencyServer {
    private Integer serverId;

    private String serverName;

    private String serverUser;

    private String serverIp;

    private Integer serverPort;

    private Integer serverMemory;

    private String havePassword;

    private String passwordMode;

    private String passwordUri;

    private String password;

    private String createUser;

    private Date createTime;

    private Date updateTime;

    private String updateUser;

    private String isValid;

    private String serverGroup;

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName == null ? null : serverName.trim();
    }

    public String getServerUser() {
        return serverUser;
    }

    public void setServerUser(String serverUser) {
        this.serverUser = serverUser == null ? null : serverUser.trim();
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp == null ? null : serverIp.trim();
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getServerMemory() {
        return serverMemory;
    }

    public void setServerMemory(Integer serverMemory) {
        this.serverMemory = serverMemory;
    }

    public String getHavePassword() {
        return havePassword;
    }

    public void setHavePassword(String havePassword) {
        this.havePassword = havePassword == null ? null : havePassword.trim();
    }

    public String getPasswordMode() {
        return passwordMode;
    }

    public void setPasswordMode(String passwordMode) {
        this.passwordMode = passwordMode == null ? null : passwordMode.trim();
    }

    public String getPasswordUri() {
        return passwordUri;
    }

    public void setPasswordUri(String passwordUri) {
        this.passwordUri = passwordUri == null ? null : passwordUri.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid == null ? null : isValid.trim();
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup == null ? null : serverGroup.trim();
    }
}
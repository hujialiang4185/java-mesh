package com.huawei.emergency.entity;

import java.util.Date;

public class EmergencyElement {
    private Integer elementId;

    private String elementNo;

    private String elementTitle;

    private String elementType;

    private Integer parentId;

    private Integer scriptId;

    private String isValid;

    private String createUser;

    private Date createTime;

    private Integer seq;

    private String elementParams;

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getElementNo() {
        return elementNo;
    }

    public void setElementNo(String elementNo) {
        this.elementNo = elementNo == null ? null : elementNo.trim();
    }

    public String getElementTitle() {
        return elementTitle;
    }

    public void setElementTitle(String elementTitle) {
        this.elementTitle = elementTitle == null ? null : elementTitle.trim();
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType == null ? null : elementType.trim();
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getScriptId() {
        return scriptId;
    }

    public void setScriptId(Integer scriptId) {
        this.scriptId = scriptId;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid == null ? null : isValid.trim();
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

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getElementParams() {
        return elementParams;
    }

    public void setElementParams(String elementParams) {
        this.elementParams = elementParams == null ? null : elementParams.trim();
    }
}
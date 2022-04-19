package com.huawei.agent.entity;

import lombok.Data;

@Data
public class ExecParam {
    private int detailId;

    private String content;

    private String scriptType;

    private String param;

    private String scriptName;
}

package com.huawei.agent.entity;

import lombok.Data;

@Data
public class ExecResult {
    private int detailId;
    private String msg;
    private int code;

    public static ExecResult success(int recordId,String info){
        ExecResult execResult = new ExecResult();
        execResult.setDetailId(recordId);
        execResult.setMsg(info);
        execResult.setCode(0);
        return execResult;
    }

    public static ExecResult fail(int recordId,String errorInfo){
        ExecResult execResult =new ExecResult();
        execResult.setDetailId(recordId);
        execResult.setMsg(errorInfo);
        execResult.setCode(-1);
        return execResult;
    }
}

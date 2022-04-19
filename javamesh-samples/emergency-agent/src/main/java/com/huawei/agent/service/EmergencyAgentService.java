package com.huawei.agent.service;

import com.huawei.agent.common.api.CommonResult;
import com.huawei.agent.entity.ExecParam;

import javax.servlet.http.HttpServletRequest;

public interface EmergencyAgentService extends EmergencyCallBack,EmergencyCache {
    CommonResult exec(HttpServletRequest request,ExecParam execParam);

    CommonResult cancel(int recordId,String scriptType);
}

package com.huawei.agent.controller;

import com.huawei.agent.common.api.CommonResult;
import com.huawei.agent.entity.ExecParam;
import com.huawei.agent.service.EmergencyAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class EmergencyAgentController {
    @Autowired
    private EmergencyAgentService service;

    @PostMapping("/execute")
    public CommonResult exec(HttpServletRequest request, @RequestBody ExecParam execParam){
        return service.exec(request,execParam);
    }

    @GetMapping("cancel")
    public CommonResult cancel(int recordId,String scriptType){
        return service.cancel(recordId,scriptType);
    }

}

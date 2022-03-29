package com.huawei.user.controller;

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.entity.EmergencyGroup;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class GroupController {
    @Autowired
    private GroupService service;

    @GetMapping("/group")
    public CommonResult listGroup(@RequestParam(value = "group_name", required = false) String groupName,
                                  UsernamePasswordAuthenticationToken authentication,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                  @RequestParam(value = "current", defaultValue = "1") int current,
                                  @RequestParam(value = "sorter", defaultValue = "create_time") String sorter,
                                  @RequestParam(value = "order", defaultValue = "DESC") String order) {
        if (authentication == null) {
            return CommonResult.failed("No login.");
        }
        JwtUser principal = (JwtUser) authentication.getPrincipal();
        if (principal == null) {
            return CommonResult.failed("No login.");
        }
        String username = principal.getUsername();
        return service.listGroup(groupName, username, pageSize, current, sorter, order);
    }

    @PostMapping("group")
    public CommonResult addGroup(UsernamePasswordAuthenticationToken authentication, @RequestBody EmergencyGroup emergencyGroup) {
        return service.addGroup((JwtUser) authentication.getPrincipal(), emergencyGroup);
    }

    @DeleteMapping("group")
    public CommonResult deleteGroup(@RequestParam(value = "group_id[]") int[] groupId) {
        int count;
        count = service.deleteGroup(groupId);
        if (count == groupId.length) {
            return CommonResult.success();
        } else if (count == -1) {
            return CommonResult.failed(FailedInfo.GROUP_BE_USED);
        } else {
            return CommonResult.failed(FailedInfo.DELETE_GROUP_FAIL);
        }
    }

    @GetMapping("/group/search")
    public CommonResult search(UsernamePasswordAuthenticationToken authentication,
                               @RequestParam(value = "value", required = false) String groupName) {
        return service.searchGroup(((JwtUser) authentication.getPrincipal()).getUserEntity(), groupName);
    }
}

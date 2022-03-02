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
                                  @RequestParam(value = "create_user", required = false) String createUser,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                  @RequestParam(value = "current", defaultValue = "1") int current,
                                  @RequestParam(value = "sorter", defaultValue = "create_time") String sorter,
                                  @RequestParam(value = "order", defaultValue = "DESC") String order) {
        return service.listGroup(groupName, createUser, pageSize, current, sorter, order);
    }

    @PostMapping("group")
    public CommonResult addGroup(HttpServletRequest request, @RequestBody EmergencyGroup emergencyGroup) {
        return service.addGroup(request, emergencyGroup);
    }

    @DeleteMapping("group")
    public CommonResult deleteGroup(@RequestParam(value = "group_id[]") int[] groupId) {
        int count;
        try {
            count = service.deleteGroup(groupId);
        } catch (SQLException e) {
            return CommonResult.failed(FailedInfo.GROUP_BE_USED);
        }
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
        return service.searchGroup(((JwtUser) authentication.getPrincipal()).getUserEntity(),groupName);
    }
}

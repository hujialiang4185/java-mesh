package com.huawei.user.service;

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.entity.EmergencyGroup;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface GroupService {

    CommonResult listGroup(String groupName, String createUser, int pageSize, int current, String sorter, String order);

    CommonResult addGroup(HttpServletRequest request, EmergencyGroup emergencyGroup);

    int deleteGroup(int[] groupId) throws MySQLIntegrityConstraintViolationException;

    CommonResult searchGroup(HttpServletRequest request, String groupName);
}

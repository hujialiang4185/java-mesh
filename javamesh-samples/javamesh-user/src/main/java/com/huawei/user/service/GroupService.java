package com.huawei.user.service;

import com.huawei.user.common.api.CommonResult;
import com.huawei.user.entity.EmergencyGroup;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.entity.UserEntity;

public interface GroupService {

    CommonResult listGroup(String groupName, String createUser, int pageSize, int current, String sorter, String order);

    CommonResult addGroup(JwtUser jwtUser, EmergencyGroup emergencyGroup);

    int deleteGroup(int[] groupId);

    CommonResult searchGroup(UserEntity user, String groupName);
}

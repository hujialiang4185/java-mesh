package com.huawei.emergency.service;

import com.huawei.emergency.entity.UserEntity;

import java.util.HashMap;
import java.util.Map;

public interface UserAdminCache {
    Map<String, UserEntity> userMap = new HashMap<>();
}

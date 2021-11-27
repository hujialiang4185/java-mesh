package com.huawei.emergency.mapper;

import com.huawei.emergency.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<String> getAuthByRole(String role);

    User selectUserByName(String username);
}

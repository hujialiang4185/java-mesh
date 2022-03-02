package com.huawei.user.mapper;

import com.huawei.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserMapper {
    List<String> getAuthByRole(String userName);

    UserEntity selectUserByName(String userName);

    int countByName(String userName);

    int insertUser(UserEntity entity);

    int insertUserToEmergency(UserEntity entity);

    List<UserEntity> listUser(UserEntity user);

    int updateEnableByName(String[] usernames, String enable, Timestamp timestamp);

    int updatePwdByName(String userName, String password,Timestamp timestamp);

    int updateUser(UserEntity user);

    String getGroupByUser(String userName);

    List<String> approverSearch(String groupName);

    List<String> adminApproverSearch(String groupName);

    int updateEmergencyUser(UserEntity user);
}

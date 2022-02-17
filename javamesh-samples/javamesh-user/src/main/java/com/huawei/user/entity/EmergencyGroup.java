package com.huawei.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class EmergencyGroup {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emergency_group.group_id
     *
     * @mbg.generated
     */
    @JsonProperty("group_id")
    private Integer groupId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emergency_group.group_name
     *
     * @mbg.generated
     */
    @JsonProperty("group_name")
    private String groupName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emergency_group.create_time
     *
     * @mbg.generated
     */
    @JsonProperty("created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column emergency_group.create_user
     *
     * @mbg.generated
     */
    @JsonProperty("created_by")
    private String createUser;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emergency_group.group_id
     *
     * @return the value of emergency_group.group_id
     *
     * @mbg.generated
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emergency_group.group_id
     *
     * @param groupId the value for emergency_group.group_id
     *
     * @mbg.generated
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emergency_group.group_name
     *
     * @return the value of emergency_group.group_name
     *
     * @mbg.generated
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emergency_group.group_name
     *
     * @param groupName the value for emergency_group.group_name
     *
     * @mbg.generated
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emergency_group.create_time
     *
     * @return the value of emergency_group.create_time
     *
     * @mbg.generated
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emergency_group.create_time
     *
     * @param createTime the value for emergency_group.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column emergency_group.create_user
     *
     * @return the value of emergency_group.create_user
     *
     * @mbg.generated
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column emergency_group.create_user
     *
     * @param createUser the value for emergency_group.create_user
     *
     * @mbg.generated
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }
}
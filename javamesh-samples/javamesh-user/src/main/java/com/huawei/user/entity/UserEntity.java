package com.huawei.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private static final String ROLE_ADMIN = "管理员";

    private static final String ROLE_OPERATOR = "操作员";

    private static final String ROLE_APPROVER = "审核员";

    private static final String HEALTHY = "正常";

    private static final String EXPIRED = "失效";

    @JsonProperty("username")
    private String userName;

    @JsonProperty("nickname")
    private String nickName;

    private String password;

    private String role;

    private List<String> auth;

    @JsonProperty("status")
    private String enabled;

    @JsonProperty("group_name")
    private String groupName;

    private Timestamp createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("update_time")
    private Timestamp updateTime;

    /**
     * 通过中文角色名设置role
     *
     * @param role 角色字符串
     */
    public void setRoleByChinese(String role) {
        if (role == null) {
            this.setRole("");
            return;
        }
        switch (role) {
            case ROLE_OPERATOR:
                this.setRole("OPERATOR");
                break;
            case ROLE_APPROVER:
                this.setRole("APPROVER");
                break;
            case ROLE_ADMIN:
                this.setRole("ADMIN");
                break;
            default:
                this.setRole("");
        }
    }

    /**
     * 通过中文角色名设置status
     *
     * @param status 角色字符串
     */
    public void setEnableByChinese(String status) {
        if (StringUtils.isNotBlank(status)) {
            switch (status) {
                case HEALTHY:
                    this.setEnabled("T");
                    break;
                case EXPIRED:
                    this.setEnabled("F");
            }
        }
    }
}

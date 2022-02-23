package com.huawei.emergency.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long userId;

    private String nickName;

    private String userName;

    private String passWord;

    private String role;

    private List<String> auth;

    private String group;

    public User(String userName, String nickName, String role, List<String> auth,String group) {
        this.userName = userName;
        this.nickName = nickName;
        this.role = role;
        this.auth = auth;
        this.group=group;
    }
}

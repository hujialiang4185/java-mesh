package com.huawei.emergency.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUser implements UserDetails {
    private UserEntity userEntity;

    private List<String> authList;

    public JwtUser(UserEntity userEntity, List<String> authList) {
        this.userEntity = userEntity;
        this.authList = authList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authList.stream().map(auth -> new SimpleGrantedAuthority(auth)).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassWord();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserName();
    }

    public String getGroupName() {
        return userEntity.getGroup();
    }

    public UserEntity getUserEntity(){
        return userEntity;
    }

    public List<String> getAuthList(){
        return authList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userEntity.getEnabled().equals("T");
    }
}

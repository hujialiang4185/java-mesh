package com.huawei.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.huawei.user.common.api.CommonResult;
import com.huawei.user.common.constant.FailedInfo;
import com.huawei.user.common.util.EscapeUtil;
import com.huawei.user.entity.EmergencyGroup;
import com.huawei.user.entity.EmergencyGroupExample;
import com.huawei.user.entity.JwtUser;
import com.huawei.user.entity.UserEntity;
import com.huawei.user.mapper.EmergencyGroupMapper;
import com.huawei.user.service.GroupService;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private EmergencyGroupMapper mapper;

    @Override
    public CommonResult listGroup(String groupName, String createUser, int pageSize, int current, String sorter, String order) {
        EmergencyGroup emergencyGroup = new EmergencyGroup();
        emergencyGroup.setGroupName(EscapeUtil.escapeChar(groupName));
        emergencyGroup.setCreateUser(EscapeUtil.escapeChar(createUser));
        Page<EmergencyGroup> pageInfo = PageHelper.startPage(current, pageSize, sorter + System.lineSeparator() + order).doSelectPage(() -> {
            mapper.listGroup(emergencyGroup);
        });
        return CommonResult.success(pageInfo.getResult(), (int) pageInfo.getTotal());
    }

    @Override
    public CommonResult addGroup(JwtUser jwtUser, EmergencyGroup emergencyGroup) {
        EmergencyGroupExample example = new EmergencyGroupExample();
        example.createCriteria().andGroupNameEqualTo(emergencyGroup.getGroupName());
        long count = mapper.countByExample(example);
        if (count > 0) {
            return CommonResult.failed(FailedInfo.GROUP_NAME_EXISTS);
        }
        emergencyGroup.setCreateUser(jwtUser.getUsername());
        emergencyGroup.setCreateTime(getTimestamp());
        int insert = mapper.insert(emergencyGroup);
        if (insert > 0) {
            return CommonResult.success();
        } else {
            return CommonResult.failed(FailedInfo.GROUP_ADD_FAIL);
        }
    }

    @Override
    public int deleteGroup(int[] groupIds) {
        int count = 0;
        for (int groupId : groupIds) {
            try {
                count += mapper.deleteByPrimaryKey(groupId);
            } catch (Exception e) {
                return -1;
            }
        }
        return count;
    }

    @Override
    public CommonResult searchGroup(UserEntity user, String groupName) {
        String userName = user.getUserName();
        if (!userName.equals("admin")) {
            String[] result = new String[1];
            result[0]=user.getGroupName();
            return CommonResult.success(result);
        }
        List<String> groups = mapper.searchGroup(EscapeUtil.escapeChar(groupName));
        return CommonResult.success(groups);
    }

    private Timestamp getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = sdf.format(new Date());
        return Timestamp.valueOf(nowDate);
    }
}

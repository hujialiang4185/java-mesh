package com.huawei.emergency.service;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.ScriptManageDto;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.emergency.entity.UserEntity;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.script.exec.log.LogResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface EmergencyScriptService {
    CommonResult<List<EmergencyScript>> listScript(JwtUser jwtUser, String scriptName, String scriptUser, int pageSize,
        int current, String sorter, String order, String status);

    int deleteScripts(int[] scriptIds);

    void downloadScript(int scriptId, HttpServletResponse response);

    int uploadScript(UserEntity user, EmergencyScript script, MultipartFile file);

    EmergencyScript selectScript(int scriptId);

    int insertScript(UserEntity user, EmergencyScript script);

    int updateScript(EmergencyScript script);

    List<String> searchScript(JwtUser jwtUser, String scriptName, String status, String scriptType);

    EmergencyScript getScriptByName(String scriptName);

    String submitReview(JwtUser jwtUser, EmergencyScript script);

    int approve(String userName, Map<String, Object> map);

    CommonResult debugScript(int scriptId);

    CommonResult debugScriptBeforeSave(String content, String serverName);

    CommonResult debugScriptStop(Integer debugId);

    LogResponse debugLog(int detailId, int lineIndex);

    /**
     * 创建Gui脚本
     *
     * @param script 脚本信息
     * @return
     */
    CommonResult createGuiScript(UserEntity user, EmergencyScript script);

    /**
     * 更新GUI脚本
     *
     * @param treeResponse
     * @return
     */
    CommonResult updateGuiScript(String userName, TreeResponse treeResponse);

    /**
     * 查询编排脚本
     *
     * @param scriptId 脚本Id
     * @return
     */
    CommonResult queryGuiScript(int scriptId);

    /**
     * 创建IDE脚本
     *
     * @param scriptManageDto 脚本信息
     * @return
     */
    CommonResult createIdeScript(UserEntity user, ScriptManageDto scriptManageDto);

    /**
     * 修改IDE脚本
     *
     * @param scriptManageDto 脚本信息
     * @return
     */
    CommonResult updateIdeScript(ScriptManageDto scriptManageDto);

    /**
     * 获取脚本路径
     *
     * @param script {@link EmergencyScript} 脚本信息
     * @return 脚本路径
     */
    String grinderPath(EmergencyScript script);
}

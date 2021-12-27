package com.huawei.emergency.service;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.emergency.layout.TreeResponse;
import com.huawei.script.exec.log.LogResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface EmergencyScriptService {
    CommonResult<List<EmergencyScript>> listScript(HttpServletRequest request, String scriptName, String scriptUser, int pageSize, int current, String sorter, String order,String status);

    int deleteScripts(int[] scriptIds);

    void downloadScript(int scriptId, HttpServletResponse response);

    int uploadScript(HttpServletRequest request,EmergencyScript script,MultipartFile file);

    EmergencyScript selectScript(int scriptId);

    int insertScript(HttpServletRequest request, EmergencyScript script);

    int updateScript(HttpServletRequest request, EmergencyScript script);

    List<String> searchScript(HttpServletRequest request, String scriptName,String status);

    EmergencyScript getScriptByName(String scriptName);

    String submitReview(HttpServletRequest request,EmergencyScript script);

    int approve(Map<String, Object> map);

    CommonResult debugScript(int scriptId);

    LogResponse debugLog(int detailId, int lineIndex);

    /**
     * 创建编排脚本
     *
     * @param script 脚本信息
     * @return
     */
    CommonResult createOrchestrate(EmergencyScript script);

    /**
     * 更新编排脚本
     *
     * @param treeResponse
     * @return
     */
    CommonResult updateOrchestrate(HttpServletRequest request,TreeResponse treeResponse);

    /**
     * 查询编排脚本
     *
     * @param scriptId 脚本Id
     * @return
     */
    CommonResult queryOrchestrate(int scriptId);

}

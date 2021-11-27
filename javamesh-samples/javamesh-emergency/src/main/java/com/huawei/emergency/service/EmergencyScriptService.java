package com.huawei.emergency.service;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.dto.ScriptInfoDto;
import com.huawei.emergency.dto.SearchScriptDto;
import com.huawei.emergency.entity.EmergencyScript;
import com.huawei.script.exec.log.LogRespone;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface EmergencyScriptService {
    CommonResult<List<EmergencyScript>> listScript(String scriptName, String scriptUser, int pageSize, int current, String sorter, String order);

    int deleteScripts(int[] scriptIds);

    void downloadScript(int scriptId, HttpServletResponse response);

    int uploadScript(String userName,EmergencyScript script,MultipartFile file);

    EmergencyScript selectScript(int scriptId);

    int insertScript(String userName, EmergencyScript script);

    int updateScript(EmergencyScript script);

    List<String> searchScript(String scriptName);

    EmergencyScript getScriptByName(String scriptName);

    String submitReview(EmergencyScript script);

    int approve(Map<String, Object> map);

    CommonResult debugScript(int scriptId);

    LogRespone debugLog(int detailId,int lineIndex);
}

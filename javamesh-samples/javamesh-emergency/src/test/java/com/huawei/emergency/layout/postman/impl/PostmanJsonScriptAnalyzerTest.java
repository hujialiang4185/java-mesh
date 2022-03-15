package com.huawei.emergency.layout.postman.impl;

import com.huawei.emergency.layout.postman.entity.PostmanScript;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequest;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestBody;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestDefine;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestHeader;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestQueryParam;
import com.huawei.emergency.layout.postman.entity.request.PostmanRequestUrl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PostmanJsonScriptAnalyzerTest {
    /**
     * 读取文件转换出来的脚本
     */
    private PostmanScript postmanScript;

    @Before
    public void init() {
        try(InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("postman/postman_5_http.json")) {
            PostmanJsonScriptAnalyzer postmanJsonScriptAnalyzer = new PostmanJsonScriptAnalyzer();
            postmanScript = postmanJsonScriptAnalyzer.processPostmanScript(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void check_size_after_parse_postman_json_to_entity() {
        Assert.assertEquals(5, postmanScript.getPostmanRequests().size());
    }

    @Test
    public void check_request_method_after_parse_postman_json_to_entity() {
        PostmanRequest postmanRequest = null;
        for (PostmanRequestDefine define : postmanScript.getPostmanRequests()) {
            if ("75 Neutron".equals(define.getName())) {
                postmanRequest = define.getPostmanRequest();
                break;
            }
        }
        Assert.assertNotNull(postmanRequest);
        Assert.assertEquals("PUT", postmanRequest.getMethod());
    }

    @Test
    public void check_request_header_after_parse_postman_json_to_entity() {
        PostmanRequest postmanRequest = null;
        for (PostmanRequestDefine define : postmanScript.getPostmanRequests()) {
            if ("75 Neutron".equals(define.getName())) {
                postmanRequest = define.getPostmanRequest();
                break;
            }
        }
        Assert.assertNotNull(postmanRequest);
        List<PostmanRequestHeader> requestHeaders = postmanRequest.getHeader();
        PostmanRequestHeader postmanRequestHeader = null;
        for (PostmanRequestHeader requestHeader : requestHeaders) {
            if (requestHeader.getKey().equals("Content-Type")) {
                postmanRequestHeader = requestHeader;
            }
        }
        Assert.assertNotNull(postmanRequestHeader);
        Assert.assertEquals("application/json", postmanRequestHeader.getValue());
    }

    @Test
    public void check_request_body_after_parse_postman_json_to_entity() {
        PostmanRequest postmanRequest = null;
        for (PostmanRequestDefine define : postmanScript.getPostmanRequests()) {
            if ("75 Neutron".equals(define.getName())) {
                postmanRequest = define.getPostmanRequest();
                break;
            }
        }
        Assert.assertNotNull(postmanRequest);
        PostmanRequestBody requestBody = postmanRequest.getBody();
        Assert.assertEquals("raw", requestBody.getMode());
        Assert.assertEquals("{\"size\":10}", requestBody.getRaw());
    }

    @Test
    public void check_request_url_after_parse_postman_json_to_entity() {
        PostmanRequest postmanRequest = null;
        for (PostmanRequestDefine define : postmanScript.getPostmanRequests()) {
            if ("75 Neutron".equals(define.getName())) {
                postmanRequest = define.getPostmanRequest();
                break;
            }
        }
        Assert.assertNotNull(postmanRequest);
        PostmanRequestUrl requestUrl = postmanRequest.getUrl();
        Assert.assertEquals("https", requestUrl.getProtocol());
        Assert.assertEquals("9696", requestUrl.getPort());
        Assert.assertEquals("100.114.237.75", requestUrl.getWholeHost());
        Assert.assertEquals("/v2.0/ext/eip-bandwidths/04189375-fadc-406e-b880-9668cd196c84/tags/f1bd5043-965e-47f3-b3c2-e70d89f0eff9=", requestUrl.getWholePath());
    }

    @Test
    public void check_query_param_after_parse_postman_json_to_entity() {
        PostmanRequest postmanRequest = null;
        for (PostmanRequestDefine define : postmanScript.getPostmanRequests()) {
            if ("75 Neutron".equals(define.getName())) {
                postmanRequest = define.getPostmanRequest();
                break;
            }
        }
        Assert.assertNotNull(postmanRequest);
        PostmanRequestUrl requestUrl = postmanRequest.getUrl();
        List<PostmanRequestQueryParam> queryParams = requestUrl.getQuery();
        Assert.assertEquals(2, queryParams.size());
        PostmanRequestQueryParam pathParam = queryParams.get(0);
        PostmanRequestQueryParam foldParam = queryParams.get(1);
        Assert.assertEquals("path", pathParam.getKey());
        Assert.assertEquals("/", pathParam.getValue());
        Assert.assertEquals("folderName", foldParam.getKey());
        Assert.assertEquals("test33/test44", foldParam.getValue());
    }
}
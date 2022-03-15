package com.huawei.emergency.layout.postman.impl;

import com.huawei.emergency.layout.TestElement;
import com.huawei.emergency.layout.TestPlanTestElement;
import com.huawei.emergency.layout.config.HttpCookieManager;
import com.huawei.emergency.layout.config.HttpHeaderManager;
import com.huawei.emergency.layout.controller.TransactionController;
import com.huawei.emergency.layout.custom.BeforeThreadTestElement;
import com.huawei.emergency.layout.postman.entity.PostmanScript;
import com.huawei.emergency.layout.sampler.HttpSampler;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.huawei.emergency.layout.config.HttpCookieManager.CookieValue;
import static com.huawei.emergency.layout.config.HttpHeaderManager.Header;
import static com.huawei.emergency.layout.config.HttpRequestDefault.Parameters;

public class PostmanScript2GuiConverterTest {
    /**
     * 测试实例
     */
    private final PostmanScript2GuiConverter postmanScript2GuiConverter = new PostmanScript2GuiConverter();

    /**
     * postman json格式脚本转换工具
     */
    private final PostmanJsonScriptAnalyzer postmanJsonScriptAnalyzer = new PostmanJsonScriptAnalyzer();

    /**
     * 读取文件转换出来的脚本
     */
    private PostmanScript postmanScript;

    @Before
    public void init() {
        try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("postman/postman_5_http.json")) {
            postmanScript = postmanJsonScriptAnalyzer.processPostmanScript(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void when_postmanScript_is_null_then_return_empty_list() {
        List<TestPlanTestElement> testPlanTestElements = postmanScript2GuiConverter.convertPostmanScript(null);
        Assert.assertTrue(testPlanTestElements.isEmpty());
    }

    @Test
    public void when_postmanScript_has_null_requests_then_return_empty_list() {
        PostmanScript postmanScript = new PostmanScript();
        List<TestPlanTestElement> testPlanTestElements = postmanScript2GuiConverter.convertPostmanScript(postmanScript);
        Assert.assertTrue(testPlanTestElements.isEmpty());
    }

    @Test
    public void when_postmanScript_has_not_support_requests_then_return_valid_list() {
        List<TestPlanTestElement> testPlanTestElements = postmanScript2GuiConverter.convertPostmanScript(postmanScript);
        Assert.assertEquals(5, testPlanTestElements.size());
    }

    @Test
    public void when_postmanScript_has_support_requests_then_check_detail() {
        List<TestPlanTestElement> testPlanTestElements = postmanScript2GuiConverter.convertPostmanScript(postmanScript);
        TestPlanTestElement testPlanTestElement = null;
        for (TestPlanTestElement planTestElement : testPlanTestElements) {
            if (planTestElement.getTitle().equals("75 Neutron" + PostmanScript2GuiConverter.TEST_PLAN_NAME_SUFFIX)) {
                testPlanTestElement = planTestElement;
                break;
            }
        }
        Assert.assertNotNull(testPlanTestElement);
        List<TestElement> controllerElement = testPlanTestElement.getTestElements();
        BeforeThreadTestElement beforeThreadTestElement = null;
        TransactionController transactionController = null;
        for (TestElement testElement : controllerElement) {
            if (testElement instanceof BeforeThreadTestElement) {
                beforeThreadTestElement = (BeforeThreadTestElement) testElement;
            }
            if (testElement instanceof TransactionController) {
                transactionController = (TransactionController) testElement;
            }
        }
        Assert.assertNotNull(beforeThreadTestElement);
        Assert.assertNotNull(transactionController);
        Assert.assertEquals("75 Neutron" + PostmanScript2GuiConverter.BEFORE_THREAD_PROCESS_NAME_SUFFIX, beforeThreadTestElement.getTitle());
        Assert.assertEquals("75 Neutron" + PostmanScript2GuiConverter.TRANSACTION_CONTROLLER_NAME_SUFFIX, transactionController.getTitle());
        List<TestElement> managerElements = beforeThreadTestElement.getTestElements();
        List<TestElement> requestElements = transactionController.getTestElements();
        HttpHeaderManager httpHeaderManager = null;
        HttpCookieManager httpCookieManager = null;
        for (TestElement managerElement : managerElements) {
            if (managerElement instanceof HttpHeaderManager) {
                httpHeaderManager = (HttpHeaderManager) managerElement;
                continue;
            }
            if (managerElement instanceof HttpCookieManager) {
                httpCookieManager = (HttpCookieManager) managerElement;
            }
        }
        HttpSampler httpSampler = null;
        for (TestElement requestElement : requestElements) {
            if (requestElement instanceof HttpSampler) {
                httpSampler = (HttpSampler) requestElement;
            }
        }
        Assert.assertNotNull(httpHeaderManager);
        assertHttpHeaderManager(httpHeaderManager);
        Assert.assertNotNull(httpCookieManager);
        assertHttpCookieManager(httpCookieManager);
        Assert.assertNotNull(httpSampler);
        assertHttpSampler(httpSampler);
    }

    private void assertHttpSampler(HttpSampler httpSampler) {
        Assert.assertEquals("75 Neutron", httpSampler.getTitle());
        Assert.assertEquals("{\"size\":10}", httpSampler.getBody());
        Assert.assertEquals("100.114.237.75", httpSampler.getDomain());
        Assert.assertEquals("PUT", httpSampler.getMethod());
        Assert.assertEquals("/v2.0/ext/eip-bandwidths/04189375-fadc-406e-b880-9668cd196c84/tags/f1bd5043-965e-47f3-b3c2-e70d89f0eff9=", httpSampler.getPath());
        Assert.assertEquals(9696, httpSampler.getPort());
        Assert.assertEquals("https", httpSampler.getProtocol());
        List<Parameters> parameters = httpSampler.getParameters();
        Assert.assertNotNull(parameters);
        assertQueryParam(parameters);
    }

    private void assertQueryParam(List<Parameters> parameters) {
        Parameters pathParam = null;
        Parameters foldParam = null;
        for (Parameters parameter : parameters) {
            if (parameter.getName().equals("path")) {
                pathParam = parameter;
                continue;
            }
            if (parameter.getName().equals("folderName")) {
                foldParam = parameter;
            }
        }
        Assert.assertNotNull(pathParam);
        Assert.assertNotNull(foldParam);
        Assert.assertEquals("path", pathParam.getName());
        Assert.assertEquals("/", pathParam.getValue());
        Assert.assertEquals("folderName", foldParam.getName());
        Assert.assertEquals("test33/test44", foldParam.getValue());
    }

    private void assertHttpCookieManager(HttpCookieManager httpCookieManager) {
        Assert.assertEquals("75 Neutron" + PostmanScript2GuiConverter.HTTP_COOKIE_MANAGER_SUFFIX, httpCookieManager.getTitle());
        List<CookieValue> cookies = httpCookieManager.getCookies();
        CookieValue sessionId = null;
        for (CookieValue cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID")) {
                sessionId = cookie;
                break;
            }
        }
        Assert.assertNotNull(sessionId);
        Assert.assertEquals("JSESSIONID", sessionId.getName());
        Assert.assertEquals("123456789", sessionId.getValue());
        Assert.assertEquals("100.114.237.75", sessionId.getDomain());
        Assert.assertEquals("/", sessionId.getPath());
    }

    private void assertHttpHeaderManager(HttpHeaderManager httpHeaderManager) {
        Assert.assertEquals("75 Neutron" + PostmanScript2GuiConverter.HTTP_HEADER_MANAGER_SUFFIX, httpHeaderManager.getTitle());
        List<Header> headers = httpHeaderManager.getHeaders();
        Header contentType = null;
        for (Header header : headers) {
            if (header.getName().equals("Content-Type")) {
                contentType = header;
                break;
            }
        }
        Assert.assertNotNull(contentType);
        Assert.assertEquals("Content-Type", contentType.getName());
        Assert.assertEquals("application/json", contentType.getValue());
    }
}
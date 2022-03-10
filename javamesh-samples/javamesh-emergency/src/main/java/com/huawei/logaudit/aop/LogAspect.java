package com.huawei.logaudit.aop;

import com.huawei.common.api.CommonResult;
import com.huawei.emergency.entity.JwtUser;
import com.huawei.logaudit.entity.LogAuditEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aop切面处理类
 *
 * @author h30009881
 * @since 2022-03-10
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    /**
     * 保存请求的私有对象
     */
    private static final ThreadLocal<LogAuditEntity> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * ipv6表示的自身ip
     */
    private static final String LOCAL_HOT_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * ipv4表示的自身ip
     */
    private static final String LOCAL_HOT_IPV4 = "127.0.0.1";

    /**
     * 未识别的ip
     */
    private static final String UNRECOGNIZED_IP = "unknown";

    /**
     * 用户请求经过前端转发后，真实的ip是ip字符串的第一个，中间 “，”隔开
     */
    private static final int REAL_IP_INDEX = 0;

    /**
     * 响应成功的字符串
     */
    private static final String SUCCESS_STRING = "success";
    /**
     * 响应失败的字符串
     */
    private static final String FAILED_STRING = "failed";

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.huawei.logaudit.aop.WebOperationLog)")
    public void pointCut() {
    }

    /**
     * 注销后才获取信息的话session被注销的话就无法获取到用户
     */
    @Before(value = "pointCut()")
    public void beforePoint() {
        LogAuditEntity logAuditEntity = new LogAuditEntity();

        // 接收到请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logAuditEntity.setIpAddress(getIpFromRequest(request));
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        logAuditEntity.setOperationPeople(((JwtUser) authentication.getPrincipal()).getUsername());
        THREAD_LOCAL.set(logAuditEntity);
    }

    /**
     * 切点执行完成
     *
     * @param joinPoint 切点对象
     * @param response  接口响应结果
     */
    @AfterReturning(value = "pointCut()", returning = "response")
    public void afterReturningPoint(JoinPoint joinPoint, Object response) {
        LogAuditEntity logAuditEntity = new LogAuditEntity();
        logAuditEntity.setOperationDate(getTimestamp());

        // 获取响应的结果
        CommonResult<Object> result = (CommonResult<Object>) response;

        // result == null是mo登录的情况我们的接口没有任何返回，所以响应为null。
        logAuditEntity.setOperationResults((result == null || result.isSuccess())
                ? SUCCESS_STRING : FAILED_STRING);

        // 接收到请求
        logAuditEntity.setIpAddress(THREAD_LOCAL.get().getIpAddress());
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        logAuditEntity.setOperationPeople(THREAD_LOCAL.get().getOperationPeople() == null
                ? ((JwtUser) authentication.getPrincipal()).getUsername() : THREAD_LOCAL.get().getOperationPeople());

        // 打印请求内容
        try {
            WebOperationLog declaredAnnotation = getDeclaredAnnotation(joinPoint);
            logAuditEntity.setOperationDetails(declaredAnnotation.operationDetails());
            logAuditEntity.setOperationType(declaredAnnotation.operationType().getTypeString());
            logAuditEntity.setResourceType(declaredAnnotation.resourceType());
            logAuditEntity.setLevel(declaredAnnotation.operationType().getTypeInt());
        } catch (NoSuchMethodException e) {
            log.error("can't find WebOperationLog class");

            // 反射的时候未匹配到自定义的注解类
            return;
        }
        THREAD_LOCAL.remove();
    }

    /**
     * 获取方法中声明的注解
     *
     * @param joinPoint 切点对象
     * @return WebOperationLog
     * @throws NoSuchMethodException
     */
    private WebOperationLog getDeclaredAnnotation(JoinPoint joinPoint) throws NoSuchMethodException {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();

        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // 拿到方法对应的参数类型
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();

        // 根据类、方法、参数类型（重载）获取到方法的具体信息
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);

        // 拿到方法定义的注解信息
        WebOperationLog annotation = objMethod.getDeclaredAnnotation(WebOperationLog.class);

        // 返回
        return annotation;
    }

    /**
     * 根据request请求对象返回用户实际的请求ip
     *
     * @param request request对象
     * @return ip
     */
    public String getIpFromRequest(HttpServletRequest request) {
        List<String> ipHeadList = Stream.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP", "X-Real-IP").collect(Collectors.toList());
        for (String ipHead : ipHeadList) {
            if (checkIp(request.getHeader(ipHead))) {
                return request.getHeader(ipHead).split(",")[REAL_IP_INDEX];
            }
        }
        return LOCAL_HOT_IPV6.equals(request.getRemoteAddr()) ? LOCAL_HOT_IPV4 : request.getRemoteAddr();
    }

    /**
     * 检查ip是否存在
     *
     * @param ip ip
     * @return 判断结果
     */
    private boolean checkIp(String ip) {
        return !(ip == null || 0 == ip.length() || UNRECOGNIZED_IP.equalsIgnoreCase(ip));
    }

    /**
     * 获取当前系统时间
     *
     * @return 当前系统时间
     */
    private Timestamp getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = sdf.format(new Date());
        return Timestamp.valueOf(nowDate);
    }
}

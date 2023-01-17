package com.dfssi.dataplatform.aop;

import com.dfssi.dataplatform.annotation.LogAudit;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 接口调用日志审计
 * Created by yanghs on 2018/5/22.
 */
@Aspect
@Component
public class WebLogAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(com.dfssi.dataplatform.annotation.LogAudit)")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {

        startTime.set(System.currentTimeMillis());

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String methodAnnotation = getMethodAnnotation(joinPoint);
        if(methodAnnotation != null){
            logger.info(String.format("调用接口： %s。", methodAnnotation));
        }

        // 记录下请求内容
        logger.info("REQUEST URL : " + request.getRequestURL().toString());
        logger.debug("REQUEST HTTP_METHOD : " + request.getMethod());
        logger.debug("REQUEST IP : " + request.getRemoteAddr());
        Signature signature = joinPoint.getSignature();
        logger.debug("REQUEST CLASS_METHOD : " + signature.getDeclaringTypeName() + "." + signature.getName());
        logger.info("REQUEST ARGS : " + Arrays.toString(joinPoint.getArgs()));

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容 以及耗时
        logger.debug("RESPONSE RESULT: " + ret);
        logger.info(String.format("SPEND TIME : %ss" , (System.currentTimeMillis() - startTime.get()) / 1000.0  ));
    }

    private String getMethodAnnotation(JoinPoint joinPoint){

        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        // 得到方法的参数的类型
        Class[] argClass = ((MethodSignature) signature).getParameterTypes();
        String annotationValue = null;
        try {
            // 获得当前访问的class
            Class<?> className = joinPoint.getTarget().getClass();

            // 得到访问的方法对象
            Method method = className.getMethod(methodName, argClass);

            // 判断是否存在@LogAudit注解
            if (method.isAnnotationPresent(LogAudit.class)) {
                LogAudit annotation = method.getAnnotation(LogAudit.class);
                // 取出注解中的方法注释
                annotationValue = annotation.value();
            }

            if(annotationValue.length() < 1 && method.isAnnotationPresent(ApiOperation.class)){
                ApiOperation annotation = method.getAnnotation(ApiOperation.class);
                // 取出注解中的方法注释
                annotationValue = annotation.value();
            }
        } catch (Exception e) {
            logger.error(null, e);
            e.printStackTrace();
        }
        return annotationValue;
    }

}

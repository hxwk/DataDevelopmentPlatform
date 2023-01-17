package com.dfssi.dataplatform.cloud.common.annotation;

import com.dfssi.dataplatform.cloud.common.configuration.GlobalCorsImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 全局跨域请求
 * Created by yanghs on 2018/10/29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GlobalCorsImportSelector.class)
public @interface EnableGlobalCors {
}

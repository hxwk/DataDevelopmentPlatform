package com.dfssi.dataplatform.cloud.common.annotation;

import com.dfssi.dataplatform.cloud.common.configuration.LimitInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by yanghs on 2018/10/31.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LimitInterceptor.class)
public @interface EnableLimit {

}

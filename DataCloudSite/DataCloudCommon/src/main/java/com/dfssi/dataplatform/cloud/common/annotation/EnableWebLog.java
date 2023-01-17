package com.dfssi.dataplatform.cloud.common.annotation;

/**
 * Created by yanghs on 2018/10/29.
 */

import com.dfssi.dataplatform.cloud.common.configuration.WebLogImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WebLogImportSelector.class)
public @interface EnableWebLog {

}

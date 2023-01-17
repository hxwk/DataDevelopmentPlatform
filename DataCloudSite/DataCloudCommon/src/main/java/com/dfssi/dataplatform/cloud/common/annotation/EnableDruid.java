package com.dfssi.dataplatform.cloud.common.annotation;

import com.dfssi.dataplatform.cloud.common.configuration.DruidImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * druid数据源管理
 * Created by yanghs on 2018/10/29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DruidImportSelector.class)
public @interface EnableDruid {

}

package com.dfssi.dataplatform.external.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 9:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DataSource {

    String value() default GPDATA;

    String GPDATA = "gpDatasource";

}

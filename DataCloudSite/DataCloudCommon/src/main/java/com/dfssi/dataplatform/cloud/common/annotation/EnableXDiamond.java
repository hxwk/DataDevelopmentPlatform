package com.dfssi.dataplatform.cloud.common.annotation;

import com.dfssi.dataplatform.cloud.common.configuration.XDiamondImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 配置中心
 * Created by yanghs on 2018/10/29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(XDiamondImportSelector.class)
public @interface EnableXDiamond {
}

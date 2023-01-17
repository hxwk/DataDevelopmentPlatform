package com.dfssi.dataplatform.cloud.common.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 实例化全局跨域
 * Created by yanghs on 2018/10/29.
 */
public class GlobalCorsImportSelector  implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"com.dfssi.dataplatform.cloud.common.configuration.CorsConfiguration"};
    }
}
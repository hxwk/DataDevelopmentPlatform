package com.dfssi.dataplatform.cloud.common.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 实例化 DruidConfiguration
 * Created by yanghs on 2018/10/29.
 */
public class DruidImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{"com.dfssi.dataplatform.cloud.common.configuration.DruidConfiguration"};
    }
}

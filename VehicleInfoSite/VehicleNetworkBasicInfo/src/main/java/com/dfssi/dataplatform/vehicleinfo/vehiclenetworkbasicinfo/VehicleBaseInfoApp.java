/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo;

import com.dfssi.dataplatform.cloud.common.annotation.EnableGlobalCors;
import com.dfssi.dataplatform.cloud.common.annotation.EnableWebLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 车联网加车模块
 * @author yanghs
 * @since 2018-4-2 10:45:57
 */
@SpringBootApplication
@EnableSwagger2
@EnableGlobalCors
@EnableWebLog
@EnableHystrix
public class VehicleBaseInfoApp{

    public static void main(String[] args) {
        SpringApplication.run(VehicleBaseInfoApp.class,args);
    }


}
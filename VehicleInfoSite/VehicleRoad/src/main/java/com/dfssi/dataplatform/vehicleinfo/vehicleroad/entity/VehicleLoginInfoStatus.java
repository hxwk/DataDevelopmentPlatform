package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import lombok.Data;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/25 16:23
 */
@Data
public class VehicleLoginInfoStatus {
    private String vid;
    private int status;
    private long ts = System.currentTimeMillis();
}

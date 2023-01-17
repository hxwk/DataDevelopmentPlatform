package com.dfssi.dataplatform.abs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/10/28 15:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsVehicleStatusEntity {

    private String vid;

    private String status;

    private long lastModifiedTime;

}

package com.dfssi.dataplatform.abs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/10/26 20:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsCheckResultEntity {

    private String vid;

    private long startTime;

    private long stopTime;

    private double distant;

    private int result;

    private int checkCount;

}

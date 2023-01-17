package com.dfssi.dataplatform.abs.entity;

import com.dfssi.dataplatform.abs.check.ABSCheckRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/10/27 10:22
 */
@Data
@AllArgsConstructor
public class AbsCheckRecordEntity {

    private String vid;

    private double speed;

    private double lon;

    private double lat;

    private long collTime;

    private double lessSpeed;

    private int checkCount;

    public AbsCheckRecordEntity(ABSCheckRecord record, int checkCount) {
        this.vid = record.getVid();
        this.speed = record.getSpeed();
        this.lon = record.getLon();
        this.lat = record.getLat();
        this.collTime = record.getCollTime();
        this.lessSpeed = record.getLessSpeed();
        this.checkCount = checkCount;
    }

}

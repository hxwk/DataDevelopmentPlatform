package com.dfssi.dataplatform.ide.service.service;

import com.dfssi.dataplatform.analysis.entity.AnalysisStepTypeEntity;
import com.dfssi.dataplatform.common.service.CommonMenuCtr;
import com.dfssi.dataplatform.metadata.entity.DataResourceConfEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class DataServiceLeftMenuItems extends ArrayList {

    @JsonIgnore
    private CommonMenuCtr dataResourceCtr = null;
    @JsonIgnore
    private CommonMenuCtr shareCtr = null;

    public DataServiceLeftMenuItems() {
        this.init();
    }

    private void init() {
        this.buildDataResourceCtr();
        this.buildShareCtr();
    }

    private void buildDataResourceCtr() {
        dataResourceCtr = new CommonMenuCtr(1, "数据资源", "数据资源", "./css/zTreeStyle/img/diy/8.png");
        CommonMenuCtr hiveResourceCtr = new CommonMenuCtr(12, "Hive", "Hive", "./css/zTreeStyle/img/diy/8.png");
        dataResourceCtr.addChild(hiveResourceCtr);

        this.add(dataResourceCtr);
    }

    private void buildShareCtr() {
        shareCtr = new CommonMenuCtr(2, "共享", "共享", "./css/zTreeStyle/img/diy/8.png");
        this.add(shareCtr);
    }

    public void addDataResourceItem(DataResourceConfEntity drce) {
        CommonMenuCtr hiveResourceCtr = dataResourceCtr.findByName("Hive");
        if (StringUtils.isBlank(drce.getShowName())) {
            drce.setShowName(drce.getDataresName());
        }
        hiveResourceCtr.addChild(drce);
    }

    public void addShareItem(AnalysisStepTypeEntity aste) {
        this.generateShowName(aste);
        this.shareCtr.addChild(aste);
    }

    private void generateShowName(AnalysisStepTypeEntity aste) {
        if (StringUtils.isBlank(aste.getShowName())) {
            aste.setShowName(aste.getName());
        }
    }
}

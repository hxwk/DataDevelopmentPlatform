package com.dfssi.dataplatform.mapper;

import com.google.common.base.Joiner;
import org.apache.ibatis.jdbc.SQL;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/7/24 15:18
 */
public abstract class AbstractQureySQLProvider {

    public void addCondition(SQL sql,
                             String enterprise,
                             String hatchback,
                             String vin,
                             String beginDay,
                             String endDay,
                             String dayField){

        if(beginDay != null && endDay != null) {
            if (beginDay.equals(endDay)) {
                sql.WHERE(String.format("%s=#{beginDay}", dayField));
            } else {
                sql.WHERE(String.format("%s >= #{beginDay} and %s <= #{endDay}", dayField, dayField));
            }
        }

        if(vin != null){
            String[] vins = vin.split(",");
            String condition = String.format("(vin ='%s')", Joiner.on("' or vin ='").skipNulls().join(vins));
            sql.WHERE(condition);
        }

        if(hatchback != null){
            String[] hatchbacks = hatchback.split(",");
            String condition = String.format("(hatchback ='%s')", Joiner.on("' or hatchback ='").skipNulls().join(hatchbacks));
            sql.WHERE(condition);
        }

        if(enterprise != null){
            String[] enterprises = enterprise.split(",");
            String condition = String.format("(enterprise ='%s')", Joiner.on("' or enterprise ='").skipNulls().join(enterprises));
            sql.WHERE(condition);
        }
    }
}

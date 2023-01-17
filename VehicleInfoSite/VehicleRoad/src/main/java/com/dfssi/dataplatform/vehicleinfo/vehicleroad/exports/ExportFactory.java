package com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.ExportConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.excel.ExcelExportor;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.txt.TxtExportor;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/5 8:48
 */
@Slf4j
public class ExportFactory {

    private ExportFactory(){}



    public static Exportor newExportor(String exportType, ExportConfig config){

        Preconditions.checkNotNull(exportType, "导出类型不能为空.");

        switch (exportType){
            case "excel":
                return new ExcelExportor(config);
            case "txt":
                return new TxtExportor(config);
            default:
                throw new IllegalArgumentException(String.format("不识别的导出类型：%s", exportType));
        }
    }

}

package com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.ExportConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.Exportor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Description:
 *  excel生成器， 单sheet
 * @author LiXiaoCong
 * @version 2018/9/30 10:20
 */
@Slf4j
public class ExcelExportor extends Exportor {

    private String suffix = "xlsx";

    public ExcelExportor(ExportConfig config){
        super(config, "excel");
    }


    @Override
    public void finish(String fileName, boolean clean) throws Exception{

        File file = new File(dir, String.format("%s.%s", fileName, suffix));
        OutputStream out = null;
        try {
            out = new FileOutputStream(file, true);
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, false);

            Sheet sheet = new Sheet(1, 0);
            if(sheetName != null){
                sheet.setSheetName(sheetName);
            }

            writer.write0(rows, sheet);
            writer.finish();

            if(clean){ clean(); }
            log.info(String.format("导出excel文件%s成功。", file.getAbsolutePath()));
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(String.format("关闭excel文件%s输出流失败。", file.getAbsolutePath()), e);
                }
            }
        }
    }


}

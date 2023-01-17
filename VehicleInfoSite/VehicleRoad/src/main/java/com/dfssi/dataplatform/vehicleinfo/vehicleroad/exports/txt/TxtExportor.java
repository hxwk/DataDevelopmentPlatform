package com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.txt;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.ExportConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports.Exportor;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/5 10:36
 */
@Slf4j
public class TxtExportor extends Exportor {

    private String suffix = "txt";

    public TxtExportor(ExportConfig config){
        super(config, "txt");
    }

    @Override
    public void finish(String fileName, boolean clean) throws Exception{

        File file = new File(dir, String.format("%s.%s", fileName, suffix));
        OutputStream out = null;
        try {

            List<String> collect = rows.stream().map(row -> Joiner.on("\t").join(row)).collect(Collectors.toList());

            out = new FileOutputStream(file, true);
            IOUtils.writeLines(collect, "\n", out, "utf-8");

            if(clean){ clean(); }
            log.info(String.format("导出txt文件%s成功。", file.getAbsolutePath()));
        }finally {
            if(out != null){
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    log.error(String.format("关闭excel文件%s输出流失败。", file.getAbsolutePath()), e);
                }
            }
        }
    }


}


package com.dfssi.dataplatform.vehicleinfo.vehicleroad.exports;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.ExportConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.CompressUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/5 8:47
 */
@Slf4j
public abstract class Exportor {

    protected ExportConfig config;
    protected String baseDir;
    protected String exportType;

    protected String sheetName;
    protected Set<String> columns;

    protected String dayDir;
    protected String timeDir;
    protected String dir;
    protected List<List<String>> rows;

    public Exportor(ExportConfig config, String exportType){
        this.config = config;
        this.baseDir = config.getTmpDir();
        Preconditions.checkNotNull(baseDir, "文件导出零时目录不能为空");
        if(baseDir.endsWith("/")){
            baseDir = baseDir.substring(0, baseDir.length() - 1);
        }
        checkOrCreateDir(baseDir, exportType);

        this.exportType = exportType;

        this.rows = Lists.newLinkedList();
    }

    public abstract void finish(String fileName, boolean clean) throws Exception;

    public String dir2Ftp(FTPClient ftpClient) throws Exception {
        String zipFile = dir2Zip();
        String s = upload2Ftp(zipFile, ftpClient);

        //删除文件
        delete(zipFile);
        delete(dir);

        return s;
    }

    protected String dir2Zip() throws Exception {
        String zipFile = String.format("%s/%s/%s/%s-%s.zip", baseDir, exportType, dayDir, dayDir, timeDir);
        CompressUtil.zipQuickly(zipFile, dir);
        log.info(String.format("添加到压缩文件：%s 成功。", zipFile));
        return zipFile;
    }

    protected String upload2Ftp(String zipFile, FTPClient ftpClient) throws Exception {

        log.info(String.format("开始上传目录%s的zip压缩文件。", dir));

        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.changeWorkingDirectory(config.getParentDir());

        File f = new File(zipFile);
        String name = f.getName();
        FileInputStream is = new FileInputStream(f);

        String ftpDir = null;
        try {
            if(ftpClient.storeFile(name, is)){
                log.info(String.format("上传目录%s的zip压缩文件成功。", dir));
                ftpDir = String.format("%s/%s", config.getParentDir(), name);
            }else{
                log.error(String.format("上传目录%s的zip压缩文件失败。", dir));
            }
        } catch (Exception e){
            log.error(String.format("上传目录%s的zip压缩文件失败。", dir), e);
        } finally{
            try {
                is.close();
            } catch (IOException e) {
                log.warn(null, e);
            }
        }

        return ftpDir;
    }

    private void delete(String file){
        File df = new File(file);
        FileUtils.deleteQuietly(df);
    }


    public void addRow(List<String> row){
        this.rows.add(row);
    }

    public void addRows(Collection<List<String>> rows){
        this.rows.addAll(rows);
    }

    public void addRowMap(Map<String, Object> rowMap) {
        if(rowMap != null && !rowMap.isEmpty()){
            if(columns == null || columns.isEmpty()){
                setColumns(Sets.newHashSet(rowMap.keySet()));
            }

            List<String> row = columns.stream().map(column -> {
                Object o = rowMap.get(column);
                if(o == null){
                    return "";
                }else{
                    return o.toString();
                }
            }).collect(Collectors.toList());

            addRow(row);
        }
    }

    public void addRowMaps(Collection<Map<String, Object>> rowMaps){
        rowMaps.stream().forEach(this :: addRowMap);
    }

    public int getSize(){
        return rows.size();
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getExportType() {
        return exportType;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public String getDayDir() {
        return dayDir;
    }

    public String getTimeDir() {
        return timeDir;
    }

    public String getDir() {
        return dir;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
       if(rows.isEmpty()){
           addRow(Lists.newArrayList(columns));
       }
    }

    public void clean() {
        rows.clear();
        addRow(Lists.newArrayList(columns));
    }


    private void checkOrCreateDir(String baseDir, String exportType){

        DateTime now = DateTime.now();
        String[] dayTime = now.toString("yyyyMMdd HHmmss-SSS").split(" ");
        dayDir = dayTime[0];
        timeDir = dayTime[1];
        dir = String.format("%s/%s/%s/%s/", baseDir, exportType, dayDir, timeDir);

        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
    }
}


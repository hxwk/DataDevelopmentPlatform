package com.dfssi.hbase.common;

import com.dfssi.common.logging.DynamicLogFactory;
import com.dfssi.hbase.v2.HConfiguration;
import com.dfssi.hbase.v2.HContext;
import com.google.common.collect.Sets;
import org.apache.commons.cli.*;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.RegionLoad;
import org.apache.hadoop.hbase.ServerLoad;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *      手动执行HBase表的 split操作
 *
 *      根据region的大小来确定是否需要切分
 *
 * @author LiXiaoCong
 * @version 2017/2/17 11:08
 */
public class ManualSplit {

    private static Logger logger =
            DynamicLogFactory.getLogger("ManualSplit", "/var/log/hbase/");

    private HConfiguration conf;
    private Admin admin;

    private int maxRegionSize;
    private boolean hasRegionNeedSpilt;
    private Set<String> regionSpliting;

    public ManualSplit(int maxRegionSize) throws Exception {
        this.conf = HContext.get().gethConfiguration();
        this.admin = HContext.get().getAdmin();

        this.maxRegionSize = maxRegionSize * 1024;
        this.hasRegionNeedSpilt = true;
        this.regionSpliting = Sets.newHashSet();
    }

    public void execute() throws InterruptedException {
        logger.info("开始执行 region 的切分检查操作。");
        //第一次 包含需要切分的 region  但是不能保证 切分后的region 是否还需要再次切分
        //因此这个是一个循环检查的过程 直到 没有需要切分的 region为止
        while (hasRegionNeedSpilt) {
            this.hasRegionNeedSpilt = false;
            splitCheck();

            //休眠 5 分钟
            Thread.sleep(1000 * 60 * 5);
        }
        logger.info("执行 region 的切分检查操作完成。");
    }

    private void splitCheck(){
        try {
            ClusterStatus clusterStatus = admin.getClusterStatus();
            Collection<ServerName> servers = clusterStatus.getServers();
            ServerLoad serverLoad;
            for(ServerName serverName : servers){
                serverLoad = clusterStatus.getLoad(serverName);
                splitCheckRegionServer(serverLoad);
            }
        } catch (IOException e) {
            logger.error(null, e);
        }

    }

    private void splitCheckRegionServer(ServerLoad serverLoad){

        Map<byte[], RegionLoad> regionsLoad = serverLoad.getRegionsLoad();
        for(Map.Entry<byte[], RegionLoad> entry : regionsLoad.entrySet()){
            splitCheckRegion(entry.getKey(), entry.getValue());
        }

    }

    private void splitCheckRegion(byte[] regionName, RegionLoad regionLoad){

        String name = new String(regionName);
        try {
            //int memStoreSizeMB = regionLoad.getMemStoreSizeMB();
            //if(memStoreSizeMB > 32) admin.flushRegion(regionName);

            //majorCompactRegion 为异步操作 无法知道什么时候完成 ，可能会与即将执行的 split操作造成冲突
            //int storefiles = regionLoad.getStorefiles();
            //if(storefiles > 2)admin.majorCompactRegion(regionName);

            //当一个region的大小超过 6g 进行region切分
            int storefileSizeMB = regionLoad.getStorefileSizeMB();
            if(storefileSizeMB >= maxRegionSize && !regionSpliting.contains(name)){
                regionSpliting.add(name);
                logger.info(String.format("检查到region : %s 的大小为 %s MB超过了阀值 ： %s MB, 执行切分请求。",
                        name, storefileSizeMB, maxRegionSize));

                hasRegionNeedSpilt = true;
                admin.splitRegion(regionName);

                logger.info(String.format("提交切分到region : %s 请求完成。", name));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {

        CommandLine commandLine = parseArgs(args);
        String maxRegionSize = commandLine.getOptionValue("maxRegionSize", "6");

        ManualSplit compactAndSplit = new ManualSplit(Integer.parseInt(maxRegionSize));
        compactAndSplit.execute();
    }

    private static CommandLine parseArgs(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("help", false, "帮助 打印参数详情");
        options.addOption("maxRegionSize", true, " int 类型, region的切分阀值，默认为6， 单位为 G ");

        CommandLineParser parser = new PosixParser();
        CommandLine lines = parser.parse(options, args);

        if(lines.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.setOptPrefix(HelpFormatter.DEFAULT_LONG_OPT_PREFIX);
            formatter.printHelp("ManualSplit", options);
            System.exit(0);
        }
        return lines;
    }
}

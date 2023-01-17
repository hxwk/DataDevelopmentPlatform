package com.dfssi.common;

import com.dfssi.common.json.Jsons;
import com.dfssi.common.net.Https;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *   webhdfs 获取目录长度等信息的工具类
 * @author LiXiaoCong
 * @version 2018/3/5 9:34
 */
public class WebHDFSUtil {

    private WebHDFSUtil(){}

    //http://devmaster:50070/webhdfs/v1/user/hdfs/hiveexternal/prod?op=GETCONTENTSUMMARY
    public static long getPathLength(String nameNodeHost,
                                     int nameNodeWebhdfsPort,
                                     String path) throws Exception {

        long length = 0L;
        String url = String.format("http://%s:%s/webhdfs/v1%s?op=GETCONTENTSUMMARY",
                nameNodeHost, nameNodeWebhdfsPort, path);

        String jsonRes = Https.get(url);

        Map<String, Map<String, Object>> res = Jsons.toMap(jsonRes);
        Map<String, Object> contentSummary = res.get("ContentSummary");
        if(contentSummary != null){
            length = Long.parseLong(contentSummary.get("length").toString());
        }
        return length;
    }

    public static List<String> listSubPathNames(String nameNodeHost,
                                               int nameNodeWebhdfsPort,
                                               String path) throws Exception{
        String url = String.format("http://%s:%s/webhdfs/v1%s?op=LISTSTATUS",
                nameNodeHost, nameNodeWebhdfsPort, path);
        String jsonRes = Https.get(url);

        List<String> subPathNames = new ArrayList<>();

        Map<String, Map<String, List<Map<String, Object>>>> res
                = Jsons.toMap(jsonRes);
        Map<String, List<Map<String, Object>>> fileStatuses = res.get("FileStatuses");
        if(fileStatuses != null){
            List<Map<String, Object>> fileStatus = fileStatuses.get("FileStatus");
            if(fileStatus != null){
                for(Map<String, Object> map : fileStatus){
                    if("DIRECTORY".equals(map.get("type"))){
                        subPathNames.add(map.get("pathSuffix").toString());
                    }
                }
            }
        }

        return subPathNames;
    }

    /**
     * 仅适合读取小文件
     * @throws Exception
     */
    public static String openfile(String nameNodeHost,
                                int nameNodeWebhdfsPort,
                                String path) throws Exception {

        String url = String.format("http://%s:%s/webhdfs/v1%s?op=OPEN",
                nameNodeHost, nameNodeWebhdfsPort, path);
        return Https.get(url, "utf-8");
    }

    public static void main(String[] args) throws Exception {
        long pathLength = getPathLength("devmaster",
                50070,
                "/user/hdfs/hiveexternal/prod");
        System.err.println(pathLength);

        List<String> pathNames = listSubPathNames("devmaster",
                50070,
                "/user/hdfs/hiveexternal/prod");
        System.err.println(pathNames);

        String str = openfile("devmaster",
                50070,
                "/user/hdfs/config/prod/driverBehavior/init/IntegrateTaskDef.xml");
        System.out.println(str);

    }
}

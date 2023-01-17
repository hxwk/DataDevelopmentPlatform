package com.dfssi.dataplatform.vehicleinfo.vehicleroad.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName DirectionUtils
 * @Description TODO 文件列表转换成JSON
 * @Author chenf
 * @Date 2018/9/30
 * @Versiion 1.0
 **/
public class DirectionUtils {

    public static void main(String[] args) {
        JSONObject Jo = getJsonFromDirection("/data/etc/qcmap_webclient_wifidaemon_file;/data/etc/qcmap_wifidaemon_webclient_file;/data/etc/dvpt_smd_server_file;");
        System.out.println(JSONObject.toJSONString(Jo));
    }

    public static JSONObject getJsonFromDirection(String pathStr) {


        Set<String> set = new HashSet<>();
        HashMap<String, JSONObject> fileList = new HashMap<>();
        HashMap<String, Set> gx = new HashMap<>();
        Set<String> rl = new HashSet();
        int count = 0;
        if (StringUtils.isBlank(pathStr)) {
            return null;
        }
        String[] path = pathStr.split(";");
        for (int j = 0; j < path.length; j++) {
            String filepath = path[j].substring(1,path[j].length());
            String[] dir = filepath.split("\\/");

                for(int i = 0 ;i <dir.length;i++){
                    String uri = "";
                    if(i>0) {
                        for (int k = 0; k < i; k++) {
                            uri =  uri +"/"+ dir[k];
                        }
                        //uri=uri+"/"+dir[i];
                        String uri2 =uri;
                        Set list =gx.get(uri2);
                        uri=uri+"/"+dir[i];
                        if (list == null) {
                            list = new HashSet();
                        }
                        list.add(uri);
                        gx.put(uri2,list);

                    }else{
                        uri = uri+"/"+dir[i];
                    }
                    JSONObject jo1 = fileList.get(uri);
                    if (jo1 == null) {
                        jo1 = new JSONObject();
                        jo1.put("name", dir[i]);
                        jo1.put("filePath",uri);
                    }
                    fileList.put(uri,jo1);

                    if(i==0){
                        rl.add(uri);
                    }

                }

        }

        Set<String> gxkeyset  = gx.keySet();
        for(String str:gxkeyset) {
           Set<String> vs =  gx.get(str);
            if(vs!=null&&vs.size()>0) {
                JSONObject jo = fileList.get(str);
                JSONArray ja = new JSONArray();
                for (String str2 : vs) {
                    ja.add(fileList.get(str2));
                }
                jo.put("child",ja);
            }
        }
        JSONArray rootlist = new JSONArray();
        JSONObject root = new JSONObject();
        root.put("directory", rootlist);
        for (String filepath:rl) {
            rootlist.add(fileList.get(filepath));
        }
        return root;
    }

}


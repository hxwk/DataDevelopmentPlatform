package com.dfssi.dataplatform.datasync.plugin.source.crawler;

import com.dfssi.dataplatform.datasync.plugin.source.common.UtilTools;

/**
 * Created by jian on 2017/11/30.
 */
public class zhuanpinyin {

    public static void main(String[] args) {
        UtilTools zpy = new UtilTools();
       /*zpy.getCodeByCity();
        String url ="http://tianqi.2345.com/";
        String chs = "赣州";
        System.out.println(chs);
        System.out.println(getPinYin(chs));
        System.out.println(url+getPinYin(chs)+"/"+"57993"+".htm");*/

        //zpy.getLinks("http://tianqi.2345.com");
        zpy.getSuffixLinks();
    }
}

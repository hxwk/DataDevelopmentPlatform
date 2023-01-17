package com.dfssi.dataplatform.analysis.es;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.hadoop.cfg.PropertiesSettings;

import java.io.File;

public class Rest {

    private static SimpleRestClient restClient;
    private EsDateIndexCreator dateIndexCreator;

    public SimpleRestClient getRestClient(){

        if(restClient == null) {
            PropertiesSettings propertiesSettings = new PropertiesSettings();
            propertiesSettings.setProperty("es.nodes", "172.16.1.221,172.16.1.222,172.16.1.223");
            propertiesSettings.setProperty("es.clustername", "elk");

            //restClient = new SimpleRestClient(propertiesSettings);
            this.dateIndexCreator = new EsDateIndexCreator(propertiesSettings);
        }
        return restClient;
    }

    public void test() throws Exception {
        SimpleRestClient restClient = getRestClient();
        String mapping = FileUtils.readFileToString(new File("D:/BDMS/demo/terminal_0200.json"));
        //restClient.putMapping("demo", "/demo/_mapping/demo", mapping.getBytes());

        dateIndexCreator.createMapping("20180207_terminal_0200", "terminal_0200");
    }


    public static void main(String[] args) throws Exception {

        Rest rest = new Rest();

        rest.test();

    }

}
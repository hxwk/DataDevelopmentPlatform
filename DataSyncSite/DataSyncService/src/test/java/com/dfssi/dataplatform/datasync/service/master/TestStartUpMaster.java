package com.dfssi.dataplatform.datasync.service.master;

import com.dfssi.dataplatform.datasync.common.utils.StringUtil;
import com.dfssi.dataplatform.datasync.common.utils.PropertiUtil;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.REST_PACKAGE;
import static com.dfssi.dataplatform.datasync.service.rpc.cluster.Constant.REST_SERVICE_BASE_PORT;

/**
 * Created by HSF on 2018/3/7.
 */
public class TestStartUpMaster {
    public static void main(String[] args) {
        try{
            String conf = ClassLoader.getSystemResource("config/master.properties").getFile().toString();
            File configurationFile = new File(conf);
            System.setProperty("conf", configurationFile.getCanonicalPath());
            String serverAddress = PropertiUtil.getStr("server_address");
            System.out.println("serverIP = " + serverAddress);
            URI uri = UriBuilder.fromUri("http://0.0.0.0").port(REST_SERVICE_BASE_PORT).build();
            ResourceConfig rc = new PackagesResourceConfig(REST_PACKAGE);
            rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,true);
            Set<Class<?>> classes = rc.getRootResourceClasses();
            String property = System.getProperty("java.class.path");
            System.out.println("path = " + property);
            System.out.println("StringUtil.class.getClassLoader() = " + StringUtil.class.getClassLoader());
            classes.forEach(aClass -> System.out.println("aClass = " + aClass.getClassLoader()));
            HttpServer server = GrizzlyServerFactory.createHttpServer(uri, rc);
            server.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

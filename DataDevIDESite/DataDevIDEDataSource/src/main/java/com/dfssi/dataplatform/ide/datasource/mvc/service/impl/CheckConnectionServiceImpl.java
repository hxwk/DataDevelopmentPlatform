package com.dfssi.dataplatform.ide.datasource.mvc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.ide.datasource.mvc.entity.DataConnectionTestEntity;
import com.dfssi.dataplatform.ide.datasource.mvc.service.CheckConnectionService;
import com.dfssi.dataplatform.ide.datasource.util.Constants;
import com.dfssi.dataplatform.ide.datasource.util.ResultVo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @date 2018/10/10
 * @description ????????????
 */
@Service(value = "checkConnection")
public class CheckConnectionServiceImpl implements CheckConnectionService {
    private static Logger logger = Logger.getLogger(CheckConnectionServiceImpl.class);
    /**
     *jbdc????????????
     * @param info
     * @param dbType
     * @return
     */
    public Connection getConnection (DataConnectionTestEntity info, String dbType) {
        Connection conn1 = null;
        String url = null;
        try {
            if((Constants.STR_MYSQL).equals(dbType)){
                url = "jdbc:mysql://" + info.getIp() + ":" + info.getPort() + "/" + info.getDatabaseName();
                Class.forName("com.mysql.jdbc.Driver");
            }else if((Constants.STR_ORACLE).equals(dbType)){
                url = "jdbc:oracle:thin:@" + info.getIp() + ":" + info.getPort() + ":" + info.getDatabaseName();
                Class.forName("oracle.jdbc.driver.OracleDriver");
            }else if((Constants.STR_SQLSERVER).equals(dbType)){
                url ="jdbc:sqlserver://"+ info.getIp() + ":" + info.getPort() + ";DatabaseName=" + info.getDatabaseName();
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            }else if((Constants.STR_DBTWO).equals(dbType)){
                //DBURL : "jdbc:db2://x.xxx.xxx.xxx:50000/waadb" ;
                url ="jdbc:db2://"+ info.getIp() + ":" + info.getPort() + "/" + info.getDatabaseName();
                Class.forName("com.ibm.db2.jcc.DB2Driver");
            }else if((Constants.STR_HIVE).equals(dbType)){
                //("jdbc:hive2://xx.xx.xx.xx:21050/default","admin","admin");
                url ="jdbc:hive2://"+ info.getIp() + ":" + info.getPort() + "/" + info.getDatabaseName();
                Class.forName("org.apache.hadoop.hive.jdbc.HiveDriver");
            }else if((Constants.STR_GP).equals(dbType)){
                url ="jdbc:pivotal:greenplum://"+ info.getIp() + ":" + info.getPort() + ";DatabaseName=" + info.getDatabaseName();
                Class.forName("com.pivotal.jdbc.GreenplumDriver");
            }
            //???????????????
            conn1 = DriverManager.getConnection(url, info.getDatabaseUsername(), info.getDatabasePassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn1;
    }

    /**
     * jbdc????????????
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo exceQuery(DataConnectionTestEntity info, String sql, String dbType) throws Exception {
        ResultVo rs = new ResultVo();
        Statement stmt = null;
        Connection conn = null;
        int count = -1;
        try {
            conn = getConnection(info, dbType);
            if(null != conn){
                stmt = conn.createStatement();
                ResultSet re = stmt.executeQuery(sql);
                if(re.next()) {
                    count = re.getInt(1);
                }
            }
            if(-1 != count){
                rs.setErrorNo(0);
                rs.setSuccessMsg("?????????????????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("?????????????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("?????????????????????");
        }finally {
            if(null != conn){
                conn.close();
            }
            if(null != stmt){
                stmt.close();
            }
        }
        return rs;
    }

    /**
     * ????????????????????????
     * @param address
     * @param port
     * @return
     * @throws Exception
     */
    public boolean checkHdfsIsActive(String address,String port) throws Exception{
        //?????????checkHdfsIsActive("172.16.1.210","50070");
        String hdfs_request = "http://"+address+":"+port+"/jmx";
        //???????????????host => "192.168.144.62"  port => 50070
        //??????url: http://192.168.144.145:50070/jmx?qry=Hadoop:service=NameNode,name=NameNodeStatus
        //??????http??????
        String hdfs_result = getWithHttp(hdfs_request, "qry=Hadoop:service=NameNode,name=NameNodeStatus");
        //??????????????????????????????
        JSONObject hdfs_json = JSON.parseObject(hdfs_result.toLowerCase());
        String state = hdfs_json.getJSONArray("beans").getJSONObject(0).getString("state");
        if ("active".equals(state)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * ??????????????????????????????
     * @param HdfsURI
     * @param HdfsActiveNodeUser
     * @param filePath
     * @return
     *
     */
    public ResultVo checkHdfsFilePathIsExist(String HdfsURI, String HdfsActiveNodeUser, String filePath){
        //?????????checkHdfsFilePathIsExist("hdfs://172.16.1.210:8020", "hdfs", "/tmp/logs");
        ResultVo res = new ResultVo();
        Configuration configuration = new Configuration();
        //?????????????????????jar????????????????????????
        //configuration.set("fs.defaultFS", "hdfs://172.16.1.210:9000");
        //configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fileSystem = null;
        try
        {
            // ???????????????NN????????????????????????????????????HDFS??????
            fileSystem = FileSystem.get(new URI(HdfsURI), configuration, HdfsActiveNodeUser);
        }
        catch (Exception e)
        {
            //??????HDFS???????????????API????????????
            e.printStackTrace();
            res.setErrorNo( -1);
            res.setErrorMsg("??????HDFS???????????????API????????????!HdfsURI???" + HdfsURI + ",user:"
                    + HdfsActiveNodeUser);
            return res;
        }
        //????????????HDFS???????????????API????????????
        boolean isExists = false;
        boolean isDirectorys = false;
        boolean isFiles = false;
        Path path = new Path(filePath);
        try {
            isExists = fileSystem.exists(path);
            isDirectorys = fileSystem.isDirectory(path);
            isFiles = fileSystem.isFile(path);
        } catch (IOException e){
            e.printStackTrace();
            res.setErrorNo( -1);
            res.setErrorMsg("??????HDFS????????????????????????????????????????????????????????????!HdfsURI???" + HdfsURI + ",user:"+ HdfsActiveNodeUser+",path:"+path);
            return res;
        } finally {
            try {
                fileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!isExists){
            res.setErrorNo(-1);
            res.setErrorMsg("??????????????????:HDFS???????????????????????????????????????");
            return res;
        }else{
            if(isDirectorys){
                res.setErrorNo(0);
                res.setSuccessMsg("??????????????????:????????????HDFS??????????????????????????????");
                return res;
            }else if(isFiles){
                res.setErrorNo(0);
                res.setSuccessMsg("??????????????????:????????????HDFS??????????????????????????????");
                return res;
            }
        }
        return res;
    }

    /**
     * ?????????URL??????GET???????????????
     * @param url  ???????????????URL
     * @param content
     *            ???????????????????????????????????? name1=value1&name2=value2 ????????????
     * @return URL ????????????????????????????????????
     */
    public String getWithHttp(String url, String content) throws Exception
    {
        String result = "";
        BufferedReader in = null;
        try
        {
            String urlNameString = url + "?" + content;
            URL realUrl = new URL(urlNameString);
            // ?????????URL???????????????
            URLConnection connection = realUrl.openConnection();
            // ???????????????????????????
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ?????????????????????
            connection.connect();
            // ?????? BufferedReader??????????????????URL?????????
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        finally {
            try {
                if ( in != null ) { in.close(); }
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     *??????kafka??????????????????topic???????????????????????????topic
     */
    public ResultVo getTopics(DataConnectionTestEntity info){
        Properties props = new Properties();
        props.put("group.id", info.getKafkaGroupId());//?????????
        //props.put("enable.auto.commit", "true");//?????????
        //props.put("auto.commit.interval.ms", "1000");//?????????
        //props.put("bootstrap.servers", "172.16.1.121:9092,172.16.1.122:9092,172.16.1.123:9092");
        //props.put("request.timeout.ms", info.getKafkaRequestTimeOut());//?????????
        //props.put("session.timeout.ms", info.getKafkaSessionTimeOut());//?????????
        props.put("bootstrap.servers", info.getKafkaAddress());
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        boolean boo = false;
        ResultVo rs = new ResultVo();
        KafkaConsumer<String, String> consumer = null;
        try {
            consumer = new KafkaConsumer<String, String>(props);
            //Map map = consumer.listTopics();
            Set<String> set = consumer.listTopics().keySet();
            boo = set.contains(info.getKafkaTopic());
            if(boo){
                rs.setErrorNo(0);
                rs.setSuccessMsg("????????????kafka?????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("????????????kafka??????????????????kafka??????:"+info.getKafkaTopic());
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????kafka????????????????????????????????????");
        }finally {
            if(null != consumer){ consumer.close(); }
        }
        return rs;
    }

    /**
     * hbase????????????
     * @param info
     * @return
     */
    @Override
    public ResultVo checkHbase(DataConnectionTestEntity info){
        ResultVo rs = new ResultVo();
        //?????????sql.Connection???????????????
        org.apache.hadoop.hbase.client.Connection conn = null;
        Configuration conf = HBaseConfiguration.create();
        //conf.set("hbase.rootdir", "hdfs://master:9000/hbase");
        conf.set("hbase.zookeeper.quorum", info.getIp());// zookeeper??????
        conf.set("hbase.zookeeper.property.clientPort", info.getPort());// zookeeper??????
        try {
            conn = ConnectionFactory.createConnection(conf);
            Admin admin = conn.getAdmin();
            TableName tableNameObj = TableName.valueOf(info.getTableName());
            //?????????????????????
            if(admin.tableExists(tableNameObj)){
                rs.setErrorNo(0);
                rs.setSuccessMsg("????????????hbase?????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("????????????hbase?????????????????????????????????:"+info.getTableName());
            }
        } catch (IOException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????hbase?????????????????????????????????");
        }finally {
            try {
                if(null != conn){conn.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rs;
    }

    /**
     * es????????????
     * @param info
     * @return
     */
    @Override
    public ResultVo checkElasticSearch(DataConnectionTestEntity info){
        ResultVo rs = new ResultVo();
        Settings settings = Settings.builder().put("cluster.name", info.getEsClusterName())// ???????????????
                .put("client.transport.ignore_cluster_name", true) // ????????????????????????, ??????????????????????????????????????????
                .build();
        TransportClient client = null;
        try {
//          TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(info.getEsIp()),Integer.parseInt(info.getEsPort())));
            client = new PreBuiltTransportClient(settings).addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(info.getIp()), Integer.parseInt(info.getPort())));
            if(null != client){
                rs.setErrorNo(0);
                rs.setSuccessMsg("????????????es?????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("????????????es??????????????????es???????????????");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????es??????????????????es???????????????");
        }finally {
            if(null != client){
                client.close();
            }
        }
        return rs;
    }

    /**
     *mongodb????????????
     */
    @Override
    public ResultVo checkMongodb(DataConnectionTestEntity info){
        ResultVo rs = new ResultVo();
        MongoClient mongoClient =null;
        try {
            // To connect to mongodb server
            mongoClient = new MongoClient(info.getIp(), Integer.parseInt(info.getIp()));
            // Now connect to your databases
            MongoDatabase database = mongoClient.getDatabase(info.getDatabaseName());
            //MongoCollection<Document> collection = database.getCollection("mycol");
            if(null != database){
                rs.setErrorNo(0);
                rs.setSuccessMsg("????????????mongodb?????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("????????????mongodb?????????????????????????????????????????????"+info.getDatabaseName());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????mongodb??????????????????ip?????????");
        }finally {
            if(null != mongoClient){mongoClient.close();}
        }
        return rs;
    }

    /**
     * geode????????????
     * @param info
     * @return
     */
    @Override
    public ResultVo checkGeode(DataConnectionTestEntity info){
        ResultVo rs = new ResultVo();
        Region region = null;
        ClientCache cache = null;
        try {
            cache = new ClientCacheFactory().addPoolLocator(info.getIp(), Integer.parseInt(info.getPort())).create();
            ClientRegionFactory rf = cache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
            //?????????
            region = rf.create(info.getGeodeRegionName());
            //?????????
            Object objList = region.query("select * from /"+info.getGeodeRegionName());
            if(null != objList){
                rs.setErrorNo(0);
                rs.setSuccessMsg("????????????geode?????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("????????????geode????????????????????????????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????geode?????????????????????????????????????????????");
        }finally {
            if(null != cache){
                cache.close();
            }
            if(null != region){
                region.close();
            }
        }
        return rs;
    }

    /**
     * UDP????????????
     * @param info
     * @return
     */
    @Override
    public ResultVo checkUDP(DataConnectionTestEntity info){
        ResultVo rs = new ResultVo();
        DatagramSocket socket = null;
        InetSocketAddress address = null;
        try {
            address = new InetSocketAddress(info.getIp(),Integer.parseInt(info.getPort()));
            socket = new DatagramSocket(address);
            rs.setErrorNo(0);
            rs.setSuccessMsg("????????????UDP?????????");
        } catch (IOException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????UDP??????????????????ip?????????????????????");
        }finally {
            if(null != socket){
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return rs;
    }

    /**
     * TCP????????????
     * @param info
     * @return
     */
    @Override
    public ResultVo checkTCP(DataConnectionTestEntity info){
        Socket socket = new Socket();
        ResultVo rs = new ResultVo();
        try {
            //???????????????socket.connect()
            //socket.connect(new InetSocketAddress(info.getMasterIP(), Integer.parseInt(info.getMasterPort())),5000);
            socket.connect(new InetSocketAddress(info.getClientIP(), Integer.parseInt(info.getClientPort())),5000);
            rs.setErrorNo(0);
            rs.setSuccessMsg("????????????TCP?????????");
        } catch (IOException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????TCP??????????????????Client???????????????????????????");
        }finally {
            if(null != socket){
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return rs;
    }

    /**
     * http??????
     * @param info
     * @return
     */
    @Override
    public ResultVo checkHttp(DataConnectionTestEntity info){
        ResultVo rs = new ResultVo();
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        HttpGet httpGet = null;
        String requestModel = info.getRequestModel();//???????????????0???get, ,1:post
        String requestParams = info.getRequestParams();
        CloseableHttpResponse response = null;
        Integer statusCode = null;
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000).setConnectTimeout(6000).setConnectionRequestTimeout(6000).build();
            if((Constants.STR_ZERO).equals(requestModel)){
                //get
                String url = "http://"+info.getIp()+":"+info.getPort()+info.getPath()+"/"+info.getRequestParams();
                httpGet = new HttpGet(url);
                httpGet.setConfig(requestConfig);
                response = httpClient.execute(httpGet);
                statusCode = response.getStatusLine().getStatusCode();
            }else if((Constants.STR_ONE).equals(requestModel)){
                //post
                String url = "http://"+info.getIp()+":"+info.getPort()+info.getPath();
                httpPost = new HttpPost(url);
                httpPost.setConfig(requestConfig);
                httpPost.setEntity(new StringEntity(requestParams));
                response = httpClient.execute(httpPost);
                statusCode = response.getStatusLine().getStatusCode();
            }
            if(statusCode == 200){
                rs.setErrorNo(0);
                rs.setSuccessMsg("????????????http?????????");
            }else{
                rs.setErrorNo(-1);
                rs.setErrorMsg("????????????http??????????????????path????????????"+info.getPath());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????http??????????????????ip?????????????????????");
        } catch (IOException e) {
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("????????????http??????????????????ip?????????????????????");
        }finally{
            try {
                if(httpPost != null){
                    httpPost.releaseConnection();
                }
                if(httpGet != null){
                    httpGet.releaseConnection();
                }
                if(httpClient != null){
                    httpClient.close();
                }
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rs;
    }

}
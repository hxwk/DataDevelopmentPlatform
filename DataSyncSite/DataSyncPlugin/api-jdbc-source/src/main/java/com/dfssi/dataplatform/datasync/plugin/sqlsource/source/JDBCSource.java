/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.dfssi.dataplatform.datasync.plugin.sqlsource.source;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.common.DatabaseType;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.common.FileUtils;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.common.KafkaHeader;
import com.dfssi.dataplatform.datasync.plugin.sqlsource.metrics.SqlSourceCounter;
import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.flume.agent.Event;
import com.dfssi.dataplatform.datasync.flume.agent.EventDeliveryException;
import com.dfssi.dataplatform.datasync.flume.agent.PollableSource;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.event.SimpleEvent;
import com.dfssi.dataplatform.datasync.flume.agent.source.AbstractSource;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.dfssi.dataplatform.datasync.plugin.sqlsource.common.Constants.*;
import static com.dfssi.dataplatform.datasync.plugin.sqlsource.common.SqlSourceParamConstants.*;


/**
 * A Source to read data from a SQL database. This source ask for new data in a table each configured time.<p>
 * @author Marcelo Valle JianKang
 * @modify time 20171211
 */
public class JDBCSource extends AbstractSource implements Configurable, PollableSource {
    private static final Logger logger = LoggerFactory.getLogger(JDBCSource.class);

    private static final String osProperty = "os.name";
    private static final String os_windows = "windows";
    private static final String FILESEPARATE = System.getProperty("file.separator");
    private static final String NAMESPACE = "dataplatform" + FILESEPARATE + "jdbcsource";
    private static final String USER_HOME ="user.home";
    private static String DEFAULT_STATUS_DIRECTORY= StringUtils.EMPTY ;
    private static String DEFAULT_STATUS_FILE = StringUtils.EMPTY;
    private static String taskid;


    protected SQLSourceHelper sqlSourceHelper;
    private SqlSourceCounter sqlSourceCounter;
    private CSVWriter csvWriter;
    private HibernateHelper hibernateHelper;


    /**
     * Configure the source, load configuration properties and establish connection with database
     */
    @Override
    public void configure(Context context) {
        logger.getName();

        context.put("status.file.path",getUserHomePath());

        taskid = context.getString("taskId");
        DEFAULT_STATUS_FILE += taskid;

        context.put("status.file.name",DEFAULT_STATUS_FILE);

        String jdbcUrl = context.getString("jdbcUrl");
        String databaseUsername = context.getString("username");
        String databasePassword = context.getString("password");
        String tableName = context.getString("tableName");
        boolean isFullExtract = context.getInteger("isFullExtract",0)>0?true:false;
        String autocommit = context.getString("autocommit");

        if(StringUtils.isNotEmpty(jdbcUrl)){
            context.put("hibernate.connection.url",jdbcUrl);
        }

        if(StringUtils.isNotEmpty(tableName)){
            context.put("table",tableName);
        }

        if(StringUtils.isNotEmpty(databaseUsername)){
            context.put("hibernate.connection.username",databaseUsername);
        }

        if(StringUtils.isNotEmpty(databasePassword)){
            context.put("hibernate.connection.password",databasePassword);
        }

        if(StringUtils.isNotEmpty(autocommit)){
            context.put("hibernate.connection.autocommit","true");
        }

        String url = context.getString("hibernate.connection.url");
        if(StringUtils.isNotEmpty(url)) {
            if (url.toLowerCase().contains(DatabaseType.MYSQL.name().toLowerCase())) {
                context.put("hibernate.connection.driver_class", MYSQL_CONN_DRIVER_CLASS);
                context.put("hibernate.dialect", MYSQL_DIALECT);

            } else if (url.toLowerCase().contains(DatabaseType.ORACLE.name().toLowerCase())) {
                context.put("hibernate.connection.driver_class", ORCL_CONN_DRIVER_CLASS);
                context.put("hibernate.dialect", ORCL_DIALECT);
            }
        }else if(StringUtils.isEmpty(url)){
            String dbType = context.getString("dbname"); //dbname是前端传过来的数据库类型名称比如mysql,oracle等
            if(StringUtils.isNotEmpty(dbType) && dbType.equalsIgnoreCase(DatabaseType.MYSQL.name())){
                context.put("hibernate.connection.driver_class", MYSQL_CONN_DRIVER_CLASS);
                context.put("hibernate.dialect", MYSQL_DIALECT);
            }else if(StringUtils.isNotEmpty(dbType) && dbType.equalsIgnoreCase(DatabaseType.ORACLE.name())){
                context.put("hibernate.connection.driver_class", ORCL_CONN_DRIVER_CLASS);
                context.put("hibernate.dialect", ORCL_DIALECT);
            }
          //  String conn_url = String.format("jdbc:%s://%s:%s/%s",dbType,ip,port,databaseName);
           // context.put("hibernate.connection.url",conn_url);
        }else{
            logger.warn("url or dbType is neither null");
        }

        context.put("hibernate.connection.provider_class",CONN_PROVIDER_CLASS);
        context.put("hibernate.temp.use_jdbc_metadata_defaults","false");

        /**
         * 将传过来的JSon格式转换成String
         */
        //context.put("columns.to.select",UtilTools.list2String(JSONUtil.fromJson(context.getString("columns"),new TypeToken<List<String>>(){})));
        String selectColumns = context.getString("columns");
        if(StringUtils.isNotEmpty(selectColumns)) {
            context.put("columns.to.select", selectColumns);
        }else{
            context.put("columns.to.select", Constants.ASTERISK);
        }

    	logger.info("Reading and processing configuration values for source " + getName());
		
    	/* Initialize configuration parameters */
    	sqlSourceHelper = new SQLSourceHelper(context, this.getName());
        
    	/* Initialize metric counters */
		sqlSourceCounter = new SqlSourceCounter("SOURCESQL." + this.getName());
        /* Establish connection with database */
        hibernateHelper = new HibernateHelper(sqlSourceHelper);
        hibernateHelper.establishSession();
       
        /* Instantiate the CSV Writer */
        csvWriter = new CSVWriter(new ChannelWriter(),sqlSourceHelper.getDelimiterEntry().charAt(0));
        
    }

    /**
     * get user home file path by task id
     * @return path
     */
    public static String getUserHomePath(){
        DEFAULT_STATUS_DIRECTORY = System.getProperty(USER_HOME);
        DEFAULT_STATUS_DIRECTORY +=FILESEPARATE;
        DEFAULT_STATUS_DIRECTORY +=NAMESPACE;
        DEFAULT_STATUS_DIRECTORY +=FILESEPARATE;
        logger.info("current task status directory is "+DEFAULT_STATUS_DIRECTORY);
        FileUtils.judeDirExists(new File(DEFAULT_STATUS_DIRECTORY));
        return DEFAULT_STATUS_DIRECTORY;
    }

    /**
     * get status file
     * @return
     */
    private static String getStatusFile(){
        DEFAULT_STATUS_FILE += "_status.log";
        FileUtils.judeFileExists(new File(DEFAULT_STATUS_DIRECTORY+DEFAULT_STATUS_FILE));
        logger.info("status log file "+DEFAULT_STATUS_DIRECTORY+DEFAULT_STATUS_FILE);
        return DEFAULT_STATUS_FILE;
    }
    
    /**
     * Process a batch of events performing SQL Queries
     */
	@Override
	public Status process() throws EventDeliveryException {
		
		try {
			sqlSourceCounter.startProcess();
            KafkaHeader kafkaHeader = new KafkaHeader();
            kafkaHeader.setTaskId(taskid);
            kafkaHeader.setTopic(JDBCTOPIC);
            kafkaHeader.setSourceType(JDBC_SOURCE);
			//List<List<Object>> result = hibernateHelper.executeQuery();
			List<Map<String,Object>> result = hibernateHelper.executeQuery();
            Event event;
            List<Event> eventList = Lists.newArrayList();
            for(Map<String,Object> entity : result) {
                event = new SimpleEvent();
                event.setBody(JSON.toJSONBytes(entity));
                logger.debug("process() entity:{}",entity);
                event.getHeaders().put(HEADER_KEY,JSON.toJSONString(kafkaHeader));
                eventList.add(event);
            }
            this.getChannelProcessor().processEventBatch(eventList);
			/*if (!result.isEmpty())
			{
				csvWriter.writeAll(sqlSourceHelper.getAllRows(result),sqlSourceHelper.encloseByQuotes());
				csvWriter.flush();
				sqlSourceCounter.incrementEventCount(result.size());
				
				sqlSourceHelper.updateStatusFile();
			}
			
			sqlSourceCounter.endProcess(result.size());
			
			if (result.size() < sqlSourceHelper.getMaxRows()){
				Thread.sleep(sqlSourceHelper.getRunQueryDelay());
			}*/
						
			return Status.READY;
			
		} catch (InterruptedException e) {
			logger.error("Error procesing row", e);
			return Status.BACKOFF;
		}
	}
//todo
    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }
    //todo
    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    /**
	 * Starts the source. Starts the metrics counter.
	 */
	@Override
    public void start() {
        
    	logger.info("Starting sql source {} ...", getName());
        sqlSourceCounter.start();
        super.start();
    }

	/**
	 * Stop the source. Close database connection and stop metrics counter.
	 */
    @Override
    public void stop() {
        logger.info("Stopping sql source {} ...", getName());
        
        try 
        {
            hibernateHelper.closeSession();
            csvWriter.close();    
        } catch (IOException e) {
        	logger.warn("Error CSVWriter object ", e);
        } finally {
        	this.sqlSourceCounter.stop();
        	super.stop();
        }
    }
    
    private class ChannelWriter extends Writer{
        private List<Event> events = Lists.newArrayList();

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            Event event = new SimpleEvent();
            
            String s =  new String(cbuf);
            event.setBody(s.substring(off, len-1).getBytes(Charset.forName("UTF-8")));

            Map<String, String> headers;
            headers = Maps.newHashMap();
			headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
			event.setHeaders(headers);
			
            events.add(event);
            
            if (events.size() >= sqlSourceHelper.getBatchSize()){
                flush();
            }
        }

        @Override
        public void flush() throws IOException {

            getChannelProcessor().processEventBatch(events);
            events.clear();
        }

        @Override
        public void close() throws IOException {
            flush();
        }
    }
}

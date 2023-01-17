/*
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
 */
package com.dfssi.dataplatform.datasync.plugin.sqlsource.source;

import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.google.common.collect.Lists;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Helper class to manage hibernate sessions and perform queries
 * 
 * @author <a href="mailto:mvalle@keedio.com">Marcelo Valle</a>
 *
 */
public class HibernateHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(HibernateHelper.class);

	private static SessionFactory factory;
	private Session session;
	private ServiceRegistry serviceRegistry;
	private Configuration config;
	private SQLSourceHelper sqlSourceHelper;

	/**
	 * Constructor to initialize hibernate configuration parameters
	 * @param sqlSourceHelper Contains the configuration parameters from flume config file
	 */
	public HibernateHelper(SQLSourceHelper sqlSourceHelper) {

		this.sqlSourceHelper = sqlSourceHelper;
		Context context = sqlSourceHelper.getContext();
		
		Map<String,String> hibernateProperties = context.getSubProperties("hibernate.");
		logger.info("-----"+hibernateProperties);
		Iterator<Map.Entry<String,String>> it = hibernateProperties.entrySet().iterator();
		
		config = new Configuration();
		Map.Entry<String, String> e;
		
		while (it.hasNext()){
			e = it.next();
			config.setProperty("hibernate." + e.getKey(), e.getValue());
		}


	}

	/**
	 * Connect to database using hibernate
	 */
	public void establishSession() {

		logger.info("Opening hibernate session");

		serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(config.getProperties()).build();

		factory = config.buildSessionFactory(serviceRegistry);
		session = factory.openSession();
		session.setCacheMode(CacheMode.IGNORE);
		
		session.setDefaultReadOnly(sqlSourceHelper.isReadOnlySession());
	}

	/**
	 * Close database connection
	 */
	public void closeSession() {

		logger.info("Closing hibernate session");

		session.close();
		factory.close();
	}

	/**
	 * Execute the selection query in the database
	 * @return The query result. Each Object is a cell content. <p>
	 * The cell contents use database types (date,int,string...), 
	 * keep in mind in case of future conversions/castings.
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> executeQuery() throws InterruptedException {

		//List<List<Object>> rowsList = Lists.newArrayList();
		List<Map<String,Object>> rowsList = Lists.newArrayList();
		Query query;
		
		if (!session.isConnected()){
			resetConnection();
		}
				
		if (!sqlSourceHelper.isCustomQuerySet()){
			
			query = session.createSQLQuery(sqlSourceHelper.buildQuery());
			
			if (sqlSourceHelper.getExtractNumber() != 0){
				query = query.setMaxResults(sqlSourceHelper.getExtractNumber());
			}			
		}
		else
		{
			query = session
					.createSQLQuery(sqlSourceHelper.getQuery())
					.setFirstResult(Integer.parseInt(sqlSourceHelper.getCurrentIndex()));

			if (sqlSourceHelper.getMaxRows() != 0){
				query = query.setMaxResults(sqlSourceHelper.getMaxRows());
			}
		}
		
		try {
			//rowsList = query.setFetchSize(sqlSourceHelper.getMaxRows()).setResultTransformer(Transformers.TO_LIST).list();
			rowsList = query.setFetchSize(20).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

		}catch (Exception e){
			logger.error("Exception thrown, resetting connection.",e);
			resetConnection();
		}
		
		if (!rowsList.isEmpty()){
			if (sqlSourceHelper.isCustomQuerySet()){
					sqlSourceHelper.setCurrentIndex(rowsList.get(rowsList.size()-1).get(0).toString());
			}
			else
			{
				//sqlSourceHelper.setCurrentIndex(Integer.toString((Integer.parseInt(sqlSourceHelper.getCurrentIndex())
				//		+ rowsList.size())));
			}
		}
		
		return rowsList;
	}

	private void resetConnection() throws InterruptedException{
		session.close();
		factory.close();
		establishSession();
	}
}

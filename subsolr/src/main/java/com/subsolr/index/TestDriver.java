/**
 * 
 */
package com.subsolr.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.subsolr.contextprocessor.model.DocumentDefinition;

/**
 * @author aditya
 * 
 */
public class TestDriver {
	public static final Logger logger = LoggerFactory.getLogger(TestDriver.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		logger.info("Starting SolrLite Indexing");
		 new ClassPathXmlApplicationContext("classpath:/application-Context.xml");
		logger.info("End of  SolrLite Indexing- Time Taken in seconds =  " + (System.currentTimeMillis()- startTime)/1000.0);

	}

}

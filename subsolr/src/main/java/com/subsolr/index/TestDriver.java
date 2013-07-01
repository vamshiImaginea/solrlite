/**
 * 
 */
package com.subsolr.index;

import java.util.HashMap;
import java.util.Map;

import com.subsolr.documentprocessors.DocumentProcessor;

/**
 * @author aditya
 * 
 */
public class TestDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Map<String, IndexConfiguration> propertyMap = new HashMap<String, IndexConfiguration>();
		for (String indexName : propertyMap.keySet()) {
			IndexBuilder.createIndex(indexName, propertyMap.get(indexName));
		}

	}

}

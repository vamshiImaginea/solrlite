package com.subsolr.contextprocessors;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.subsolr.contextprocessors.model.FieldDefinition;

/**
 * Reads the FieldContext and generates the FieldDefinition list and the Domain
 * Fields list
 * 
 * @author aditya
 * 
 */
public class FieldContextProcessor {
	private static String confFile = "FieldContext.xml";
	private static Map<String, FieldDefinition> fieldDefinitions;

	public static Map<Field, FieldDefinition> getAllFields() {
		// code here to return the list of available field definitions
		return null;
	}

	// will want to do a get by name .. worry about implementation later.
	public FieldDefinition getFieldDefinition(String fieldName) {
		return fieldDefinitions.get(fieldName);

	}
}

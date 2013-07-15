package com.subsolr.contextprocessors;

import java.util.Map;

import org.apache.lucene.document.Field;

import com.subsolr.contextprocessors.model.FieldTypeDefinition;

/**
 * Reads the FieldContext and generates the FieldDefinition list and the Domain
 * Fields list
 * 
 * @author aditya
 * 
 */
public class FieldContextProcessor {
	private static String confFile = "FieldContext.xml";
	private static Map<String, FieldTypeDefinition> fieldTypeDefinitions;

	public static Map<Field, FieldTypeDefinition> getAllFields() {
		// code here to return the list of available field definitions
		return null;
	}

	
	public FieldTypeDefinition getFieldDefinition(String fieldTypeName) {
		return fieldTypeDefinitions.get(fieldTypeName);

	}
}

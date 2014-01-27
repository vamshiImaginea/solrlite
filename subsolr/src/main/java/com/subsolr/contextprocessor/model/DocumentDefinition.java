package com.subsolr.contextprocessor.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.cqengine.IndexedCollection;
import com.subsolr.entityprocessors.model.Record;
import com.subsolr.util.DynamicIndexer;
import com.subsolr.util.PojoGenerator;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.radixreversed.ReversedRadixTreeIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;

import static com.googlecode.cqengine.query.QueryFactory.*;


/**
 * Contains the possible lucene document definitions using field name -- source
 * mapping
 * 
 * @author aditya
 * 
 */
public class DocumentDefinition {
	private String documentName;
	private Map<String, FieldSetDefinition> fieldSets;
	LinkedHashMap<String, String> mappingRules;
	;

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public Map<String, FieldSetDefinition> getFieldSets() {
		return fieldSets;
	}

	public void setFieldSets(Map<String, FieldSetDefinition> map) {
		this.fieldSets = map;
	}

	public List<Record> getRecordsToBeIndexed() throws NotFoundException, CannotCompileException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, List<Record>> recordsByFieldSet = Maps.newHashMap();
		for (Map.Entry<String, FieldSetDefinition> fieldSetEntry : fieldSets.entrySet()) {
			recordsByFieldSet.put(fieldSetEntry.getKey(), fieldSetEntry.getValue().getEntityProcessor().getRecords(fieldSetEntry.getValue()));
		}

		return combinedFieldSets(recordsByFieldSet);

	}
	@SuppressWarnings({"rawtypes","unchecked"})
	private List<Record> combinedFieldSets(Map<String, List<Record>> recordsByFieldSet) throws NotFoundException, CannotCompileException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (Map.Entry<String, String> mappingRuleEntry : mappingRules.entrySet()) {
			String mappingRule = mappingRuleEntry.getValue();
			final String[] fieldSetConditions = mappingRule.split("=");
			String[] fieldSetsInMapping = fieldSetConditions[0].split(".");
			FieldSetDefinition fieldSetDefinitionOpLeft = fieldSets.get(fieldSetConditions[0].split("#")[0].trim());
			FieldSetDefinition fieldSetDefinitionOpRight = fieldSets.get(fieldSetConditions[1].split("#")[0].trim());

			
			Class fieldsetDataClassLeft = PojoGenerator.generate(fieldSetDefinitionOpLeft.getName(), fieldSetDefinitionOpLeft.getFieldNameToEntityNameMap().keySet());
			Class fieldsetDataClassRight = PojoGenerator.generate(fieldSetDefinitionOpRight.getName(), fieldSetDefinitionOpRight.getFieldNameToEntityNameMap().keySet());

			List<Record> recordsOfLeftOp = recordsByFieldSet.get(fieldSetDefinitionOpLeft.getName());
			List fieldsetDataDataList = populateData(fieldsetDataClassLeft, recordsOfLeftOp);

			Map fieldsetDataAtributesLeft = DynamicIndexer.generateAttributesForPojo(fieldsetDataClassLeft);
			Map fieldsetDataAtributesRight = DynamicIndexer.generateAttributesForPojo(fieldsetDataClassRight);
			IndexedCollection autoIndexedCollectionLeft = DynamicIndexer.newAutoIndexedCollection(fieldsetDataAtributesLeft.values());
			autoIndexedCollectionLeft.addAll(fieldsetDataDataList);
			

			IndexedCollection autoIndexedCollectionRight = DynamicIndexer.newAutoIndexedCollection(fieldsetDataAtributesRight.values());
			autoIndexedCollectionRight.addAll(populateData(fieldsetDataClassRight, recordsByFieldSet.get(fieldSetDefinitionOpRight.getName())));
			
			 SimpleAttribute<Object, String> simpleAttribute  = new SimpleAttribute<Object, String>(fieldSetConditions[1].split(".")[1]) {
				@Override
				public String getValue(final Object object) {
					try {
						return (String) object.getClass().getMethod("get"+fieldSetConditions[1].split(".")[1]).invoke(object, null);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}
		    };
			
			for(Record record : recordsOfLeftOp){
				ResultSet resultSet = autoIndexedCollectionRight.retrieve(contains(simpleAttribute,record.getValueByIndexName().get(fieldSetConditions[1].split(".")[1])));
				Iterator iterator = resultSet.iterator();
				while(iterator.hasNext()){
					Object next = iterator.next();
					
				}
			}
			
		}

		return null;
	}
	@SuppressWarnings({"rawtypes","unchecked"})
	private List populateData(Class fieldsetDataClass, List<Record> records) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException {
		List recordsLists = Lists.newArrayList();

		for (Record record : records) {
			record.getValueByIndexName();
			Object obj = fieldsetDataClass.newInstance();
			for (Map.Entry<String, String> recordEntry : record.getValueByIndexName().entrySet()) {
				fieldsetDataClass.getMethod("set" + recordEntry.getKey(), String.class).invoke(obj, recordEntry.getValue());
			}
			recordsLists.add(obj);

		}

		return recordsLists;

	}

	public void setMappingRules(LinkedHashMap<String, String> mappingRules) {
		this.mappingRules = mappingRules;

	}

}

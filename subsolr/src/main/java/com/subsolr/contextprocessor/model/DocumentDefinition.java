package com.subsolr.contextprocessor.model;

import static com.googlecode.cqengine.query.QueryFactory.contains;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.resultset.ResultSet;
import com.subsolr.contextprocessor.FieldContextProcessor;
import com.subsolr.entityprocessors.model.Record;
import com.subsolr.index.IndexBuilder;
import com.subsolr.util.DynamicIndexer;
import com.subsolr.util.PojoGenerator;

/**
 * Pojo for Document definition having fields,fieldsets, feildsetMappings and
 * mapping rules among field sets
 * 
 * 
 * @author vamsiy-mac aditya
 */

public class DocumentDefinition {
	private String documentName;
	private Map<String, FieldSetDefinition> fieldSets;
	LinkedHashMap<String, String> mappingRules;
	Map<String, Set<String>> attributes = Maps.newHashMap(); // for joins
	public static final Logger logger = LoggerFactory.getLogger(DocumentDefinition.class);
	private FieldContextProcessor fieldContextProcessor;

	public DocumentDefinition(String documentName, Map<String, FieldSetDefinition> fieldSets, LinkedHashMap<String, String> mappingRules, FieldContextProcessor fieldContextProcessor) {
		this.documentName = documentName;
		this.fieldSets = fieldSets;
		this.mappingRules = mappingRules;
		this.fieldContextProcessor = fieldContextProcessor;
	}

	public void indexRecordsForDoc(IndexWriter writer) throws NotFoundException, CannotCompileException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		List<Record> records = Lists.newArrayList();
		if (fieldSets.size() == 1) {
			records = fieldSets.get(fieldSets.keySet().iterator().next()).getEntityProcessor().getRecords(fieldSets.get(fieldSets.keySet().iterator().next()));
		} else {
			Map<String, List<Record>> recordsByFieldSet = Maps.newHashMap();
			for (Map.Entry<String, FieldSetDefinition> fieldSetEntry : fieldSets.entrySet()) {
				recordsByFieldSet.put(fieldSetEntry.getKey(), fieldSetEntry.getValue().getEntityProcessor().getRecords(fieldSetEntry.getValue()));
			}
			logger.info("All Field sets data extracted");
			records = combinedFieldSets(recordsByFieldSet);
		}

		indexRecords(records, writer);

	}

	private void indexRecords(List<Record> records, IndexWriter writer) throws IOException, InstantiationException, IllegalAccessException {
		logger.info("Indexing  Records -  ");
		writer.addDocuments(createDocument(records));
		writer.commit();

	}

	public Collection<Document> createDocument(List<Record> records) throws InstantiationException, IllegalAccessException {

		Collection<Document> docs = Collections2.transform(records, new Function<Record, Document>() {

			@Override
			public Document apply(Record record) {
				Document doc = new Document();
				Map<String, String> valueByIndexName = record.getValueByIndexName();
				for (Entry<String, String> fieldDefEntry : valueByIndexName.entrySet()) {
					if (null != fieldDefEntry.getValue()) {
						FieldDefinition fieldDefinition = fieldContextProcessor.getFieldDefinitionsByName(fieldDefEntry.getKey());
						Class<? extends FieldType> fieldTypeClassName = fieldDefinition.getFieldTypeDefinition().getFieldTypeClassName();
						FieldType fieldType = null;
						try {
							fieldType = fieldTypeClassName.newInstance();
						} catch (Exception e) {
							e.printStackTrace();
						}
						List<Analyzer> analyzer = fieldDefinition.getFieldTypeDefinition().getAnalyzer();
						if (analyzer.size() != 0) {
							fieldType.setAnalyzer(analyzer.get(0));
							fieldType.setIsExplicitAnalyzer(true);
						}
						SchemaField schemaField = new SchemaField(fieldDefinition.getFieldName(), fieldType, IndexBuilder.calcProps(fieldDefinition.getFieldName(), fieldType,
								fieldDefinition.getFieldProperties()), "");
						IndexableField field = schemaField.createField(fieldDefEntry.getValue(), 1.0f);
						doc.add(field);
					}
				}
				return doc;
			}
		});
		return docs;

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Record> combinedFieldSets(Map<String, List<Record>> recordsByFieldSet) throws NotFoundException, CannotCompileException, IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Record> records = Lists.newArrayList();
		for (Map.Entry<String, String> mappingRuleEntry : mappingRules.entrySet()) {
			logger.info("processing mapping rule - name : " + mappingRuleEntry.getKey());
			records = Lists.newArrayList();
			String mappingRule = mappingRuleEntry.getValue();
			attributes.put(mappingRuleEntry.getKey(), Sets.<String> newHashSet());
			final String[] fieldSetConditions = mappingRule.split("=");
			Class fieldsetDataClassRight = generateClassFile(fieldSetConditions[1].split("#")[0].trim());

			List<Record> recordsOfLeftOp = getRecords(recordsByFieldSet, fieldSetConditions[0].split("#")[0].trim());
			List<Record> recordsOfRightOp = getRecords(recordsByFieldSet, fieldSetConditions[1].split("#")[0].trim());
			Collection fieldsetDataRight = populateData(fieldsetDataClassRight, recordsOfRightOp);
			Map fieldsetDataAtributesRight = DynamicIndexer.generateAttributesForPojo(fieldsetDataClassRight);

			IndexedCollection autoIndexedCollectionRight = DynamicIndexer.newAutoIndexedCollection(fieldsetDataAtributesRight.values());
			autoIndexedCollectionRight.addAll(fieldsetDataRight);
			logger.info("indexed Collection created : ");

			SimpleAttribute<Object, String> simpleAttribute = new SimpleAttribute<Object, String>(fieldSetConditions[1].split(".")[1].trim()) {
				@Override
				public String getValue(final Object object) {
					try {
						return (String) object.getClass().getMethod("get" + fieldSetConditions[1].split("#")[1].trim()).invoke(object, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			};

			getCominedRecords(records, mappingRuleEntry, fieldSetConditions, recordsOfLeftOp, autoIndexedCollectionRight, simpleAttribute);

			recordsByFieldSet.put(mappingRuleEntry.getKey(), records);

		}

		return records;
	}

	private void getCominedRecords(List<Record> records, Map.Entry<String, String> mappingRuleEntry, final String[] fieldSetConditions, List<Record> recordsOfLeftOp,
			final IndexedCollection autoIndexedCollectionRight, SimpleAttribute<Object, String> simpleAttribute) {
		int i = 0;
		for (Record record : recordsOfLeftOp) {
			logger.debug("record  : " + i++);
			ResultSet resultSet = autoIndexedCollectionRight.retrieve(contains(simpleAttribute, record.getValueByIndexName().get(fieldSetConditions[1].split("#")[1].trim())));
			if (resultSet.isNotEmpty()) {
				Iterator iterator = resultSet.iterator();
				while (iterator.hasNext()) {
					Object next = iterator.next();
					createCombinedRecord(records, record, next, fieldSetConditions[1].split("#")[0].trim(), mappingRuleEntry.getKey());
				}
			} else {
				records.add(record);
			}
		}
	}

	private List<Record> getRecords(Map<String, List<Record>> recordsByFieldSet, final String fieldSetCondition) {
		FieldSetDefinition fieldSetDefinition = fieldSets.get(fieldSetCondition);
		List<Record> records = null;
		if (null != fieldSetDefinition) {
			records = recordsByFieldSet.get(fieldSetDefinition.getName());
		} else {
			records = recordsByFieldSet.get(fieldSetCondition);
		}

		return records;
	}

	private Class generateClassFile(String fieldSetName) throws NotFoundException, CannotCompileException {
		String className = null;
		Set<String> attributeSet = null;

		FieldSetDefinition fieldSetDefinition = fieldSets.get(fieldSetName);
		if (null != fieldSetDefinition) {
			className = fieldSetDefinition.getName();
			attributeSet = fieldSetDefinition.getFieldNameToEntityNameMap().keySet();
		} else {
			className = fieldSetName;
			attributeSet = attributes.get(fieldSetName);
		}

		return PojoGenerator.generate(className, attributeSet);
	}

	private void createCombinedRecord(List<Record> records, Record record, Object next, String string, String mappingName) {

		Map<String, String> map = Maps.newHashMap();
		map.putAll(record.getValueByIndexName());
		for (String field : fieldSets.get(string).getFieldNameToEntityNameMap().keySet()) {
			String value = null;
			try {
				value = (String) next.getClass().getMethod("get" + field).invoke(next, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put(field, value);

		}
		attributes.get(mappingName).addAll(map.keySet());

		records.add(new Record(map));

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection populateData(final Class fieldsetDataClass, List<Record> records) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException {
		Collection recordsLists = Collections2.transform(records, new Function<Record, Object>() {

			@Override
			public Object apply(Record record) {
				Object obj = null;
				try {
					obj = fieldsetDataClass.newInstance();
					for (Map.Entry<String, String> recordEntry : record.getValueByIndexName().entrySet()) {
						fieldsetDataClass.getMethod("set" + recordEntry.getKey(), String.class).invoke(obj, recordEntry.getValue());

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				return obj;
			}
		});
		return recordsLists;

	}

}

package com.subsolr.index;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;
import org.springframework.beans.factory.InitializingBean;

import com.subsolr.contextprocessor.DocumentContextProcessor;
import com.subsolr.contextprocessor.model.DocumentDefinition;
import com.subsolr.contextprocessor.model.FieldDefinition;
import com.subsolr.entityprocessors.model.Record;

public class IndexBuilder implements InitializingBean {

	private static final Version LUCENE_VERSION = Version.valueOf("LUCENE_43");
	private String luceneDirectory = null;
	private DocumentContextProcessor documentContextProcessor = null;

	public IndexBuilder(String luceneDirectory, DocumentContextProcessor documentContextProcessor) {
		this.luceneDirectory = luceneDirectory;
		this.documentContextProcessor = documentContextProcessor;
	}

	public void indexRecordsForDocument(List<Record> recordLists, String documentName) throws IOException, IllegalArgumentException, InstantiationException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {
		IndexWriter writer = getIndexWriterForDocument(false, documentName);

		for (Record record : recordLists) {
			Document doc = new Document();
			Map<FieldDefinition, String> valueByIndexName = record.getValueByIndexName();
			for (Entry<FieldDefinition, String> fieldDefEntry : valueByIndexName.entrySet()) {
				FieldDefinition fieldDefinition = fieldDefEntry.getKey();
				Class<? extends FieldType> fieldTypeClassName = fieldDefinition.getFieldTypeDefinition().getFieldTypeClassName();
				FieldType fieldType = fieldTypeClassName.newInstance();
				fieldType.setAnalyzer(fieldDefinition.getFieldTypeDefinition().getAnalyzer());
				//fieldType.setSimilarity(fieldDefinition.getFieldTypeDefinition().getSimilarityClassName());//TODO Similarity
				SchemaField schemaField = new SchemaField(fieldDefinition.getFieldName(), fieldType);
				IndexableField field = schemaField.createField(fieldDefEntry.getValue(),1.0f);
				doc.add(field);

			}
			writer.addDocument(doc);

		}
		writer.close();

	}

	
	
	
	private IndexWriter getIndexWriterForDocument(boolean b, String documentName) throws IOException {
		
		StandardAnalyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
		Directory index = FSDirectory.open(new File(luceneDirectory+File.pathSeparator+documentName));
		IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
		IndexWriter indexWriter = new IndexWriter(index, config);
		
		return indexWriter;
	}

	public void rebuildIndexes() throws IOException {}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, DocumentDefinition> documentDefinitions = documentContextProcessor.getDocumentDefinitions();
		for (Entry<String, DocumentDefinition> documentEntryDefinition : documentDefinitions.entrySet()) {
			String doucmentName = documentEntryDefinition.getKey();
			DocumentDefinition documentDefinition = documentEntryDefinition.getValue();
			List<Record> recordsToBeIndexed = documentDefinition.getRecordsToBeIndexedd();
			indexRecordsForDocument(recordsToBeIndexed, doucmentName);

		}

	}

}

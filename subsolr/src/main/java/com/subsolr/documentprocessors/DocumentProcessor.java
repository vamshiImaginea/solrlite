package com.subsolr.documentprocessors;

import java.util.List;

import org.apache.lucene.document.Document;

import com.subsolr.contextprocessor.model.DocumentDefinition;

public interface DocumentProcessor {

	List<Document> generateDocuments(DocumentDefinition documentDefinition);

}

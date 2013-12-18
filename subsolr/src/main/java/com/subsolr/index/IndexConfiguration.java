package com.subsolr.index;

import com.subsolr.contextprocessor.model.DocumentDefinition;

/**
 * Class that holds index configuration details like name , directorypath,
 * document definitions etc.
 * 
 * @author aditya
 * 
 */
public class IndexConfiguration {
	//index properties here .. 
	private DocumentDefinition documentDefinition;

	public DocumentDefinition getDocumentDefinition() {
		return documentDefinition;
	}

	public void setDocumentDefinition(DocumentDefinition documentDefinition) {
		this.documentDefinition = documentDefinition;
	}
}

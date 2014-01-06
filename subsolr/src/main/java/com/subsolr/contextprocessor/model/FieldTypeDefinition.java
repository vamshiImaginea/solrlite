package com.subsolr.contextprocessor.model;

import org.apache.lucene.document.Field;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.analysis.SolrAnalyzer;
import org.apache.solr.schema.FieldType;


public class FieldTypeDefinition {
	private String name;
	private Class<?extends FieldType> fieldTypeClassName;
	private Integer positionIncrementGap;
	private Similarity similarityClassName;
	private SolrAnalyzer analyzer;
   /**
    * @return the name
    */
   public String getName() {
      return name;
   }
   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }
  
   /**
    * @return the positionIncrementGap
    */
   public Integer getPositionIncrementGap() {
      return positionIncrementGap;
   }
   /**
    * @param positionIncrementGap the positionIncrementGap to set
    */
   public void setPositionIncrementGap(Integer positionIncrementGap) {
      this.positionIncrementGap = positionIncrementGap;
   }
  
   /**
    * @return the analyzer
    */
   public SolrAnalyzer getAnalyzer() {
      return analyzer;
   }
   /**
    * @param analyzer the analyzer to set
    */
   public void setAnalyzer(SolrAnalyzer analyzer) {
      this.analyzer = analyzer;
   }
   /**
    * @return the similarityClassName
    */
   public Similarity getSimilarityClassName() {
      return similarityClassName;
   }
   /**
    * @param similarityClassName the similarityClassName to set
    */
   public void setSimilarityClassName(Similarity similarityClassName) {
      this.similarityClassName = similarityClassName;
   }
public FieldTypeDefinition(String name, Class<? extends FieldType> fieldTypeClass, Integer positionIncrementGap, Similarity similarityClassName, SolrAnalyzer analyzer) {
	this.name = name;
	this.fieldTypeClassName = fieldTypeClass;
	this.positionIncrementGap = positionIncrementGap;
	this.similarityClassName = similarityClassName;
	this.analyzer = analyzer;
}
public Class<?extends FieldType> getFieldTypeClassName() {
	return fieldTypeClassName;
}
public void setFieldTypeClassName(Class<?extends FieldType> fieldTypeClassName) {
	this.fieldTypeClassName = fieldTypeClassName;
}


	

	
}

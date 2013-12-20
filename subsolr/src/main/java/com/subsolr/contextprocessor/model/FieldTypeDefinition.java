package com.subsolr.contextprocessor.model;

import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.analysis.SolrAnalyzer;
import org.apache.solr.schema.FieldType;


public class FieldTypeDefinition {
	private String name;
	private FieldType fieldTypeClassName;
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
    * @return the fieldTypeClassName
    */
   public FieldType getFieldTypeClassName() {
      return fieldTypeClassName;
   }
   /**
    * @param fieldTypeClassName the fieldTypeClassName to set
    */
   public void setFieldTypeClassName(FieldType fieldTypeClassName) {
      this.fieldTypeClassName = fieldTypeClassName;
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
public FieldTypeDefinition(String name, FieldType fieldTypeClassName, Integer positionIncrementGap, Similarity similarityClassName, SolrAnalyzer analyzer) {
	this.name = name;
	this.fieldTypeClassName = fieldTypeClassName;
	this.positionIncrementGap = positionIncrementGap;
	this.similarityClassName = similarityClassName;
	this.analyzer = analyzer;
}


	

	
}

package com.subsolr.contextprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.subsolr.contextprocessor.model.FieldDefinition;
import com.subsolr.contextprocessor.model.FieldTypeDefinition;
import com.subsolr.utils.DomUtility;

/**
 * Reads the FieldContext and generates the FieldDefinition list and the Domain Fields list
 * 
 * @author aditya
 * 
 */
public class FieldContextProcessor {
   // TODO: set all these through spring context
   private static String confFile = "FieldContext.xml";
   private static Map<String, NodeList> fieldContext;
   private static InputStream fieldContextStream = FieldContextProcessor.class.getResourceAsStream(confFile);
   private static Set<String> nodeNames = new HashSet<String>(Arrays.asList("field_type", "field"));

   // TODO: Field Type Definitions, should these remain in memory or should we load on demand?
   private Set<FieldTypeDefinition> fieldTypeDefinitions;
   private Set<FieldDefinition> fieldDefinitions;
   
   private static void loadFieldContext() throws ParserConfigurationException, SAXException, IOException {
      fieldContext = DomUtility.getNodeMap(fieldContextStream, nodeNames);
      setFieldTypeDefinitions(fieldContext.get("field_type"));
      setFieldDefinitions(fieldContext.get("field"));
   }

   private static void setFieldDefinitions(NodeList nodeList) {
      // TODO Auto-generated method stub
      
   }

   private static void setFieldTypeDefinitions(final NodeList fieldDefinitions) {
   
   }

}

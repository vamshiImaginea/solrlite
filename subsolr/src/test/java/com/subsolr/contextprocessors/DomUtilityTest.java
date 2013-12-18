package com.subsolr.contextprocessors;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subsolr.utils.DomUtility;

public class DomUtilityTest {
   private InputStream fieldContext = DomUtilityTest.class.getResourceAsStream("FieldContext.xml");
   private Set<String> nodeNames = new HashSet<String>(Arrays.asList("field_type", "biz_field_defintion", "field"));

   @Test
   public void testxml() throws Exception {
      Map<String, NodeList> nodeMap = DomUtility.getNodeMap(fieldContext, nodeNames);
      Assert.assertTrue(nodeMap.get("field_type").getLength() == 3);
      Assert.assertTrue(nodeMap.get("field").getLength() == 7);

   }

   @Test
   public void testFieldType() throws Exception {
      Map<String, NodeList> nodeMap = DomUtility.getNodeMap(fieldContext, nodeNames);
      Assert.assertTrue(nodeMap.get("field_type").getLength() == 3);
      for (int i = 0; i < nodeMap.get("field_type").getLength(); i++) {
         Node parent = nodeMap.get("field_type").item(i);
         NamedNodeMap nNmap = parent.getAttributes();

         for (int j = 0; j < nNmap.getLength(); j++)
            System.out.println("parent "+ nNmap.item(j).getNodeName() + " : " + nNmap.item(j).getNodeValue());

         NodeList children = parent.getChildNodes();

         for (int p = 0; p < children.getLength(); p++) {
            Node child = children.item(p);
            System.out.println(child.getNodeName());
            NamedNodeMap childAttrs = child.getAttributes();
            if (childAttrs != null)
               for (int k = 0; k < childAttrs.getLength(); k++) {
                  System.out.println("child "+ child.getNodeName()+childAttrs.item(k).getNodeName() + " : " + childAttrs.item(k).getNodeValue());
               }
         }
         System.out.println("\n");
      }

   }

}

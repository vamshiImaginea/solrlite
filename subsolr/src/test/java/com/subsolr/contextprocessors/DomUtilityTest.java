package com.subsolr.contextprocessors;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subsolr.utils.DomUtility;

public class DomUtilityTest {
	private InputStream fieldContext = DomUtilityTest.class
			.getResourceAsStream("FieldContext.xml");
	private Set<String> nodeNames = new HashSet<String>(Arrays.asList(
			"field_type", "field_definition", "field"));

	@Test
	public void test() throws Exception {
		Map<String, NodeList> nodeMap = DomUtility.getNodeMap(fieldContext,
				nodeNames);
		Assert.assertTrue(nodeMap.get("field_type").getLength() == 1);
		Assert.assertTrue(nodeMap.get("field_definition").getLength() == 1);
		Assert.assertTrue(nodeMap.get("field").getLength() == 7);

		for (int i = 0; i < nodeMap.get("field").getLength(); i++) {
			Node n = nodeMap.get("field").item(i);
			NamedNodeMap nNmap = n.getAttributes();
			for (int j = 0; j < nNmap.getLength(); j++)
				System.out.println(nNmap.item(j).getNodeName() + " : "
						+ nNmap.item(j).getNodeValue());
		}

	}

}

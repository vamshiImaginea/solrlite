package com.subsolr.utils;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.XMLErrorLogger;
import org.apache.solr.core.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XMLUtil {
   public static final Logger log = LoggerFactory.getLogger(Config.class);
   private static final XMLErrorLogger xmllog = new XMLErrorLogger(log);

   private final Document doc;
   private final String prefix;
   private final String name;

   static final XPathFactory xpathFactory = XPathFactory.newInstance();

   /**
    * Builds a config:
    * <p>
    * Note that the 'name' parameter is used to obtain a valid input stream if no valid one is provided through 'is'. If
    * no valid stream is provided, a valid SolrResourceLoader instance should be provided through 'loader' so the
    * resource can be opened (@see SolrResourceLoader#openResource); if no SolrResourceLoader instance is provided, a
    * default one will be created.
    * </p>
    * <p>
    * Consider passing a non-null 'name' parameter in all use-cases since it is used for logging & exception reporting.
    * </p>
    * 
    * @param loader
    *           the resource loader used to obtain an input stream if 'is' is null
    * @param name
    *           the resource name used if the input stream 'is' is null
    * @param is
    *           the resource as a SAX InputSource
    * @param prefix
    *           an optional prefix that will be preprended to all non-absolute xpath expressions
    * @throws javax.xml.parsers.ParserConfigurationException
    * @throws java.io.IOException
    * @throws org.xml.sax.SAXException
    */
   public XMLUtil(String name, InputSource is, String prefix) throws ParserConfigurationException, IOException,
         SAXException {

      this.name = name;
      this.prefix = (prefix != null && !prefix.endsWith("/")) ? prefix + '/' : prefix;
      try {
         javax.xml.parsers.DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         // only enable xinclude, if a SystemId is available
         if (is.getSystemId() != null) {
            try {
               dbf.setXIncludeAware(true);
               dbf.setNamespaceAware(true);
            } catch (UnsupportedOperationException e) {
               log.warn(name + " XML parser doesn't support XInclude option");
            }
         }

         final DocumentBuilder db = dbf.newDocumentBuilder();

         db.setErrorHandler(xmllog);
         try {
            doc = db.parse(is);
         } finally {
            // some XML parsers are broken and don't close the byte stream
            // (but they should according to spec)
            IOUtils.closeQuietly(is.getByteStream());
         }

      } catch (ParserConfigurationException e) {
         SolrException.log(log, "Exception during parsing file: " + name, e);
         throw e;
      } catch (SAXException e) {
         SolrException.log(log, "Exception during parsing file: " + name, e);
         throw e;
      } catch (SolrException e) {
         SolrException.log(log, "Error in " + name, e);
         throw e;
      }
   }

   public XPath getXPath() {
      return xpathFactory.newXPath();
   }

   public Object evaluate(String path, QName type) {
      XPath xpath = xpathFactory.newXPath();
      try {
         String xstr = normalize(path);

         // TODO: instead of prepending /prefix/, we could do the search rooted at /prefix...
         Object o = xpath.evaluate(xstr, doc, type);
         return o;

      } catch (XPathExpressionException e) {
         throw new RuntimeException("Error in xpath evaluation");
      }
   }

   public Node getNode(String path, boolean errIfMissing) {
      XPath xpath = xpathFactory.newXPath();
      Node nd = null;
      String xstr = normalize(path);

      try {
         nd = (Node) xpath.evaluate(xstr, doc, XPathConstants.NODE);

         if (nd == null) {
            if (errIfMissing) {
               throw new RuntimeException(name + " missing " + path);
            } else {
               log.debug(name + " missing optional " + path);
               return null;
            }
         }

         log.trace(name + ":" + path + "=" + nd);
         return nd;

      } catch (XPathExpressionException e) {
         // SolrException.log(log, "Error in xpath", e);
         throw new RuntimeException("Error in xpath:" + xstr + " for " + name, e);
      } catch (SolrException e) {
         throw (e);
      } catch (Throwable e) {
         SolrException.log(log, "Error in xpath", e);
         throw new RuntimeException("Error in xpath:" + xstr + " for " + name, e);
      }

   }

   private String normalize(String path) {
      return (prefix == null || path.startsWith("/")) ? path : prefix + path;
   }

}
